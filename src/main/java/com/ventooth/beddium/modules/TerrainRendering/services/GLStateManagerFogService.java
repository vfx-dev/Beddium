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

package com.ventooth.beddium.modules.TerrainRendering.services;

import org.embeddedt.embeddium.impl.render.chunk.fog.FogService;
import org.embeddedt.embeddium.impl.render.chunk.shader.ChunkFogMode;
import org.lwjgl.opengl.GL11;

import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.EntityRenderer;

public class GLStateManagerFogService implements FogService {
    @Override
    public float getFogEnd() {
        return GL11.glGetInteger(GL11.GL_FOG_END);
    }

    @Override
    public float getFogStart() {
        return GL11.glGetInteger(GL11.GL_FOG_START);
    }

    @Override
    public float getFogDensity() {
        return GL11.glGetFloat(GL11.GL_FOG_DENSITY);
    }

    @Override
    public int getFogShapeIndex() {
        return 0;
    }

    @Override
    public float getFogCutoff() {
        return getFogEnd();
    }

    @Override
    public float[] getFogColor() {
        EntityRenderer entityRenderer = Minecraft.getMinecraft().entityRenderer;
        return new float[]{
                entityRenderer.fogColorRed,
                entityRenderer.fogColorGreen,
                entityRenderer.fogColorBlue,
                1.0F
        };
    }

    @Override
    public ChunkFogMode getFogMode() {
        if (!GL11.glGetBoolean(GL11.GL_FOG)) {
            return ChunkFogMode.NONE;
        }
        return ChunkFogMode.fromGLMode(GL11.glGetInteger(GL11.GL_FOG_MODE));
    }
}
