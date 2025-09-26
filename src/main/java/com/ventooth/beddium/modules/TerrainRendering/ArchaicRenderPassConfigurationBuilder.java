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

package com.ventooth.beddium.modules.TerrainRendering;

import com.google.common.collect.ImmutableListMultimap;
import com.ventooth.beddium.Share;
import com.ventooth.beddium.modules.TerrainRendering.vertex.CompatibleChunkVertex;
import it.unimi.dsi.fastutil.objects.Reference2ReferenceOpenHashMap;
import lombok.val;
import org.embeddedt.embeddium.impl.render.chunk.RenderPassConfiguration;
import org.embeddedt.embeddium.impl.render.chunk.compile.sorting.QuadPrimitiveType;
import org.embeddedt.embeddium.impl.render.chunk.terrain.TerrainRenderPass;
import org.embeddedt.embeddium.impl.render.chunk.terrain.material.Material;
import org.embeddedt.embeddium.impl.render.chunk.terrain.material.parameters.AlphaCutoffParameter;
import org.lwjgl.opengl.GL11;

import java.util.Map;

public class ArchaicRenderPassConfigurationBuilder {
    public static TerrainRenderPass SOLID_PASS, CUTOUT_MIPPED_PASS, TRANSLUCENT_PASS;
    public static Material SOLID_MATERIAL, CUTOUT_MIPPED_MATERIAL, TRANSLUCENT_MATERIAL;

    private record ArchaicPipelineState(int pass, boolean disableBlend) implements TerrainRenderPass.PipelineState {
        @Override
        public void setup() {
            if (pass == 0) {
                // Force alpha test to use 0.1F threshold
                GL11.glAlphaFunc(GL11.GL_GREATER, 0.1F);
            }
            if (disableBlend) {
                GL11.glDisable(GL11.GL_ALPHA_TEST);
            }
        }

        @Override
        public void clear() {
            if (disableBlend) {
                GL11.glEnable(GL11.GL_ALPHA_TEST);
            }
        }
    }

    private static TerrainRenderPass.TerrainRenderPassBuilder builderForRenderType(int pass, boolean disableBlend) {
        return TerrainRenderPass.builder()
                                .pipelineState(new ArchaicPipelineState(pass, disableBlend))
                                .vertexType(CompatibleChunkVertex.get())
                                .primitiveType(QuadPrimitiveType.TRIANGULATED);
    }

    public static RenderPassConfiguration<Integer> build() {
        ImmutableListMultimap.Builder<Integer, TerrainRenderPass> vanillaRenderStages = ImmutableListMultimap.builder();

        SOLID_PASS = builderForRenderType(0, true).name("solid").fragmentDiscard(false).useReverseOrder(false).build();
        CUTOUT_MIPPED_PASS = builderForRenderType(0, false).name("cutout_mipped").fragmentDiscard(true).useReverseOrder(false).build();
        TRANSLUCENT_PASS = builderForRenderType(1, false).name("translucent")
                                                         .fragmentDiscard(false)
                                                         .useReverseOrder(true)
                                                         .useTranslucencySorting(Share.DO_TERRAIN_TRANSLUCENT_SORTING)
                                                         .build();
        TRANSLUCENT_MATERIAL = new Material(TRANSLUCENT_PASS, AlphaCutoffParameter.ZERO, true);
        SOLID_MATERIAL = new Material(SOLID_PASS, AlphaCutoffParameter.ZERO, true);
        CUTOUT_MIPPED_MATERIAL = new Material(CUTOUT_MIPPED_PASS, AlphaCutoffParameter.ONE_TENTH, true);

        vanillaRenderStages.put(1, TRANSLUCENT_PASS);
        vanillaRenderStages.put(0, SOLID_PASS);
        vanillaRenderStages.put(0, CUTOUT_MIPPED_PASS);

        // Now build the material map
        Map<Integer, Material> renderTypeToMaterialMap = new Reference2ReferenceOpenHashMap<>(4, Reference2ReferenceOpenHashMap.VERY_FAST_LOAD_FACTOR);

        renderTypeToMaterialMap.put(0, CUTOUT_MIPPED_MATERIAL);
        renderTypeToMaterialMap.put(1, TRANSLUCENT_MATERIAL);

        var vanillaRenderStageMap = vanillaRenderStages.build();

        return new RenderPassConfiguration<>(renderTypeToMaterialMap, vanillaRenderStageMap.asMap(), CUTOUT_MIPPED_MATERIAL, CUTOUT_MIPPED_MATERIAL, TRANSLUCENT_MATERIAL);
    }
}
