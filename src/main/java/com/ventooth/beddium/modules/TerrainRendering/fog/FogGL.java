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

import org.lwjgl.BufferUtils;
import org.lwjgl.opengl.GL11;

import java.nio.FloatBuffer;

/**
 * Handles fog interactions on the OpenGl side
 */
public final class FogGL {
    private static final FloatBuffer tempBuf = BufferUtils.createFloatBuffer(16);

    private FogGL() {
        throw new UnsupportedOperationException();
    }

    public static void read() {
        FogState.enabled = GL11.glGetBoolean(GL11.GL_FOG);
        FogState.mode = GL11.glGetInteger(GL11.GL_FOG_MODE);

        GL11.glGetFloatv(GL11.GL_FOG_COLOR, tempBuf);
        FogState.red = tempBuf.get(0);
        FogState.green = tempBuf.get(1);
        FogState.blue = tempBuf.get(2);

        FogState.start = GL11.glGetFloat(GL11.GL_FOG_START);
        FogState.end = GL11.glGetFloat(GL11.GL_FOG_END);
        FogState.density = GL11.glGetFloat(GL11.GL_FOG_DENSITY);
    }

    public static void write() {
        if (FogState.enabled) {
            GL11.glEnable(GL11.GL_FOG);
        } else {
            GL11.glDisable(GL11.GL_FOG);
        }
        GL11.glFogi(GL11.GL_FOG_MODE, FogState.mode);

        tempBuf.put(0, FogState.red);
        tempBuf.put(1, FogState.green);
        tempBuf.put(2, FogState.blue);
        GL11.glFogfv(GL11.GL_FOG_COLOR, tempBuf);

        GL11.glFogf(GL11.GL_FOG_START, FogState.start);
        GL11.glFogf(GL11.GL_FOG_END, FogState.end);
    }

    public static void glFog(int pname, FloatBuffer params) {
        if (pname == GL11.GL_FOG_COLOR) {
            FogState.red = params.get(0);
            FogState.green = params.get(1);
            FogState.blue = params.get(2);
        }
    }

    public static void glFogf(int pname, float param) {
        switch (pname) {
            case GL11.GL_FOG_END -> FogState.end = param;
            case GL11.GL_FOG_START -> FogState.start = param;
            case GL11.GL_FOG_DENSITY -> FogState.density = param;
        }
    }

    public static void glEnable(int cap) {
        if (cap == GL11.GL_FOG) {
            FogState.enabled = true;
        }
    }

    public static void glDisable(int cap) {
        if (cap == GL11.GL_FOG) {
            FogState.enabled = false;
        }
    }

    public static void glFogi(int pname, int param) {
        if (pname == GL11.GL_FOG_MODE) {
            FogState.mode = param;
        }
    }
}
