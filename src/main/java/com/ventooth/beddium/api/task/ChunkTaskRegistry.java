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

import com.ventooth.beddium.modules.TerrainRendering.compile.task.ChunkBuilderMeshingTaskProvider;

/**
 * Not thread safe because nobody should write to this off-thread, and it shouldn't be changed after init.
 */
public final class ChunkTaskRegistry {
    private static ChunkTaskProvider currentProvider = null;
    private static int currentPriority = 0;

    public static void registerProvider(ChunkTaskProvider provider, int priority) {
        if (provider != null && (currentProvider == null || priority < currentPriority)) {
            currentProvider = provider;
            currentPriority = priority;
        }
    }

    public static ChunkTaskProvider getProvider() {
        if (currentProvider == null) {
            currentProvider = new ChunkBuilderMeshingTaskProvider();
        }
        return currentProvider;
    }
}
