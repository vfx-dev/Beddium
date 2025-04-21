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

package com.ventooth.beddium.api.task;

import com.ventooth.beddium.modules.MEGAChunks.MegaChunkMetadata;
import lombok.RequiredArgsConstructor;
import org.embeddedt.embeddium.impl.render.chunk.RenderSection;

@RequiredArgsConstructor
public class WorldRenderRegion {
    public final int minX;
    public final int minY;
    public final int minZ;
    public final int maxX;
    public final int maxY;
    public final int maxZ;

    public static WorldRenderRegion forChunkAt(RenderSection render) {
        int minX = render.getOriginX();
        int minY = render.getOriginY();
        int minZ = render.getOriginZ();

        int maxX = minX + MegaChunkMetadata.BLOCKS_PER_WR_EDGE;
        int maxY = minY + MegaChunkMetadata.BLOCKS_PER_WR_EDGE;
        int maxZ = minZ + MegaChunkMetadata.BLOCKS_PER_WR_EDGE;

        return new WorldRenderRegion(minX, minY, minZ, maxX, maxY, maxZ);
    }
}
