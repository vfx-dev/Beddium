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

import com.ventooth.beddium.modules.MEGAChunks.MegaChunkMetadata;
import org.objectweb.asm.Opcodes;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Redirect;

import net.minecraft.client.renderer.EntityRenderer;

@Mixin(EntityRenderer.class)
public class EntityRendererMixin {
    @Shadow private float farPlaneDistance;

    @Redirect(method = "setupCameraTransform",
              at = @At(value = "FIELD",
                       target = "Lnet/minecraft/client/renderer/EntityRenderer;farPlaneDistance:F",
                       opcode = Opcodes.PUTFIELD),
              require = 1)
    private void hackFarPlane(EntityRenderer instance, float value) {
        farPlaneDistance = Math.max(16, value - MegaChunkMetadata.BLOCKS_PER_WR_EDGE - 16);
    }
}
