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

package com.ventooth.beddium.modules.TerrainRendering.ext;

import org.joml.Vector3d;

import net.minecraft.client.renderer.culling.ICamera;

public interface FrustrumExt {
    static FrustrumExt of(ICamera thiz) {
        if (thiz instanceof FrustrumExt ext) {
            return ext;
        }
        throw new IllegalArgumentException("Unsupported frustrum, some mod doing custom culling? Class: " + thiz.getClass());
    }

    default boolean beddium$isBoxInFrustum(float minX, float minY, float minZ, float maxX, float maxY, float maxZ) {
        return beddium$isBoxInFrustum(minX, minY, minZ, maxX, maxY, (double) maxZ);
    }

    boolean beddium$isBoxInFrustum(double minX, double minY, double minZ, double maxX, double maxY, double maxZ);

    Vector3d beddium$getPosition();
}
