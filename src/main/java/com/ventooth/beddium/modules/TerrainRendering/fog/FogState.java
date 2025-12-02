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

package com.ventooth.beddium.modules.TerrainRendering.fog;

import org.lwjgl.opengl.GL11;

/**
 * Keeps track of the current fog state
 */
public final class FogState {
    public static boolean enabled;
    public static int mode;

    public static float start;
    public static float end;
    public static float density;

    public static float red;
    public static float green;
    public static float blue;

    private FogState() {
        throw new UnsupportedOperationException();
    }

    public static void setDefault(float farPlaneDistance) {
        FogState.enabled = true;
        FogState.mode = GL11.GL_LINEAR;

        FogState.red = 1F;
        FogState.green = 1F;
        FogState.blue = 1F;

        FogState.start = farPlaneDistance * 0.75F;
        FogState.end = farPlaneDistance;
        FogState.density = 0.1F;
    }
}
