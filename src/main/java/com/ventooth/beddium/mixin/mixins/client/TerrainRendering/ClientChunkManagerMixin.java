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

import org.embeddedt.embeddium.impl.render.chunk.map.ChunkStatus;
import org.embeddedt.embeddium.impl.render.chunk.map.ChunkTrackerHolder;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

import net.minecraft.client.multiplayer.ChunkProviderClient;
import net.minecraft.world.World;
import net.minecraft.world.chunk.Chunk;

@Mixin(ChunkProviderClient.class)
public abstract class ClientChunkManagerMixin {
    @Shadow
    private World worldObj;

    @Inject(method = "loadChunk",
            at = @At("RETURN"))
    private void afterLoadChunkFromPacket(int x, int z, CallbackInfoReturnable<Chunk> cir) {
        ChunkTrackerHolder.get(this.worldObj).onChunkStatusAdded(x, z, ChunkStatus.FLAG_ALL);
    }

    @Inject(method = "unloadChunk",
            at = @At(value = "INVOKE",
                     target = "Lnet/minecraft/world/chunk/Chunk;onChunkUnload()V",
                     shift = At.Shift.AFTER))
    private void afterUnloadChunk(int x, int z, CallbackInfo ci) {
        ChunkTrackerHolder.get(this.worldObj).onChunkStatusRemoved(x, z, ChunkStatus.FLAG_ALL);
    }
}

