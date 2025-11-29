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
import org.embeddedt.embeddium.impl.render.chunk.AbstractSection;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.Constant;
import org.spongepowered.asm.mixin.injection.ModifyConstant;

//HAEAHAEAHAEAHAEAH!!
@Mixin(value = AbstractSection.class,
       remap = false)
public class AbstractSectionMixin {
    @ModifyConstant(method = {
            "getCenterX",
            "getCenterY",
            "getCenterZ"
    },
                    constant = @Constant(intValue = 8),
                    require = 3)
    private int extendChunk1(int constant) {
        return MegaChunkMetadata.BLOCKS_PER_WR_EDGE / 2;
    }

    @ModifyConstant(method = {
            "getOriginX",
            "getOriginY",
            "getOriginZ"
    },
                    constant = @Constant(intValue = 4),
                    require = 3)
    private int extendChunk2(int constant) {
        return MegaChunkMetadata.BLOCKS_PER_WR_EDGE_BITS;
    }
}
