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

import lombok.NoArgsConstructor;
import lombok.val;
import org.embeddedt.embeddium.impl.render.chunk.fog.FogService;
import org.embeddedt.embeddium.impl.render.chunk.shader.ChunkFogMode;

@NoArgsConstructor
public final class FogStateService implements FogService {
    @Override
    public float getFogEnd() {
        return FogStateTracker.end;
    }

    @Override
    public float getFogStart() {
        return FogStateTracker.start;
    }

    @Override
    public float getFogDensity() {
        return FogStateTracker.density;
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
        val color = FogStateTracker.color;
        return new float[]{color.get(0), color.get(1), color.get(2), color.get(3)};
    }

    @Override
    public ChunkFogMode getFogMode() {
        return FogStateTracker.isEnabled ? FogStateTracker.mode : ChunkFogMode.NONE;
    }
}
