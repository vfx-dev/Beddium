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

package com.ventooth.beddium.modules.TerrainRendering.cache;

import com.ventooth.beddium.api.cache.CachedArrays;
import com.ventooth.beddium.api.cache.CachedArraysFactory;
import it.unimi.dsi.fastutil.objects.AbstractObjectList;
import it.unimi.dsi.fastutil.objects.ObjectArrayList;
import lombok.val;

import java.util.ServiceLoader;
import java.util.concurrent.locks.ReentrantLock;

public class ArrayCache {
    private static final CachedArraysFactory FACTORY;

    static {
        val loader = ServiceLoader.load(CachedArraysFactory.class);
        loader.reload();
        FACTORY = loader.findFirst().orElse(CachedArraysVanillaLight::new);
    }

    private final int maxCacheSize;
    private final AbstractObjectList<CachedArrays> cache;
    private final ReentrantLock MUTEX = new ReentrantLock();

    public ArrayCache(int maxCacheSize) {
        this.maxCacheSize = maxCacheSize;
        cache = new ObjectArrayList<>(maxCacheSize);
    }

    public CachedArrays allocate(int size) {
        CachedArrays arr;
        while (!MUTEX.tryLock()) {
            Thread.yield();
        }
        try {
            if (this.cache.isEmpty()) {
                arr = null;
            } else {
                arr = this.cache.pop();
            }
        } finally {
            MUTEX.unlock();
        }
        if (arr != null && arr.capacity >= size) {
            return arr;
        }
        return FACTORY.create(size);

    }

    public void free(CachedArrays arr) {
        if (arr != null) {
            arr.reset();
            while (!MUTEX.tryLock()) {
                Thread.yield();
            }
            try {
                if (this.cache.size() < this.maxCacheSize) {
                    this.cache.push(arr);
                }
            } finally {
                MUTEX.unlock();
            }
        }
    }
}
