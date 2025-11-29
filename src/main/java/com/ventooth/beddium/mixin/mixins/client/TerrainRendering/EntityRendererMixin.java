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

import com.falsepattern.lib.util.MathUtil;
import com.llamalad7.mixinextras.injector.wrapoperation.Operation;
import com.llamalad7.mixinextras.injector.wrapoperation.WrapOperation;
import com.ventooth.beddium.config.TerrainRenderingConfig;
import com.ventooth.beddium.modules.TerrainRendering.fog.FogStateTracker;
import lombok.val;
import org.lwjgl.opengl.GL11;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

import net.minecraft.client.renderer.EntityRenderer;

import java.nio.FloatBuffer;

@Mixin(EntityRenderer.class)
public abstract class EntityRendererMixin {
    @Shadow
    private float farPlaneDistance;

    @Inject(method = "setupFog",
            at = @At(value = "RETURN"))
    private void applyFogBias(CallbackInfo ci) {
        val fogBias = (float) TerrainRenderingConfig.FastChunkDrawModeFogBias;
        if (TerrainRenderingConfig.ChunkDrawMode != TerrainRenderingConfig.DrawModeEnum.Fast || MathUtil.epsilonEquals(fogBias, 0F)) {
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
        val maxFogEnd = this.farPlaneDistance + fogBias;

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

    @WrapOperation(method = "setupFog",
                   at = @At(value = "INVOKE",
                            target = "Lorg/lwjgl/opengl/GL11;glFog(ILjava/nio/FloatBuffer;)V"))
    private void track_glFog(int pname, FloatBuffer params, Operation<Void> original) {
        if (TerrainRenderingConfig.FastFog) {
            FogStateTracker.glFog(pname, params);
        }
        original.call(pname, params);
    }

    @WrapOperation(method = "setupFog",
                   at = @At(value = "INVOKE",
                            target = "Lorg/lwjgl/opengl/GL11;glFogf(IF)V"))
    private void track_glFogf(int pname, float param, Operation<Void> original) {
        if (TerrainRenderingConfig.FastFog) {
            FogStateTracker.glFogf(pname, param);
        }
        original.call(pname, param);
    }

    @WrapOperation(method = "setupFog",
                   at = @At(value = "INVOKE",
                            target = "Lorg/lwjgl/opengl/GL11;glFogi(II)V"))
    private void track_glFogi(int pname, int param, Operation<Void> original) {
        if (TerrainRenderingConfig.FastFog) {
            FogStateTracker.glFogi(pname, param);
        }
        original.call(pname, param);
    }

    @WrapOperation(method = "renderWorld",
                   at = @At(value = "INVOKE",
                            target = "Lorg/lwjgl/opengl/GL11;glEnable(I)V"))
    private void track_glEnable(int cap, Operation<Void> original) {
        if (TerrainRenderingConfig.FastFog) {
            FogStateTracker.glEnable(cap);
        }
        original.call(cap);
    }

    @WrapOperation(method = "renderWorld",
                   at = @At(value = "INVOKE",
                            target = "Lorg/lwjgl/opengl/GL11;glDisable(I)V"))
    private void track_glDisable(int cap, Operation<Void> original) {
        if (TerrainRenderingConfig.FastFog) {
            FogStateTracker.glDisable(cap);
        }
        original.call(cap);
    }
}
