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

import com.ventooth.beddium.modules.TerrainRendering.services.GLStateManagerFogServiceFast;
import org.lwjgl.opengl.GL11;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Redirect;

import net.minecraft.client.renderer.EntityRenderer;

import java.nio.FloatBuffer;

@Mixin(EntityRenderer.class)
public abstract class EntityRendererFastFogMixin {
    @Shadow
    protected abstract FloatBuffer setFogColorBuffer(float red, float green, float blue, float alpha);

    @Redirect(method = "setupFog",
              at = @At(value = "INVOKE",
                       target = "Lnet/minecraft/client/renderer/EntityRenderer;setFogColorBuffer(FFFF)Ljava/nio/FloatBuffer;"))
    private FloatBuffer glFog(EntityRenderer instance, float red, float green, float blue, float alpha) {
        GLStateManagerFogServiceFast.color[0] = red;
        GLStateManagerFogServiceFast.color[1] = green;
        GLStateManagerFogServiceFast.color[2] = blue;
        GLStateManagerFogServiceFast.color[3] = alpha;
        return setFogColorBuffer(red, green, blue, alpha);
    }

    @Redirect(method = "setupFog",
              at = @At(value = "INVOKE",
                       target = "Lorg/lwjgl/opengl/GL11;glFogf(IF)V"))
    private void glFogf(int pname, float param) {
        switch (pname) {
            case GL11.GL_FOG_START -> GLStateManagerFogServiceFast.fogStart = param;
            case GL11.GL_FOG_END -> GLStateManagerFogServiceFast.fogEnd = param;
            case GL11.GL_FOG_DENSITY -> GLStateManagerFogServiceFast.fogDensity = param;
        }
        GL11.glFogf(pname, param);
    }

    @Redirect(method = "setupFog",
              at = @At(value = "INVOKE",
                       target = "Lorg/lwjgl/opengl/GL11;glFogi(II)V"))
    private void glFogi(int pname, int param) {
        if (pname == GL11.GL_FOG_MODE) {
            GLStateManagerFogServiceFast.fogMode = param;
        }
        GL11.glFogi(pname, param);
    }

    @Redirect(method = "renderWorld",
              at = @At(value = "INVOKE",
                       target = "Lorg/lwjgl/opengl/GL11;glEnable(I)V"))
    private void enableFog(int cap) {
        if (cap == GL11.GL_FOG) {
            GLStateManagerFogServiceFast.fog = true;
        }
        GL11.glEnable(cap);
    }


    @Redirect(method = "renderWorld",
              at = @At(value = "INVOKE",
                       target = "Lorg/lwjgl/opengl/GL11;glDisable(I)V"))
    private void disableFog(int cap) {
        if (cap == GL11.GL_FOG) {
            GLStateManagerFogServiceFast.fog = false;
        }
        GL11.glDisable(cap);
    }
}
