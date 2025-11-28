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

package com.ventooth.beddium.mixin.mixins.client.TerrainRendering;

import com.llamalad7.mixinextras.injector.wrapoperation.Operation;
import com.llamalad7.mixinextras.injector.wrapoperation.WrapOperation;
import com.ventooth.beddium.modules.TerrainRendering.services.GLStateManagerFogServiceFast;
import org.lwjgl.opengl.GL11;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

import net.minecraft.client.renderer.EntityRenderer;

import java.nio.FloatBuffer;

@Mixin(EntityRenderer.class)
public abstract class EntityRendererFastFogMixin {
    @Inject(method = "setupFog",
            at = @At(value = "HEAD"),
            require = 1)
    private void resetFog(int startCoords, float partialTicks, CallbackInfo ci) {
        // TODO: Setup fog to a known safe state in case a mod messes it up
    }

    @WrapOperation(method = "setupFog",
                   at = @At(value = "INVOKE",
                            target = "Lorg/lwjgl/opengl/GL11;glFog(ILjava/nio/FloatBuffer;)V"),
                   require = 2)
    private void glFog(int pname, FloatBuffer params, Operation<Void> original) {
        original.call(pname, GLStateManagerFogServiceFast.setColor(params));
    }

    @WrapOperation(method = "setupFog",
              at = @At(value = "INVOKE",
                       target = "Lorg/lwjgl/opengl/GL11;glFogf(IF)V"))
    private void glFogf(int pname, float param, Operation<Void> original) {
        switch (pname) {
            case GL11.GL_FOG_START -> GLStateManagerFogServiceFast.fogStart = param;
            case GL11.GL_FOG_END -> GLStateManagerFogServiceFast.fogEnd = param;
            case GL11.GL_FOG_DENSITY -> GLStateManagerFogServiceFast.fogDensity = param;
        }
        original.call(pname, param);
    }

    @WrapOperation(method = "setupFog",
              at = @At(value = "INVOKE",
                       target = "Lorg/lwjgl/opengl/GL11;glFogi(II)V"))
    private void glFogi(int pname, int param, Operation<Void> original) {
        if (pname == GL11.GL_FOG_MODE) {
            GLStateManagerFogServiceFast.fogMode = param;
        }
        original.call(pname, param);
    }

    @WrapOperation(method = "renderWorld",
              at = @At(value = "INVOKE",
                       target = "Lorg/lwjgl/opengl/GL11;glEnable(I)V"))
    private void enableFog(int cap, Operation<Void> original) {
        if (cap == GL11.GL_FOG) {
            GLStateManagerFogServiceFast.fog = true;
        }
        original.call(cap);
    }


    @WrapOperation(method = "renderWorld",
              at = @At(value = "INVOKE",
                       target = "Lorg/lwjgl/opengl/GL11;glDisable(I)V"))
    private void disableFog(int cap, Operation<Void> original) {
        if (cap == GL11.GL_FOG) {
            GLStateManagerFogServiceFast.fog = false;
        }
        original.call(cap);
    }
}
