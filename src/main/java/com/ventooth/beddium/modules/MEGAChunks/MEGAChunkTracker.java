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

package com.ventooth.beddium.modules.MEGAChunks;

import com.ventooth.beddium.modules.TerrainRendering.SafeChunkTracker;
import it.unimi.dsi.fastutil.longs.Long2LongMap;
import it.unimi.dsi.fastutil.longs.Long2LongOpenHashMap;
import lombok.val;
import org.embeddedt.embeddium.impl.render.chunk.map.ChunkStatus;
import org.embeddedt.embeddium.impl.render.chunk.map.ChunkTracker;
import org.embeddedt.embeddium.impl.util.PositionUtil;

public sealed interface MEGAChunkTracker permits MEGAChunkTracker.Fast, MEGAChunkTracker.Vanilla {
    long ALL_BITS_ALL = getAllBitsAll();

    private static long getAllBitsAll() {
        long accum = 0;
        for (int z = 0; z < MegaChunkMetadata.EBS_PER_WR_EDGE; z++) {
            for (int x = 0; x < MegaChunkMetadata.EBS_PER_WR_EDGE; x++) {
                accum |= shiftTo(x, z);
            }
        }
        return accum;
    }

    private static long shiftTo(int x, int z) {
        val subX = x & MegaChunkMetadata.EBS_PER_WR_EDGE_BITMASK;
        val subZ = z & MegaChunkMetadata.EBS_PER_WR_EDGE_BITMASK;
        val offset = subX + (subZ << MegaChunkMetadata.EBS_PER_WR_EDGE_BITS);
        return 1L << offset;
    }

    Long2LongMap subChunkStatus();

    default void onChunkStatusAddedMEGA(int x, int z, int flags) {
        assert flags == ChunkStatus.FLAG_ALL;
        val wX = x >> MegaChunkMetadata.EBS_PER_WR_EDGE_BITS;
        val wZ = z >> MegaChunkMetadata.EBS_PER_WR_EDGE_BITS;
        var sFlags = shiftTo(x, z);

        var key = PositionUtil.packChunk(wX, wZ);

        var prev = subChunkStatus().get(key);
        var cur = prev | sFlags;

        if (prev == cur) {
            return;
        }

        subChunkStatus().put(key, cur);

        if (cur == ALL_BITS_ALL) {
            onChunkStatusAddedImpl(wX, wZ, ChunkStatus.FLAG_ALL);
        }
    }

    default void onChunkStatusRemovedMEGA(int x, int z, int flags) {
        assert flags == ChunkStatus.FLAG_ALL;
        val wX = x >> MegaChunkMetadata.EBS_PER_WR_EDGE_BITS;
        val wZ = z >> MegaChunkMetadata.EBS_PER_WR_EDGE_BITS;
        var sFlags = shiftTo(x, z);

        var key = PositionUtil.packChunk(wX, wZ);

        var prev = subChunkStatus().get(key);
        var cur = prev & ~sFlags;

        if (prev == cur) {
            return;
        }

        if (cur == subChunkStatus().defaultReturnValue()) {
            subChunkStatus().remove(key);
        } else {
            subChunkStatus().put(key, cur);
        }

        if (prev == ALL_BITS_ALL) {
            onChunkStatusRemovedImpl(wX, wZ, ChunkStatus.FLAG_ALL);
        }
    }

    void onChunkStatusAddedImpl(int x, int z, int flags);
    void onChunkStatusRemovedImpl(int x, int z, int flags);

    final class Fast extends ChunkTracker implements MEGAChunkTracker {
        private final Long2LongMap subChunkStatus = new Long2LongOpenHashMap();

        @Override
        public Long2LongMap subChunkStatus() {
            return subChunkStatus;
        }

        @Override
        public void onChunkStatusAddedImpl(int x, int z, int flags) {
            super.onChunkStatusAdded(x, z, flags);
        }

        @Override
        public void onChunkStatusRemovedImpl(int x, int z, int flags) {
            super.onChunkStatusRemoved(x, z, flags);
        }

        @Override
        public void onChunkStatusAdded(int x, int z, int flags) {
            onChunkStatusAddedMEGA(x, z, flags);
        }

        @Override
        public void onChunkStatusRemoved(int x, int z, int flags) {
            onChunkStatusRemovedMEGA(x, z, flags);
        }
    }
    final class Vanilla extends SafeChunkTracker implements MEGAChunkTracker {
        private final Long2LongMap subChunkStatus = new Long2LongOpenHashMap();

        @Override
        public Long2LongMap subChunkStatus() {
            return subChunkStatus;
        }

        @Override
        public void onChunkStatusAddedImpl(int x, int z, int flags) {
            super.onChunkStatusAdded(x, z, flags);
        }

        @Override
        public void onChunkStatusRemovedImpl(int x, int z, int flags) {
            super.onChunkStatusRemoved(x, z, flags);
        }

        @Override
        public void onChunkStatusAdded(int x, int z, int flags) {
            onChunkStatusAddedMEGA(x, z, flags);
        }

        @Override
        public void onChunkStatusRemoved(int x, int z, int flags) {
            onChunkStatusRemovedMEGA(x, z, flags);
        }
    }
}
