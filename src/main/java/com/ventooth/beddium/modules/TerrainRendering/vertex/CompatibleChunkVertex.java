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

package com.ventooth.beddium.modules.TerrainRendering.vertex;

import com.ventooth.beddium.Compat;
import com.ventooth.beddium.modules.MEGAChunks.MegaChunkMetadata;
import lombok.val;
import org.embeddedt.embeddium.impl.gl.array.GlVertexArray;
import org.embeddedt.embeddium.impl.gl.attribute.GlVertexFormat;
import org.embeddedt.embeddium.impl.gl.tessellation.GlVertexArrayTessellation;
import org.embeddedt.embeddium.impl.gl.tessellation.TessellationBinding;
import org.embeddedt.embeddium.impl.render.chunk.terrain.material.Material;
import org.embeddedt.embeddium.impl.render.chunk.vertex.format.ChunkVertexEncoder;
import org.embeddedt.embeddium.impl.render.chunk.vertex.format.ChunkVertexType;

import net.minecraft.client.renderer.Tessellator;

import java.util.Map;

public sealed interface CompatibleChunkVertex extends ChunkVertexType permits DefaultChunkVertex, RPLEChunkVertex, SwanSongChunkVertex, SwanSongRPLEChunkVertex {
    static CompatibleChunkVertex get() {
        if (Compat.isSwansongInstalled()) {
            if (Compat.rpleInstalled()) {
                return SwanSongRPLEChunkVertex.INSTANCE;
            } else {
                return SwanSongChunkVertex.INSTANCE;
            }
        } else if (Compat.rpleInstalled()) {
            return RPLEChunkVertex.INSTANCE;
        } else {
            return DefaultChunkVertex.INSTANCE;
        }
    }

    @Override
    default float getPositionScale() {
        return 1F;
    }

    @Override
    default float getPositionOffset() {
        return 0F;
    }

    @Override
    default float getTextureScale() {
        return 1F;
    }

    /**
     * @return Stride in {@code byte} for the {@link GlVertexFormat}
     */
    int attribStride();

    /**
     * @return Stride in {@code int} for the {@link Tessellator#rawBuffer}
     */
    int rawBufStride();

    CompatibleChunkVertex.Vertex[] uninitializedQuad();

    default CompatibleChunkVertex.Tessellation getTessellation(GlVertexArray array, TessellationBinding[] bindings) {
        return new CompatibleChunkVertex.Tessellation(array, bindings);
    }

    static int encodeLightRPLE(int light) {
        return encodeLight(vanillaFromTess(light));
    }

    @Override
    default Map<String, String> getDefines() {
        val map = ChunkVertexType.super.getDefines();
        map.put("MEGACHUNK_SIZE", Float.toString(MegaChunkMetadata.BLOCKS_PER_WR_EDGE_FLOAT));
        return map;
    }

    private static int vanillaFromTess(int tess) {
        val lightMapBlock = (short) (tess & 0xFFFF);
        val lightMapSky = (short) ((tess >> 16) & 0xFFFF);

        val blockLight = remapFromShort(lightMapBlock);
        val skyLight = remapFromShort(lightMapSky);

        return blockLight | (skyLight << 16);
    }

    private static int remapFromShort(short n) {
        // Convert back from short range to 0-240 range
        val normalized = (float) (n - Short.MIN_VALUE) / (Short.MAX_VALUE - Short.MIN_VALUE);
        return Math.round(normalized * 240F);
    }

    @SuppressWarnings("PointlessBitwiseExpression")
    static int encodeDrawParameters(Material material, int sectionIndex) {
        return (((sectionIndex & 0xFF) << 8) | ((material.bits() & 0xFF) << 0));
    }

    @SuppressWarnings("PointlessBitwiseExpression")
    static int encodeLight(int light) {
        val block = light & 0xFF;
        val sky = (light >> 16) & 0xFF;
        return ((block << 0) | (sky << 8));
    }

    static float encodeTexture(float value) {
        return Math.min(0.99999997F, value);
    }

    abstract class Vertex extends ChunkVertexEncoder.Vertex {
        public abstract void copyRawBuffer(int[] rawBuffer, int ptr);
    }

    class Tessellation extends GlVertexArrayTessellation {
        public Tessellation(GlVertexArray array, TessellationBinding[] bindings) {
            super(array, bindings);
        }
    }
}
