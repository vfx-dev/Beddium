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

import java.nio.FloatBuffer;

/**
 * Captures fog state from other mods during fog setup
 */
@SuppressWarnings("unused")
public final class FogAsmHooks {
    private static boolean enabled = false;

    private FogAsmHooks() {
        throw new UnsupportedOperationException();
    }

    public static void enable() {
        enabled = true;
    }

    public static void disable() {
        enabled = false;
    }

    public static void beddium$glFog(int pname, FloatBuffer params) {
        if (enabled) {
            FogGL.glFog(pname, params);
        }
        GL11.glFog(pname, params);
    }

    public static void beddium$glFogf(int pname, float param) {
        if (enabled) {
            FogGL.glFogf(pname, param);
        }
        GL11.glFogf(pname, param);
    }

    public static void beddium$glEnable(int cap) {
        if (enabled) {
            FogGL.glEnable(cap);
        }
        GL11.glEnable(cap);
    }

    public static void beddium$glDisable(int cap) {
        if (enabled) {
            FogGL.glDisable(cap);
        }
        GL11.glDisable(cap);
    }

    public static void beddium$glFogi(int pname, int param) {
        if (enabled) {
            FogGL.glFogi(pname, param);
        }
        GL11.glFogi(pname, param);
    }
}
