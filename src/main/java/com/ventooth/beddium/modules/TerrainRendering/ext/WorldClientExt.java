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

package com.ventooth.beddium.modules.TerrainRendering.ext;

import org.embeddedt.embeddium.impl.render.chunk.map.ChunkTracker;
import org.embeddedt.embeddium.impl.render.chunk.map.ChunkTrackerHolder;

import net.minecraft.client.multiplayer.WorldClient;

public interface WorldClientExt extends ChunkTrackerHolder {
    @SuppressWarnings("CastToIncompatibleInterface")
    static WorldClientExt of(WorldClient thiz) {
        return (WorldClientExt) thiz;
    }

    @Override
    default ChunkTracker sodium$getTracker() {
        return celeritas$chunkTracker();
    }

    ChunkTracker celeritas$chunkTracker();
}
