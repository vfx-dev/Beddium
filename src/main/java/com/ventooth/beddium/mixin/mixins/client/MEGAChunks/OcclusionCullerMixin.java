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

package com.ventooth.beddium.mixin.mixins.client.MEGAChunks;

import com.ventooth.beddium.modules.MEGAChunks.MegaChunkMetadata;
import org.embeddedt.embeddium.impl.render.chunk.occlusion.OcclusionCuller;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.Constant;
import org.spongepowered.asm.mixin.injection.ModifyConstant;

//DON'T FORGET TO [Like and Subscribe] FOR MORE [Hyperlink Blocked]!
@Mixin(value = OcclusionCuller.class,
       remap = false)
public class OcclusionCullerMixin {
    @ModifyConstant(method = "isWithinRenderDistance",
                    constant = @Constant(intValue = 16),
                    require = 3)
    private static int extendChunk1(int constant) {
        return MegaChunkMetadata.BLOCKS_PER_WR_EDGE;
    }

    @ModifyConstant(method = "initOutsideWorldHeight",
                    constant = @Constant(floatValue = 16.0f),
                    require = 1)
    private float extendChunk2(float constant) {
        return MegaChunkMetadata.BLOCKS_PER_WR_EDGE;
    }

    @ModifyConstant(method = "isWithinFrustum",
                    constant = @Constant(floatValue = 9.125f),
                    require = 1)
    private static float extendChunk3(float constant) {
        return (MegaChunkMetadata.BLOCKS_PER_WR_EDGE_FLOAT / 2) + 1.0f + 0.125f;
    }
}
