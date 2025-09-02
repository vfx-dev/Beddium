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

import com.ventooth.beddium.stubpackage.org.lwjgl.opengl.GL32C;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Overwrite;

@Mixin(targets = "org.embeddedt.embeddium.impl.gl.functions.MultidrawFunctions$3")
public class MultidrawFunctionsMixin {
    /**
     * @author FalsePattern
     * @reason Lwjgl3
     */
    @Overwrite(remap = false)
    public void multiDrawElementsBaseVertex(int mode, long pCount, int type, long pIndices, int size, long pBaseVertex) {
        GL32C.nglMultiDrawElementsBaseVertex(mode, pCount, type, pIndices, size, pBaseVertex);
    }
}
