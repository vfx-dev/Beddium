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
import org.embeddedt.embeddium.impl.util.position.SectionPos;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.Constant;
import org.spongepowered.asm.mixin.injection.ModifyConstant;

@Mixin(value = SectionPos.class,
       remap = false)
public abstract class SectionPosMixin {
    @ModifyConstant(method = {
            "minX",
            "minY",
            "minZ"
    },
                    constant = @Constant(intValue = 16),
                    require = 3)
    private int extendChunk1(int constant) {
        return MegaChunkMetadata.BLOCKS_PER_WR_EDGE;
    }

    @ModifyConstant(method = {
            "maxX",
            "maxY",
            "maxZ"
    },
                    constant = @Constant(intValue = 15),
                    require = 3)
    private int extendChunk2(int constant) {
        return MegaChunkMetadata.BLOCKS_PER_WR_EDGE - 1;
    }
}
