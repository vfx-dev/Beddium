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

package com.ventooth.beddium.mixin.mixins.client.TerrainRendering.compat.ichundoors;

import com.ventooth.beddium.modules.TerrainRendering.ext.FrustrumExt;
import org.joml.Vector3d;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import us.ichun.mods.doors.client.render.culling.Frustrum;

import net.minecraft.client.renderer.culling.ClippingHelper;

@Mixin(value = Frustrum.class,
       remap = false)
public abstract class FrustrumMixin implements FrustrumExt {
    @Shadow
    private ClippingHelper clippingHelper;

    @Shadow
    private double xPosition;
    @Shadow
    private double yPosition;
    @Shadow
    private double zPosition;

    @Override
    public boolean beddium$isBoxInFrustum(double minX, double minY, double minZ, double maxX, double maxY, double maxZ) {
        return true; //TODO: This makes it work a little better, but it's still broken af
    }

    @Override
    public Vector3d beddium$getPosition() {
        return new Vector3d(this.xPosition, this.yPosition, this.zPosition);
    }
}
