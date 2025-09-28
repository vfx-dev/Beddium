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

public class GLStateManagerFogServiceFast implements FogService {
    public static final float[] color = new float[4];
    public static boolean fog;
    public static float fogEnd;
    public static float fogStart;
    public static float fogDensity;
    public static int fogMode;

    @Override
    public float getFogEnd() {
        return fogEnd;
    }

    @Override
    public float getFogStart() {
        return fogStart;
    }

    @Override
    public float getFogDensity() {
        return fogDensity;
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
        return color;
    }

    @Override
    public ChunkFogMode getFogMode() {
        if (!fog) {
            return ChunkFogMode.NONE;
        }
        return ChunkFogMode.fromGLMode(fogMode);
    }
}
