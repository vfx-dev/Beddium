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

package com.ventooth.beddium.mixin.mixins.client.BiomeColorCache;

import com.llamalad7.mixinextras.injector.wrapmethod.WrapMethod;
import com.llamalad7.mixinextras.injector.wrapoperation.Operation;
import com.ventooth.beddium.api.cache.BiomeColorCacheCapable;
import com.ventooth.beddium.modules.BiomeColorCache.BiomeColorCacheModule;
import com.ventooth.beddium.modules.BiomeColorCache.BiomeColorType;
import org.spongepowered.asm.mixin.Mixin;

import net.minecraft.block.BlockGrass;
import net.minecraft.world.IBlockAccess;

@Mixin(BlockGrass.class)
public abstract class BlockGrassMixin {
    @WrapMethod(method = "colorMultiplier")
    private int smoothBlendColor(IBlockAccess world, int posX, int posY, int posZ, Operation<Integer> original) {
        if (BiomeColorCacheModule.isCacheActive() && world instanceof BiomeColorCacheCapable ext) {
            return ext.celeritas$biomeCache().getColor(BiomeColorType.GRASS, posX, posY, posZ);
        } else {
            return original.call(world, posX, posY, posZ);
        }
    }
}
