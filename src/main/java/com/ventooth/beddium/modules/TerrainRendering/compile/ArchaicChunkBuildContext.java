/*
 * Beddium
 *
 * Copyright (C) 2025 Ven, FalsePattern
 * All Rights Reserved
 *
 * The above copyright notice and this permission notice shall be included
 * in all copies or substantial portions of the Software.
 *
 * This program is free software: you can redistribute it and/or modify
 * it under the terms of the GNU Lesser General Public License as published by
 * the Free Software Foundation, only version 3 of the License.
 *
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.
 *
 * You should have received a copy of the GNU Lesser General Public License
 * along with this program.  If not, see <https://www.gnu.org/licenses/>.
 */

package com.ventooth.beddium.modules.TerrainRendering.compile;

import com.ventooth.beddium.Share;
import com.ventooth.beddium.config.ModuleConfig;
import com.ventooth.beddium.modules.TerrainRendering.ArchaicRenderPassConfigurationBuilder;
import com.ventooth.beddium.modules.TerrainRendering.vertex.CompatibleChunkVertex;
import com.ventooth.beddium.modules.TerrainRendering.ext.TextureAtlasSpriteExt;
import com.ventooth.beddium.modules.TerrainRendering.ext.TextureMapExt;
import lombok.val;
import org.embeddedt.embeddium.impl.render.chunk.RenderPassConfiguration;
import org.embeddedt.embeddium.impl.render.chunk.compile.ChunkBuildBuffers;
import org.embeddedt.embeddium.impl.render.chunk.compile.ChunkBuildContext;
import org.embeddedt.embeddium.impl.render.chunk.data.MinecraftBuiltRenderSectionData;
import org.embeddedt.embeddium.impl.render.chunk.sprite.SpriteTransparencyLevel;
import org.embeddedt.embeddium.impl.render.chunk.terrain.material.Material;
import org.embeddedt.embeddium.impl.util.QuadUtil;
import org.jetbrains.annotations.Nullable;

import net.minecraft.client.Minecraft;
import net.minecraft.client.multiplayer.WorldClient;
import net.minecraft.client.renderer.texture.TextureAtlasSprite;
import net.minecraft.tileentity.TileEntity;

public class ArchaicChunkBuildContext extends ChunkBuildContext {
    public static final int NUM_PASSES = 2;

    private final CompatibleChunkVertex.Vertex[] vertices = CompatibleChunkVertex.get().uninitializedQuad();
    private final int rawBufStride = CompatibleChunkVertex.get().rawBufStride();

    private final TextureMapExt textureAtlas;

    public ArchaicChunkBuildContext(WorldClient world, RenderPassConfiguration renderPassConfiguration) {
        super(renderPassConfiguration);
        this.textureAtlas = TextureMapExt.of(Minecraft.getMinecraft().getTextureMapBlocks());
    }

    public void copyRawBuffer(int[] rawBuffer, int vertexCount, ChunkBuildBuffers buffers, Material material) {
        if (vertexCount == 0) {
            return;
        }

        val ctxBundle = buffers.getSectionContextBundle();
        //noinspection unchecked
        val sectionData = (MinecraftBuiltRenderSectionData<TextureAtlasSprite, TileEntity>) ctxBundle;
        val animatedSprites = sectionData.animatedSprites;

        // Require vertices in multiples of 4
        if ((vertexCount & 0x3) != 0) {
            val e = new IllegalStateException("Bad vertex count: [" + vertexCount + "] is someone triangulating the quads early?");
            if (ModuleConfig.Debug) {
                Share.log.fatal("Caught would-be runtime crash: ", e);
            } else {
                throw e;
            }
        }

        var ptr = 0;
        val numQuads = vertexCount / 4;
        // TODO: Implement Triangulation here!
        for (var quadIdx = 0; quadIdx < numQuads; quadIdx++) {
            var uSum = 0F;
            var vSum = 0F;

            for (var vertIdx = 0; vertIdx < 4; vertIdx++) {
                var baseVertex = vertices[vertIdx];
                baseVertex.copyRawBuffer(rawBuffer, ptr);
                ptr += rawBufStride;

                uSum += baseVertex.u;
                vSum += baseVertex.v;
            }

            // Track animated sprites in terrain
            val sprite = textureAtlas.celeritas$findFromUV(uSum * 0.25F, vSum * 0.25F);
            if (sprite != null && sprite.hasAnimationMetadata()) {
                animatedSprites.add(sprite);
            }

            // Calculate position-based normals
            val trueNormal = QuadUtil.calculateNormal(vertices);
            for (var vertIdx = 0; vertIdx < 4; vertIdx++) {
                vertices[vertIdx].trueNormal = trueNormal;
            }
            val facing = QuadUtil.findNormalFace(trueNormal);

            val correctMaterial = selectMaterial(material, sprite);
            buffers.get(correctMaterial)
                   .getVertexBuffer(facing)
                   .push(vertices, correctMaterial);
        }
    }

    // TODO: Can we do more with downgrading? Say Translucent-Opaque all the way?
    private static Material selectMaterial(Material baseMaterial, @Nullable TextureAtlasSprite sprite) {
        // We only select a different material if we have a sprite with no animations directly from the atlas
        if (sprite == null || sprite.getClass() != TextureAtlasSprite.class || sprite.hasAnimationMetadata()) {
            return baseMaterial;
        }

        val transparencyLevel = TextureAtlasSpriteExt.of(sprite).celeritas$transparencyLevel();
        if (baseMaterial == ArchaicRenderPassConfigurationBuilder.CUTOUT_MIPPED_MATERIAL) {
            if (transparencyLevel == SpriteTransparencyLevel.OPAQUE) {
                // Downgrade opaque sprites (no cutout) to solid
                return ArchaicRenderPassConfigurationBuilder.SOLID_MATERIAL;
            }
        } else if (baseMaterial == ArchaicRenderPassConfigurationBuilder.TRANSLUCENT_MATERIAL) {
            if (transparencyLevel != SpriteTransparencyLevel.TRANSLUCENT) {
                // Downgrade non-translucent sprites to cutout
                return ArchaicRenderPassConfigurationBuilder.CUTOUT_MIPPED_MATERIAL;
            }
        }
        return baseMaterial;
    }
}
