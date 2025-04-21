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

import com.falsepattern.lib.util.RenderUtil;
import com.ventooth.beddium.Share;
import com.ventooth.beddium.modules.MEGAChunks.MegaChunkMetadata;
import com.ventooth.beddium.modules.TerrainRendering.CameraHelper;
import com.ventooth.beddium.modules.TerrainRendering.ext.RenderGlobalExt;
import com.ventooth.swansong.shader.ClippingHelperShadow;
import com.ventooth.swansong.shader.ShaderEngine;
import lombok.val;
import org.embeddedt.embeddium.impl.gl.device.RenderDevice;
import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Overwrite;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Redirect;

import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.RenderGlobal;
import net.minecraft.client.renderer.WorldRenderer;
import net.minecraft.client.renderer.culling.Frustrum;

@Mixin(value = ShaderEngine.class,
       remap = false)
public abstract class ShaderEngineMixin {
    @Final
    @Shadow
    private static ClippingHelperShadow ch;

    @Final
    @Shadow
    private static Frustrum frustrum;

    /**
     * @author Ven
     * @reason This is our way of doing API
     */
    @Overwrite
    private static void clipRenderersByFrustumShadow(WorldRenderer[] wrs) {
        Minecraft.getMinecraft().renderGlobal.clipRenderersByFrustum(frustrum, RenderUtil.partialTick());
    }

    /**
     * @author Ven
     * @reason This is our way of doing API
     */
    @Overwrite
    private static void addWorldToShadowReceivers(WorldRenderer[] wrs) {
        val rg = Minecraft.getMinecraft().renderGlobal;
        val actualWr = RenderGlobalExt.of(rg).celeritas$worldRenderer();

        val renderLists = actualWr.getRenderSectionManager().getNonShadowRenderLists();
        val renderListIterator = renderLists.iterator();

        while (renderListIterator.hasNext()) {
            val renderList = renderListIterator.next();

            val renderRegion = renderList.getRegion();
            val renderSectionIterator = renderList.sectionsWithGeometryIterator(false);

            if (renderSectionIterator == null) {
                continue;
            }

            while (renderSectionIterator.hasNext()) {
                val renderSectionId = renderSectionIterator.nextByteAsInt();
                val renderSection = renderRegion.getSection(renderSectionId);

                if (renderSection == null) {
                    continue;
                }

                val minX = renderSection.getOriginX();
                val minY = renderSection.getOriginY();
                val minZ = renderSection.getOriginZ();

                val maxX = minX + MegaChunkMetadata.BLOCKS_PER_WR_EDGE;
                val maxY = minY + MegaChunkMetadata.BLOCKS_PER_WR_EDGE;
                val maxZ = minZ + MegaChunkMetadata.BLOCKS_PER_WR_EDGE;

                ch.addShadowReceiver(minX, minY, minZ, maxX, maxY, maxZ);
            }
        }
    }

    @Redirect(method = "renderShadowMap",
              at = @At(value = "INVOKE",
                       target = "Lnet/minecraft/client/renderer/RenderGlobal;renderSortedRenderers(IIID)I",
                       remap = true),
              require = 2)
    private static int renderProper(RenderGlobal instance, int firstWr, int numWrs, int pass, double partialTick) {
        val actualWr = RenderGlobalExt.of(instance).celeritas$worldRenderer();

        RenderDevice.enterManagedCode();
        try {
            var camPosition = CameraHelper.getCurrentCameraPosition(partialTick);
            val posX = camPosition.x;
            val posY = camPosition.y;
            val posZ = camPosition.z;

            actualWr.drawChunkLayer(pass, posX, posY, posZ);
        } catch (Exception e) {
            Share.log.warn("Failed to render shadow pass...");
            Share.log.trace("Trace: ", e);
        } finally {
            RenderDevice.exitManagedCode();
        }

        return 0;
    }
}
