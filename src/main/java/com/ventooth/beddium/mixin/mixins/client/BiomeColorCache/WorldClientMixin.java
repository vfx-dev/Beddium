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

import com.ventooth.beddium.api.cache.BiomeColorCacheCapable;
import com.ventooth.beddium.modules.BiomeColorCache.BiomeColorCacheImpl;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Unique;

import net.minecraft.client.multiplayer.WorldClient;
import net.minecraft.world.IBlockAccess;

@Mixin(WorldClient.class)
public abstract class WorldClientMixin implements IBlockAccess, BiomeColorCacheCapable {
    @Unique
    private final BiomeColorCacheImpl celeritas$biomeCache = new BiomeColorCacheImpl(this);

    @Override
    public BiomeColorCacheImpl celeritas$biomeCache() {
        return celeritas$biomeCache;
    }
}