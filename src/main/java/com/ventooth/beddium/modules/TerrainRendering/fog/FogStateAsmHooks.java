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

import com.ventooth.beddium.config.TerrainRenderingConfig;
import org.lwjgl.opengl.GL11;

import java.nio.FloatBuffer;

public final class FogStateAsmHooks {
    private FogStateAsmHooks() {
        throw new UnsupportedOperationException();
    }

    @SuppressWarnings("unused")
    public static void beddium$glFog(int pname, FloatBuffer params) {
        if (TerrainRenderingConfig.FastFog) {
            FogStateTracker.glFog(pname, params);
        }
        GL11.glFog(pname, params);
    }

    @SuppressWarnings("unused")
    public static void beddium$glFogf(int pname, float param) {
        if (TerrainRenderingConfig.FastFog) {
            FogStateTracker.glFogf(pname, param);
        }
        GL11.glFogf(pname, param);
    }

    @SuppressWarnings("unused")
    public static void beddium$glEnable(int cap) {
        if (TerrainRenderingConfig.FastFog) {
            FogStateTracker.glEnable(cap);
        }
        GL11.glEnable(cap);
    }

    @SuppressWarnings("unused")
    public static void beddium$glDisable(int cap) {
        if (TerrainRenderingConfig.FastFog) {
            FogStateTracker.glDisable(cap);
        }
        GL11.glDisable(cap);
    }

    @SuppressWarnings("unused")
    public static void beddium$glFogi(int pname, int param) {
        if (TerrainRenderingConfig.FastFog) {
            FogStateTracker.glFogi(pname, param);
        }
        GL11.glFogi(pname, param);
    }
}
