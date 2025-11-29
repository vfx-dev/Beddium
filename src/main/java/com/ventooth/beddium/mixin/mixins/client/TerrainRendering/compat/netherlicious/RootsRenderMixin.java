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

package com.ventooth.beddium.mixin.mixins.client.TerrainRendering.compat.netherlicious;

import DelirusCrux.Netherlicious.Client.Render.Block.RootsRender;
import com.llamalad7.mixinextras.injector.v2.WrapWithCondition;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;

import net.minecraft.client.renderer.Tessellator;

@Mixin(RootsRender.class)
public abstract class RootsRenderMixin {
    @WrapWithCondition(method = "renderWorldBlock",
                       at = @At(value = "INVOKE",
                                target = "Lnet/minecraft/client/renderer/Tessellator;draw()I")
                       /*require = 1*/)
    private boolean snip_TessellatorDraw(Tessellator instance) {
        return false;
    }

    @WrapWithCondition(method = "renderWorldBlock",
                       at = @At(value = "INVOKE",
                                target = "Lnet/minecraft/client/renderer/Tessellator;startDrawingQuads()V")
                       /*require = 1*/)
    private boolean snip_TessellatorStartDrawingQuads(Tessellator instance) {
        return false;
    }
}
