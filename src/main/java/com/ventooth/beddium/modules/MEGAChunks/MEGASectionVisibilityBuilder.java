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

import it.unimi.dsi.fastutil.ints.IntArrayFIFOQueue;
import it.unimi.dsi.fastutil.ints.IntArrayList;
import org.embeddedt.embeddium.impl.render.chunk.occlusion.GraphDirection;
import org.embeddedt.embeddium.impl.render.chunk.occlusion.VisibilityEncoding;

import java.util.BitSet;

// DON'T YOU WANNA BE A BIG SHOT?
public class MEGASectionVisibilityBuilder {
    private static final int SECTION_AXIS_SIZE = MegaChunkMetadata.BLOCKS_PER_WR_EDGE;
    private static final int SECTION_AXIS_MASK = SECTION_AXIS_SIZE - 1;
    private static final int TOTAL_BLOCKS = SECTION_AXIS_SIZE * SECTION_AXIS_SIZE * SECTION_AXIS_SIZE;
    private static final int BLOCKS_ON_ONE_FACE = SECTION_AXIS_SIZE * SECTION_AXIS_SIZE;
    private static final int BITS_PER_AXIS = MegaChunkMetadata.BLOCKS_PER_WR_EDGE_BITS;
    private static final int X_SHIFT = 0;
    private static final int Z_SHIFT = BITS_PER_AXIS;
    private static final int Y_SHIFT = BITS_PER_AXIS * 2;

    /**
     * All indices that are touching the edge of a section. These are used as the starting points for the floodfill.
     */
    private static final int[] INDICES_TO_INITIATE_FLOODFILL = buildFloodfillIndices();

    private final BitSet blocks;

    private static int[] buildFloodfillIndices() {
        IntArrayList indicesList = new IntArrayList(TOTAL_BLOCKS - (SECTION_AXIS_SIZE - 2) * (SECTION_AXIS_SIZE - 2) * (SECTION_AXIS_SIZE - 2));
        for (int x = 0; x < SECTION_AXIS_SIZE; x++) {
            for(int z = 0; z < SECTION_AXIS_SIZE; z++) {
                for(int y = 0; y < SECTION_AXIS_SIZE; y++) {
                    if (x == 0 || x == (SECTION_AXIS_SIZE - 1) || y == 0 || y == (SECTION_AXIS_SIZE - 1) || z == 0 || z == (SECTION_AXIS_SIZE - 1)) {
                        indicesList.add(getIndex(x, y, z));
                    }
                }
            }
        }
        return indicesList.toIntArray();
    }

    public MEGASectionVisibilityBuilder() {
        this.blocks = new BitSet(SECTION_AXIS_SIZE * SECTION_AXIS_SIZE * SECTION_AXIS_SIZE);
    }

    public long computeVisibilityEncoding() {
        int opaqueCount = blocks.cardinality();
        if (opaqueCount == TOTAL_BLOCKS) {
            // everything is opaque, so we can't see anything anywhere
            return VisibilityEncoding.NULL;
        } else if (opaqueCount < BLOCKS_ON_ONE_FACE) {
            // There are not enough blocks set to even fully cover one face. Therefore, we must be able
            // to see everything from everything.
            return VisibilityEncoding.EVERYTHING;
        } else {
            return computeWithFloodFill();
        }
    }

    public void markOpaque(int x, int y, int z) {
        this.blocks.set(getIndex(x & MegaChunkMetadata.BLOCKS_PER_WR_EDGE_BITMASK, y & MegaChunkMetadata.BLOCKS_PER_WR_EDGE_BITMASK, z & MegaChunkMetadata.BLOCKS_PER_WR_EDGE_BITMASK));
    }

    private long computeWithFloodFill() {
        long resultEncoding = 0;
        var blocks = this.blocks;
        BitSet escapedFaces = new BitSet(GraphDirection.COUNT);
        IntArrayFIFOQueue queue = new IntArrayFIFOQueue();
        for (int i : INDICES_TO_INITIATE_FLOODFILL) {
            if (!blocks.get(i)) {
                escapedFaces.clear();
                queue.clear();
                this.exploreFrom(escapedFaces, queue, i);
                for (int dir = escapedFaces.nextSetBit(0); dir >= 0; dir = escapedFaces.nextSetBit(dir+1)) {
                    for (int dir2 = escapedFaces.nextSetBit(0); dir2 >= 0; dir2 = escapedFaces.nextSetBit(dir2+1)) {
                        resultEncoding |= 1L << (dir * 8 + dir2);
                    }
                }
            }
        }
        return resultEncoding;
    }

    private void exploreFrom(BitSet escapedFaces, IntArrayFIFOQueue queue, int startIndex) {
        queue.enqueue(startIndex);
        var blocks = this.blocks;
        // Mark the start location as handled
        blocks.set(startIndex, true);
        while (!queue.isEmpty()) {
            int idx = queue.dequeueInt();
            for (int dir = 0; dir < GraphDirection.COUNT; dir++) {
                int neighborIdx = getNeighborIndex(idx, dir);
                if (neighborIdx >= 0) {
                    // We can move within the section in that direction
                    if (!blocks.get(neighborIdx)) {
                        // Mark this location as handled
                        blocks.set(neighborIdx, true);
                        queue.enqueue(neighborIdx);
                    }
                } else {
                    // We moved out of the section, mark this as an escaping face
                    escapedFaces.set(dir);
                }
            }
        }
    }

    private static int getNeighborIndex(int idx, int dir) {
        switch (dir) {
            case GraphDirection.UP -> {
                if (((idx >> Y_SHIFT) & SECTION_AXIS_MASK) == SECTION_AXIS_MASK) {
                    return -1;
                } else {
                    return idx + (1 << Y_SHIFT);
                }
            }
            case GraphDirection.DOWN -> {
                if (((idx >> Y_SHIFT) & SECTION_AXIS_MASK) == 0) {
                    return -1;
                } else {
                    return idx - (1 << Y_SHIFT);
                }
            }
            case GraphDirection.EAST -> {
                if (((idx >> X_SHIFT) & SECTION_AXIS_MASK) == SECTION_AXIS_MASK) {
                    return -1;
                } else {
                    return idx + (1 << X_SHIFT);
                }
            }
            case GraphDirection.WEST -> {
                if (((idx >> X_SHIFT) & SECTION_AXIS_MASK) == 0) {
                    return -1;
                } else {
                    return idx - (1 << X_SHIFT);
                }
            }
            case GraphDirection.SOUTH -> {
                if (((idx >> Z_SHIFT) & SECTION_AXIS_MASK) == SECTION_AXIS_MASK) {
                    return -1;
                } else {
                    return idx + (1 << Z_SHIFT);
                }
            }
            case GraphDirection.NORTH -> {
                if (((idx >> Z_SHIFT) & SECTION_AXIS_MASK) == 0) {
                    return -1;
                } else {
                    return idx - (1 << Z_SHIFT);
                }
            }
            default -> throw new IllegalArgumentException();
        }
    }

    private static int getIndex(int x, int y, int z) {
        return (y << Y_SHIFT) | (z << Z_SHIFT) | (x << X_SHIFT);
    }
}
