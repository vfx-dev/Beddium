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

import com.ventooth.beddium.api.cache.BiomeColorCacheCapable;
import lombok.AccessLevel;
import lombok.NoArgsConstructor;
import org.embeddedt.embeddium.impl.util.position.SectionPos;

import net.minecraft.world.IBlockAccess;

@NoArgsConstructor(access = AccessLevel.PRIVATE)
public final class BiomeColorCacheModule {
    private static ThreadLocal<Boolean> isCacheActive = ThreadLocal.withInitial(() -> false);

    public static void toggleCacheActive(boolean enable) {
        isCacheActive.set(enable);
    }

    public static boolean isCacheActive() {
        return isCacheActive.get();
    }

    public static void update(IBlockAccess world, int posX, int posY, int posZ) {
        if (world instanceof BiomeColorCacheCapable ext) {
            ext.celeritas$biomeCache().update(new SectionPos(posX, posY, posZ));
        }
    }
}
