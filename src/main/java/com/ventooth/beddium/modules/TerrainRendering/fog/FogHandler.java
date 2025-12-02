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

import com.falsepattern.lib.util.MathUtil;
import com.ventooth.beddium.config.TerrainRenderingConfig;
import lombok.val;
import org.lwjgl.opengl.GL11;


/**
 * Orchestrates tracking of the fog state
 */
public final class FogHandler {
    public static boolean DEBUG;

    private FogHandler() {
        throw new UnsupportedOperationException();
    }

    public static void preSetupFog(float farPlaneDistance) {
        if (TerrainRenderingConfig.FastFog) {
            FogState.setDefault(farPlaneDistance);
            FogGL.write();
        }
        FogAsmHooks.enable();
    }

    public static void postSetupFog(float farPlaneDistance) {
        FogAsmHooks.disable();
        if (TerrainRenderingConfig.ChunkDrawMode == TerrainRenderingConfig.DrawModeEnum.Fast) {
            applyBias(farPlaneDistance);
        }
    }

    private static void applyBias(float farPlaneDistance) {
        val fogBias = (float) TerrainRenderingConfig.FastChunkDrawModeFogBias;
        if (MathUtil.epsilonEquals(fogBias, 0F)) {
            return;
        }

        float fogEnd;
        float fogStart;
        if (TerrainRenderingConfig.FastFog) {
            fogEnd = FogState.end;
            fogStart = FogState.start;
        } else {
            fogEnd = GL11.glGetInteger(GL11.GL_FOG_END);
            fogStart = GL11.glGetInteger(GL11.GL_FOG_START);
        }
        val maxFogEnd = farPlaneDistance + fogBias;

        if (maxFogEnd >= fogEnd || fogStart > fogEnd) {
            return;
        }

        val biasRatio = maxFogEnd / fogEnd;
        fogEnd = maxFogEnd;
        fogStart *= biasRatio;

        GL11.glFogf(GL11.GL_FOG_END, fogEnd);
        GL11.glFogf(GL11.GL_FOG_START, fogStart);

        if (TerrainRenderingConfig.FastFog) {
            FogState.end = fogEnd;
            FogState.start = fogStart;
        }
    }
}
