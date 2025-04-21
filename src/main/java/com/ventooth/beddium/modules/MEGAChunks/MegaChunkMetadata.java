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

import com.ventooth.beddium.config.TerrainRenderingConfig;

//HEY      EVERY      !! IT'S ME!!
public class MegaChunkMetadata {
    public static final int BLOCKS_PER_EBS_EDGE_BITS = 4; //fixed
    public static final int BLOCKS_PER_EBS_EDGE = 1 << BLOCKS_PER_EBS_EDGE_BITS;
    public static final int BLOCKS_PER_EBS_EDGE_BITMASK = BLOCKS_PER_EBS_EDGE - 1;
    public static final int BLOCKS_PER_EBS_FACE = BLOCKS_PER_EBS_EDGE * BLOCKS_PER_EBS_EDGE;
    public static final int BLOCKS_PER_EBS_VOLUME = BLOCKS_PER_EBS_FACE * BLOCKS_PER_EBS_EDGE;

    public static final int EBS_PER_WR_EDGE_BITS = TerrainRenderingConfig.MEGAChunks; //dynamic
    public static final int EBS_PER_WR_EDGE = 1 << EBS_PER_WR_EDGE_BITS;
    public static final int EBS_PER_WR_EDGE_BITMASK = EBS_PER_WR_EDGE - 1;
    public static final int EBS_PER_WR_FACE = EBS_PER_WR_EDGE * EBS_PER_WR_EDGE;
    public static final int EBS_PER_WR_VOLUME = EBS_PER_WR_FACE * EBS_PER_WR_EDGE;

    public static final int BLOCKS_PER_WR_EDGE_BITS = BLOCKS_PER_EBS_EDGE_BITS + EBS_PER_WR_EDGE_BITS;
    public static final int BLOCKS_PER_WR_EDGE = BLOCKS_PER_EBS_EDGE * EBS_PER_WR_EDGE;
    public static final float BLOCKS_PER_WR_EDGE_FLOAT = (float)BLOCKS_PER_WR_EDGE;
    public static final int BLOCKS_PER_WR_EDGE_BITMASK = BLOCKS_PER_WR_EDGE - 1;
    public static final int BLOCKS_PER_WR_FACE = BLOCKS_PER_EBS_FACE * EBS_PER_WR_FACE;
    public static final int BLOCKS_PER_WR_VOLUME = BLOCKS_PER_EBS_VOLUME * EBS_PER_WR_VOLUME;


    public static final int VISGRAPH_INDEX_Y_OFFSET = BLOCKS_PER_WR_EDGE_BITS * 2;
    public static final int VISGRAPH_INDEX_Z_OFFSET = BLOCKS_PER_WR_EDGE_BITS;
}


