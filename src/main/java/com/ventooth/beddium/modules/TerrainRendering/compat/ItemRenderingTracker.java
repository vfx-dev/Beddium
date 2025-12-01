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

package com.ventooth.beddium.modules.TerrainRendering.compat;

import lombok.val;

public final class ItemRenderingTracker {
    private static final int MAX_LEVEL = 20;
    private static final ThreadLocal<Integer> stack = ThreadLocal.withInitial(() -> 0);

    private ItemRenderingTracker() {
        throw new UnsupportedOperationException();
    }

    public static void preRenderItem() {
        val cur = stack.get() + 1;
        if (cur > MAX_LEVEL) {
            throw new AssertionError("I messed up");
        }
        stack.set(cur);
    }

    public static void postRenderItem() {
        val cur = stack.get() - 1;
        if (cur < 0) {
            throw new AssertionError("I messed up");
        }
        stack.set(cur);
    }

    public static boolean isRenderingItem() {
        return stack.get() > 0;
    }
}
