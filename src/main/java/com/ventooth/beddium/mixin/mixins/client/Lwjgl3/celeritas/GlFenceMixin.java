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
import com.ventooth.beddium.stubpackage.org.lwjgl.opengl.GL32C;
import com.ventooth.beddium.stubpackage.org.lwjgl.system.MemoryStack;
import com.ventooth.beddium.stubpackage.org.lwjglx.opengl.GLSync;
import org.embeddedt.embeddium.impl.gl.sync.GlFence;
import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Overwrite;
import org.spongepowered.asm.mixin.Shadow;

import java.nio.IntBuffer;

@Lwjgl3Aware
@Mixin(value = GlFence.class,
       remap = false)
public abstract class GlFenceMixin {
    @Final
    @Shadow
    private Object id;
    @Shadow
    private boolean disposed;

    @Shadow
    protected abstract void checkDisposed();

    /**
     * @author FalsePattern
     * @reason Lwjgl3
     */
    @Overwrite
    public boolean isCompleted() {
        this.checkDisposed();

        int result;

        try (MemoryStack stack = MemoryStack.stackPush()) {
            IntBuffer count = stack.callocInt(1);
            result = GL32C.glGetSynci(((GLSync) this.id).getPointer(), GL32C.GL_SYNC_STATUS, count);

            if (count.get(0) != 1) {
                throw new RuntimeException("glGetSync returned more than one value");
            }
        }

        return result == GL32C.GL_SIGNALED;
    }

    /**
     * @author FalsePattern
     * @reason Lwjgl3
     */
    @Overwrite
    public void sync(long timeout) {
        this.checkDisposed();
        GL32C.glWaitSync(((GLSync) this.id).getPointer(), GL32C.GL_SYNC_FLUSH_COMMANDS_BIT, timeout);
    }

    /**
     * @author FalsePattern
     * @reason Lwjgl3
     */
    @Overwrite
    public void delete() {
        GL32C.glDeleteSync(((GLSync) this.id).getPointer());
        this.disposed = true;
    }
}
