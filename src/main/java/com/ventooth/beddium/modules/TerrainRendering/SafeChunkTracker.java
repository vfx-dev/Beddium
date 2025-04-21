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

import it.unimi.dsi.fastutil.longs.Long2IntOpenHashMap;
import it.unimi.dsi.fastutil.longs.LongCollection;
import it.unimi.dsi.fastutil.longs.LongOpenHashSet;
import it.unimi.dsi.fastutil.longs.LongSet;
import it.unimi.dsi.fastutil.longs.LongSets;
import lombok.SneakyThrows;
import org.embeddedt.embeddium.impl.render.chunk.map.ChunkStatus;
import org.embeddedt.embeddium.impl.render.chunk.map.ChunkTracker;
import org.embeddedt.embeddium.impl.util.PositionUtil;

import java.lang.reflect.Field;

public class SafeChunkTracker extends ChunkTracker {
    private static final Field chunkStatus;
    private static final Field chunkReady;
    private static final Field unloadQueue;
    private static final Field loadQueue;
    static {
        try {
            var klass = ChunkTracker.class;
            chunkStatus = klass.getDeclaredField("chunkStatus");
            chunkStatus.setAccessible(true);
            chunkReady = klass.getDeclaredField("chunkReady");
            chunkReady.setAccessible(true);
            unloadQueue = klass.getDeclaredField("unloadQueue");
            unloadQueue.setAccessible(true);
            loadQueue = klass.getDeclaredField("loadQueue");
            loadQueue.setAccessible(true);
        } catch (NoSuchFieldException e) {
            throw new RuntimeException(e);
        }
    }

    private final LongOpenHashSet chunkReadyForced = new LongOpenHashSet();

    @SneakyThrows
    private Long2IntOpenHashMap chunkStatus() {
        return (Long2IntOpenHashMap) chunkStatus.get(this);
    }

    @SneakyThrows
    private LongOpenHashSet chunkReady() {
        return (LongOpenHashSet) chunkReady.get(this);
    }

    @SneakyThrows
    private LongSet unloadQueue() {
        return (LongSet) unloadQueue.get(this);
    }

    @SneakyThrows
    private LongSet loadQueue() {
        return (LongSet) loadQueue.get(this);
    }

    @Override
    public void onChunkStatusAdded(int x, int z, int flags) {
        var key = PositionUtil.packChunk(x, z);

        var prev = this.chunkStatus().get(key);
        var cur = prev | flags;

        if (prev == cur) {
            return;
        }

        this.chunkStatus().put(key, cur);

        this.updateNeighbors(x, z);
    }

    @Override
    public void onChunkStatusRemoved(int x, int z, int flags) {
        var key = PositionUtil.packChunk(x, z);

        var prev = this.chunkStatus().get(key);
        int cur = prev & ~flags;

        if (prev == cur) {
            return;
        }

        if (cur == this.chunkStatus().defaultReturnValue()) {
            this.chunkStatus().remove(key);
        } else {
            this.chunkStatus().put(key, cur);
        }

        this.updateNeighbors(x, z);
    }

    private void updateNeighbors(int x, int z) {
        for (int ox = -1; ox <= 1; ox++) {
            for (int oz = -1; oz <= 1; oz++) {
                this.updateMerged(ox + x, oz + z);
            }
        }
    }

    private void updateMerged(int x, int z) {
        long key = PositionUtil.packChunk(x, z);

        int selfFlag = this.chunkStatus().get(key);
        int flags = selfFlag;

        for (int ox = -1; ox <= 1; ox++) {
            for (int oz = -1; oz <= 1; oz++) {
                flags &= this.chunkStatus().get(PositionUtil.packChunk(ox + x, oz + z));
            }
        }

        if (flags == ChunkStatus.FLAG_ALL) {
            if ((this.chunkReadyForced.add(key) || this.chunkReady().add(key)) && !this.unloadQueue().remove(key)) {
                this.loadQueue().add(key);
            }
        } else if (selfFlag == ChunkStatus.FLAG_ALL) {
            if ((this.chunkReadyForced.add(key) || this.chunkReady().remove(key)) && !this.unloadQueue().remove(key)) {
                this.loadQueue().add(key);
            }
        } else {
            if ((this.chunkReadyForced.remove(key) || this.chunkReady().remove(key)) && !this.loadQueue().remove(key)) {
                this.unloadQueue().add(key);
            }
        }
    }

    public LongCollection getReadyChunks() {
        return LongSets.unmodifiable(this.chunkReadyForced);
    }
}
