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

package com.ventooth.beddium.modules.BiomeColorCache;

import org.embeddedt.embeddium.impl.biome.BiomeColorCache;

import net.minecraft.world.IBlockAccess;
import net.minecraft.world.biome.BiomeGenBase;

public final class BiomeColorCacheImpl extends BiomeColorCache<BiomeGenBase, BiomeColorType> {
    public BiomeColorCacheImpl(IBlockAccess world) {
        super((posX, posY, posZ) -> world.getBiomeGenForCoords(posX, posZ), 3);
    }

    @Override
    protected int resolveColor(BiomeColorType type, BiomeGenBase biome, int relativeX, int relativeY, int relativeZ) {
        return switch (type) {
            case GRASS -> biome.getBiomeGrassColor(relativeX, relativeY, relativeZ);
            case FOLIAGE -> biome.getBiomeFoliageColor(relativeX, relativeY, relativeZ);
            case WATER -> biome.getWaterColorMultiplier();
        };
    }
}
