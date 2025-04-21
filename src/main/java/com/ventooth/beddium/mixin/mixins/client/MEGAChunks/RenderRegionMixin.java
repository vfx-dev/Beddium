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
import org.embeddedt.embeddium.impl.render.chunk.region.RenderRegion;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.Constant;
import org.spongepowered.asm.mixin.injection.ModifyConstant;

//Get Big and WIN [W1ld Pr1zes!]
@Mixin(value = RenderRegion.class,
       remap = false)
public abstract class RenderRegionMixin {
    @ModifyConstant(method = {"getOriginX", "getOriginY", "getOriginZ", "getCenterY"},
                    constant = @Constant(intValue = 4),
                    require = 4)
    private int extendChunk1(int constant) {
        return MegaChunkMetadata.BLOCKS_PER_WR_EDGE_BITS;
    }
    @ModifyConstant(method = "getCenterX",
                    constant = @Constant(intValue = 4,
                                         ordinal = 1),
                    require = 1)
    private int extendChunk2(int constant) {
        return MegaChunkMetadata.BLOCKS_PER_WR_EDGE_BITS;
    }
    @ModifyConstant(method = "getCenterZ",
                    constant = @Constant(intValue = 4,
                                         ordinal = 1),
                    require = 1)
    private int extendChunk3(int constant) {
        return MegaChunkMetadata.BLOCKS_PER_WR_EDGE_BITS;
    }
}
