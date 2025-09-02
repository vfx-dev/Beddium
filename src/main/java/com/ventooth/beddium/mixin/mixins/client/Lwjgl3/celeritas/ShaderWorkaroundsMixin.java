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

package com.ventooth.beddium.mixin.mixins.client.Lwjgl3.celeritas;

import com.ventooth.beddium.stubpackage.me.eigenraven.lwjgl3ify.api.Lwjgl3Aware;
import com.ventooth.beddium.stubpackage.org.lwjgl.PointerBuffer;
import com.ventooth.beddium.stubpackage.org.lwjgl.opengl.GL20C;
import com.ventooth.beddium.stubpackage.org.lwjgl.system.APIUtil;
import com.ventooth.beddium.stubpackage.org.lwjgl.system.MemoryStack;
import com.ventooth.beddium.stubpackage.org.lwjgl.system.MemoryUtil;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Overwrite;

import java.nio.ByteBuffer;

@Lwjgl3Aware
@Mixin(targets = "org.embeddedt.embeddium.impl.gl.shader.ShaderWorkarounds",
       remap = false)
public class ShaderWorkaroundsMixin {
    /**
     * @author FalsePattern
     * @reason Lwjgl3
     */
    @Overwrite
    static void safeShaderSource(int glId, CharSequence source) {
		try (MemoryStack stack = MemoryStack.stackPush()) {
			final ByteBuffer sourceBuffer = MemoryUtil.memUTF8(source, true);
			final PointerBuffer pointers = stack.mallocPointer(1);
			pointers.put(sourceBuffer);

			GL20C.nglShaderSource(glId, 1, pointers.address0(), 0);
			APIUtil.apiArrayFree(pointers.address0(), 1);
		}
    }
}
