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

package com.ventooth.beddium.mixin.mixins.client.TerrainRendering.compat.flenixcities;

import com.llamalad7.mixinextras.injector.v2.WrapWithCondition;
import com.llamalad7.mixinextras.injector.wrapmethod.WrapMethod;
import com.llamalad7.mixinextras.injector.wrapoperation.Operation;
import com.silvaniastudios.cities.core.client.renders.BlockRenderCore;
import com.silvaniastudios.cities.core.client.renders.CornerPostRenderer;
import com.silvaniastudios.cities.core.client.renders.LightBlockRenderer;
import com.silvaniastudios.cities.core.client.renders.LightBlockRotateRenderer;
import com.silvaniastudios.cities.core.client.renders.OpenStairsRenderer;
import com.silvaniastudios.cities.core.client.renders.RailingRenderer;
import com.silvaniastudios.cities.core.client.renders.Slope225HorizontalARenderer;
import com.silvaniastudios.cities.core.client.renders.Slope225HorizontalBRenderer;
import com.silvaniastudios.cities.core.client.renders.Slope225VerticalRenderer;
import com.silvaniastudios.cities.core.client.renders.Slope45Renderer;
import com.silvaniastudios.cities.core.client.renders.WalkwayFenceRenderer;
import com.silvaniastudios.cities.core.client.renders.WalkwayStairsRenderer;
import com.silvaniastudios.cities.core.client.renders.WalkwayStairsTraditionalRenderer;
import com.silvaniastudios.cities.core.client.renders.WalkwayTraditionalRenderer;
import com.ventooth.beddium.modules.TerrainRendering.compat.ItemRenderingTracker;
import org.spongepowered.asm.mixin.Dynamic;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;

import net.minecraft.block.Block;
import net.minecraft.client.renderer.RenderBlocks;
import net.minecraft.client.renderer.Tessellator;

@Mixin(value = {
        BlockRenderCore.class,
        CornerPostRenderer.class,
        LightBlockRenderer.class,
        LightBlockRotateRenderer.class,
        OpenStairsRenderer.class,
        RailingRenderer.class,
        Slope45Renderer.class,
        Slope225HorizontalARenderer.class,
        Slope225HorizontalBRenderer.class,
        Slope225VerticalRenderer.class,
        WalkwayFenceRenderer.class,
        WalkwayStairsRenderer.class,
        WalkwayStairsTraditionalRenderer.class,
        WalkwayTraditionalRenderer.class,
},
       remap = false,
       priority = 10_000)
public abstract class ISBRHMixin {
    @Dynamic
    @WrapMethod(method = "renderInventoryBlock")
    private void track_InventoryRender(Block block, int metadata, int modelId, RenderBlocks renderer, Operation<Void> original) {
        ItemRenderingTracker.preRenderItem();
        try {
            original.call(block, metadata, modelId, renderer);
        } finally {
            ItemRenderingTracker.postRenderItem();
        }
    }

    @Dynamic
    @WrapWithCondition(method = "*",
                       at = @At(value = "INVOKE",
                                target = "Lnet/minecraft/client/renderer/Tessellator;draw()I",
                                remap = true),
                       expect = -1)
    private boolean snip_tessDraw(Tessellator instance) {
        return ItemRenderingTracker.isRenderingItem();
    }

    @Dynamic
    @WrapWithCondition(method = "*",
                       at = @At(value = "INVOKE",
                                target = "Lnet/minecraft/client/renderer/Tessellator;startDrawingQuads()V",
                                remap = true),
                       expect = -1)
    private boolean snip_tessStartDrawingQuads(Tessellator instance) {
        return ItemRenderingTracker.isRenderingItem();
    }
}
