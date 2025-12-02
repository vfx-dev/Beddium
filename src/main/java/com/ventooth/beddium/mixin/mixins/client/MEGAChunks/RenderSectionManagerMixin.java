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

package com.ventooth.beddium.mixin.mixins.client.MEGAChunks;

import it.unimi.dsi.fastutil.objects.Object2ObjectOpenHashMap;
import org.embeddedt.embeddium.impl.gl.profiling.TimerQueryManager;
import org.embeddedt.embeddium.impl.render.chunk.RenderSectionManager;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Redirect;

import java.util.function.Function;

@Mixin(value = RenderSectionManager.class,
       remap = false)
public abstract class RenderSectionManagerMixin {
    @Redirect(method = "renderLayer",
              at = @At(value = "INVOKE",
                       target = "Lorg/embeddedt/embeddium/impl/gl/profiling/TimerQueryManager;startProfiling()V"),
              require = 1)
    private void kill1(TimerQueryManager instance) {

    }

    @Redirect(method = "renderLayer",
              at = @At(value = "INVOKE",
                       target = "Lorg/embeddedt/embeddium/impl/gl/profiling/TimerQueryManager;finishProfiling()V"),
              require = 1)
    private void kill2(TimerQueryManager instance) {

    }

    @Redirect(method = "renderLayer",
              at = @At(value = "INVOKE",
                       target = "Lit/unimi/dsi/fastutil/objects/Object2ObjectOpenHashMap;computeIfAbsent(Ljava/lang/Object;Ljava/util/function/Function;)Ljava/lang/Object;"),
              require = 1)
    private Object kill3(Object2ObjectOpenHashMap instance, Object o, Function function) {
        return null;
    }
}
