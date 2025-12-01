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

package com.ventooth.beddium.mixin.mixins.client.TerrainRendering.compat;

import com.llamalad7.mixinextras.injector.wrapmethod.WrapMethod;
import com.llamalad7.mixinextras.injector.wrapoperation.Operation;
import com.ventooth.beddium.Share;
import com.ventooth.beddium.config.TerrainRenderingConfig;
import com.ventooth.beddium.modules.TerrainRendering.compat.LockableTess;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Unique;

import net.minecraft.client.renderer.Tessellator;
import net.minecraft.client.shader.TesselatorVertexState;

@Mixin(value = Tessellator.class,
       priority = 10_000)
public abstract class LockableTessellatorMixin implements LockableTess {
    @Unique
    private boolean beddium$locked = false;

    @WrapMethod(method = "draw",
                require = 1)
    private int lock_draw(Operation<Integer> original) {
        if (beddium$checkLock()) {
            return original.call();
        }
        return 0;
    }

    @WrapMethod(method = "setVertexState",
                require = 1)
    private void lock_setVertexState(TesselatorVertexState state, Operation<Void> original) {
        if (beddium$checkLock()) {
            original.call(state);
        }
    }

    @WrapMethod(method = "reset",
                require = 1)
    private void lock_reset(Operation<Void> original) {
        if (beddium$checkLock()) {
            original.call();
        }
    }

    @WrapMethod(method = "startDrawingQuads",
                require = 1)
    private void lock_startDrawingQuads(Operation<Void> original) {
        if (beddium$checkLock()) {
            original.call();
        }
    }

    @WrapMethod(method = "startDrawing",
                require = 1)
    private void lock_startDrawing(int format, Operation<Void> original) {
        if (beddium$checkLock()) {
            original.call(format);
        }
    }

    @WrapMethod(method = "disableColor",
                require = 1)
    private void lock_disableColor(Operation<Void> original) {
        if (beddium$checkLock()) {
            original.call();
        }
    }

    @WrapMethod(method = "setTranslation",
                require = 1)
    private void lock_setTranslation(double x, double y, double z, Operation<Void> original) {
        if (beddium$checkLock()) {
            original.call(x, y, z);
        }
    }

    @WrapMethod(method = "addTranslation",
                require = 1)
    private void lock_addTranslation(float x, float y, float z, Operation<Void> original) {
        if (beddium$checkLock()) {
            original.call(x, y, z);
        }
    }

    @Override
    public void beddium$lock() {
        beddium$locked = true;
    }

    @Override
    public void beddium$unlock() {
        beddium$locked = false;
    }

    @Unique
    private boolean beddium$checkLock() {
        if (beddium$locked) {
            Share.log.error("Erm..", new IllegalStateException("Angry as fugg! The tess is LOCKED!!!!"));
            return false;
        } else {
            return true;
        }
    }
}
