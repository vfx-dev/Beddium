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
import com.ventooth.beddium.Share;
import com.ventooth.beddium.config.ModuleConfig;
import com.ventooth.beddium.config.TerrainRenderingConfig;
import lombok.val;
import org.embeddedt.embeddium.impl.render.chunk.shader.ChunkFogMode;
import org.jetbrains.annotations.NotNull;
import org.lwjgl.BufferUtils;
import org.lwjgl.opengl.GL11;

import java.nio.FloatBuffer;

public final class FogStateTracker {
    static final FloatBuffer color = BufferUtils.createFloatBuffer(16);

    static float end = 1000F;
    static float start = 1000F;
    static float density = 0.1F;

    static boolean isEnabled = false;
    static @NotNull ChunkFogMode mode = ChunkFogMode.NONE;

    private FogStateTracker() {
        throw new UnsupportedOperationException();
    }

    public static void setFromGl() {
        GL11.glGetFloatv(GL11.GL_FOG_COLOR, color);

        isEnabled = GL11.glGetBoolean(GL11.GL_FOG);
        mode = fromGLMode(GL11.glGetInteger(GL11.GL_FOG_MODE));
        end = GL11.glGetInteger(GL11.GL_FOG_END);
        start = GL11.glGetInteger(GL11.GL_FOG_START);
        density = GL11.glGetInteger(GL11.GL_FOG_DENSITY);
    }
    
    public static void postSetupFog(float farPlaneDistance) {
        if (TerrainRenderingConfig.ChunkDrawMode != TerrainRenderingConfig.DrawModeEnum.Fast) {
            return;
        }

        val fogBias = (float) TerrainRenderingConfig.FastChunkDrawModeFogBias;
        if (MathUtil.epsilonEquals(fogBias, 0F)) {
            return;
        }

        float fogEnd;
        float fogStart;
        if (TerrainRenderingConfig.FastFog) {
            fogEnd = FogStateTracker.end;
            fogStart = FogStateTracker.start;
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
            FogStateTracker.end = fogEnd;
            FogStateTracker.start = fogStart;
        }
    }

    public static void glFog(int pname, FloatBuffer params) {
        if (pname == GL11.GL_FOG_COLOR) {
            FogStateTracker.color.put(0, params.get(0));
            FogStateTracker.color.put(1, params.get(1));
            FogStateTracker.color.put(2, params.get(2));
            FogStateTracker.color.put(3, params.get(3));
        }
    }

    public static void glFogf(int pname, float param) {
        switch (pname) {
            case GL11.GL_FOG_END -> FogStateTracker.end = param;
            case GL11.GL_FOG_START -> FogStateTracker.start = param;
            case GL11.GL_FOG_DENSITY -> FogStateTracker.density = param;
        }
    }

    public static void glEnable(int cap) {
        if (cap == GL11.GL_FOG) {
            FogStateTracker.isEnabled = true;
        }
    }

    public static void glDisable(int cap) {
        if (cap == GL11.GL_FOG) {
            FogStateTracker.isEnabled = false;
        }
    }

    public static void glFogi(int pname, int param) {
        if (pname == GL11.GL_FOG_MODE) {
            FogStateTracker.mode = FogStateTracker.fromGLMode(param);
        }
    }

    static ChunkFogMode fromGLMode(int glEnum) {
        try {
            return ChunkFogMode.fromGLMode(glEnum);
        } catch (RuntimeException e) {
            if (ModuleConfig.Debug) {
                Share.log.warn("Failed to set fog:", e);
            }
            return ChunkFogMode.NONE;
        }
    }
}
