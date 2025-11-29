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

import com.llamalad7.mixinextras.injector.wrapoperation.Operation;
import com.llamalad7.mixinextras.injector.wrapoperation.WrapOperation;
import com.ventooth.beddium.Compat;
import com.ventooth.beddium.Share;
import com.ventooth.beddium.modules.TerrainRendering.CameraHelper;
import com.ventooth.beddium.modules.TerrainRendering.CeleritasWorldRenderer;
import com.ventooth.beddium.modules.TerrainRendering.TerrainRenderingModule;
import com.ventooth.beddium.modules.TerrainRendering.ext.FrustrumExt;
import com.ventooth.beddium.modules.TerrainRendering.ext.RenderGlobalExt;
import lombok.val;
import org.embeddedt.embeddium.impl.gl.device.RenderDevice;
import org.embeddedt.embeddium.impl.render.viewport.Viewport;
import org.lwjgl.opengl.GL11;
import org.objectweb.asm.Opcodes;
import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Overwrite;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Constant;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.ModifyConstant;
import org.spongepowered.asm.mixin.injection.Redirect;
import org.spongepowered.asm.mixin.injection.Slice;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

import net.minecraft.client.Minecraft;
import net.minecraft.client.multiplayer.WorldClient;
import net.minecraft.client.renderer.DestroyBlockProgress;
import net.minecraft.client.renderer.OpenGlHelper;
import net.minecraft.client.renderer.RenderGlobal;
import net.minecraft.client.renderer.RenderHelper;
import net.minecraft.client.renderer.culling.ICamera;
import net.minecraft.client.renderer.tileentity.TileEntityRendererDispatcher;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.AxisAlignedBB;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

// TODO: Why is the priority what it is? Jdoc.
@Mixin(value = RenderGlobal.class,
       priority = 900)
public abstract class RenderGlobalMixin implements RenderGlobalExt {
    @Shadow
    private Minecraft mc;
    @Shadow
    @Final
    private Map<Integer, DestroyBlockProgress> damagedBlocks;

    @Unique
    private CeleritasWorldRenderer celeritas$worldRenderer;
    @Unique
    private int celeritas$frame = 0;
    @Unique
    private List<TileEntity> celeritas$renderableBlockEntities = new ArrayList<>();

    @Redirect(method = "loadRenderers",
              at = @At(opcode = Opcodes.GETFIELD,
                       value = "FIELD",
                       target = "Lnet/minecraft/client/renderer/RenderGlobal;renderDistanceChunks:I",
                       ordinal = 0))
    private int nullifyBuiltChunkStorage(RenderGlobal self) {
        // Do not allow any resources to be allocated
        return 0;
    }

    // Note that we still end up with an array with a single (useless) WorldRenderer just chilling in there
    @ModifyConstant(method = "loadRenderers",
                    constant = @Constant(intValue = 16,
                                         ordinal = 0),
                    require = 1)
    private int nullifyBuiltChunkStorage2(int constant) {
        // Do not allow any resources to be allocated
        return 1;
    }

    @Inject(method = "<init>",
            at = @At("RETURN"))
    private void init(Minecraft minecraft, CallbackInfo ci) {
        // Cannot be initialized from the variable declaration
        celeritas$worldRenderer = new CeleritasWorldRenderer(minecraft);
    }

    /**
     * Runs after render global has been initialized with a new world, does not handle de-init
     */
    @Inject(method = "setWorldAndLoadRenderers",
            at = @At("RETURN"))
    private void onWorldLoad(WorldClient world, CallbackInfo ci) {
        RenderDevice.enterManagedCode();
        try {
            celeritas$worldRenderer.setWorld(world);
        } finally {
            RenderDevice.exitManagedCode();
        }
    }

    @Inject(method = "onStaticEntitiesChanged",
            at = @At("RETURN"))
    private void onTerrainUpdateScheduled(CallbackInfo ci) {
        celeritas$worldRenderer.scheduleTerrainUpdate();
    }

    /**
     * @reason Redirect the chunk layer render passes to our renderer
     * @author JellySquid
     */
    @Overwrite
    public int sortAndRender(EntityLivingBase viewEntity, int pass, double partialTicks) {
        // Allow FalseTweaks mixin to replace constant
        // TODO: WHAT FALSETWEAKS MIXIN?!?!?
        @SuppressWarnings("unused") double magicSortingConstantValue = 1D;

        RenderDevice.enterManagedCode();

        RenderHelper.disableStandardItemLighting();
        OpenGlHelper.setActiveTexture(OpenGlHelper.defaultTexUnit);
        GL11.glBindTexture(GL11.GL_TEXTURE_2D, this.mc.getTextureMapBlocks().getGlTextureId());
        GL11.glEnable(GL11.GL_TEXTURE_2D);

        // Here so that SwanSong can land it's injections
        if (Boolean.valueOf(false)) {
            // It wants 3 TOTAL ENABLES (one provided above)
            //            GL11.glEnable(0);
            GL11.glEnable(0);
            GL11.glEnable(0);

            // And 4 Disables
            GL11.glDisable(0);
            GL11.glDisable(0);
            GL11.glDisable(0);
            GL11.glDisable(0);
        }

        this.mc.entityRenderer.enableLightmap(partialTicks);

        if (TerrainRenderingModule.DEBUG_WIREFRAME_MODE) {
            TerrainRenderingModule.toggleWireframe(true);
        }

        try {
            var camPosition = CameraHelper.getCurrentCameraPosition(partialTicks);
            celeritas$worldRenderer.drawChunkLayer(pass, camPosition.x, camPosition.y, camPosition.z);
        } finally {
            if (TerrainRenderingModule.DEBUG_WIREFRAME_MODE) {
                TerrainRenderingModule.toggleWireframe(false);
            }
            RenderDevice.exitManagedCode();
        }

        this.mc.entityRenderer.disableLightmap(partialTicks);
        return 1;
    }

    /**
     * @reason Redirect the terrain setup phase to our renderer
     * @author JellySquid
     */
    @Overwrite
    public void clipRenderersByFrustum(ICamera camera, float partialTick) {
        RenderDevice.enterManagedCode();
        try {
            final Viewport viewport;
            if (Compat.isSwansongInitialized() && Compat.shadowPassActive()) {
                viewport = celeritas$createShadowViewport(FrustrumExt.of(camera), partialTick);
            } else {
                viewport = celeritas$createViewport(FrustrumExt.of(camera));
            }
            celeritas$worldRenderer.setupTerrain(viewport, partialTick, celeritas$frame++, this.mc.thePlayer.noClip, false);
        } finally {
            RenderDevice.exitManagedCode();
        }
    }

    @Unique
    private static Viewport celeritas$createShadowViewport(FrustrumExt frustrum, float partialTick) {
        val pos = CameraHelper.getCurrentCameraPosition(partialTick);
        // @formatter:off
        return new Viewport((minX, minY, minZ, maxX, maxY, maxZ) ->
                            frustrum.beddium$isBoxInFrustum(minX + pos.x, minY + pos.y, minZ + pos.z, maxX + pos.x, maxY + pos.y, maxZ + pos.z),
                            pos);
        // @formatter:on
    }

    @Unique
    private static Viewport celeritas$createViewport(FrustrumExt frustrum) {
        return new Viewport(frustrum::beddium$isBoxInFrustum, frustrum.beddium$getPosition());
    }

    /**
     * @reason Redirect chunk updates to our renderer
     * @author JellySquid
     */
    @Overwrite
    public void markBlocksForUpdate(int minX, int minY, int minZ, int maxX, int maxY, int maxZ) {
        celeritas$worldRenderer.scheduleRebuildForBlockArea(minX, minY, minZ, maxX, maxY, maxZ, false);
    }

    @Inject(method = "loadRenderers",
            at = @At("RETURN"))
    private void onReload(CallbackInfo ci) {
        RenderDevice.enterManagedCode();
        try {
            celeritas$worldRenderer.reload();
        } finally {
            RenderDevice.exitManagedCode();
        }
    }

    @Inject(method = "renderEntities",
            at = @At(value = "INVOKE",
                     target = "Lnet/minecraft/client/renderer/RenderHelper;enableStandardItemLighting()V",
                     shift = At.Shift.AFTER,
                     ordinal = 0))
    public void renderTileEntities(EntityLivingBase p_147589_1_, ICamera p_147589_2_, float partialTicks, CallbackInfo ci) {
        celeritas$renderableBlockEntities.clear();
        celeritas$worldRenderer.getRenderableBlockEntities(celeritas$renderableBlockEntities);
    }

    @Redirect(method = "renderEntities",
              at = @At(value = "FIELD",
                       target = "Lnet/minecraft/client/renderer/RenderGlobal;tileEntities:Ljava/util/List;"),
              require = 2)
    private List<TileEntity> hijackTEList(RenderGlobal instance) {
        return celeritas$renderableBlockEntities;
    }

    @Redirect(method = "renderEntities",
              at = @At(value = "INVOKE",
                       target = "Lnet/minecraft/tileentity/TileEntity;getRenderBoundingBox()Lnet/minecraft/util/AxisAlignedBB;"),
              require = 1)
    private AxisAlignedBB noBB(TileEntity instance) {
        return null;
    }

    @Redirect(method = "renderEntities",
              at = @At(value = "INVOKE",
                       target = "Lnet/minecraft/client/renderer/culling/ICamera;isBoundingBoxInFrustum(Lnet/minecraft/util/AxisAlignedBB;)Z",
                       ordinal = 0),
              slice = @Slice(from = @At(value = "FIELD",
                                        target = "Lnet/minecraft/client/renderer/RenderGlobal;tileEntities:Ljava/util/List;",
                                        ordinal = 0)),
              require = 1)
    private boolean noBB2(ICamera instance, AxisAlignedBB axisAlignedBB) {
        return true;
    }

    @Redirect(method = "renderEntities",
              at = @At(value = "INVOKE",
                       target = "Lnet/minecraft/tileentity/TileEntity;shouldRenderInPass(I)Z"),
              require = 1)
    private boolean customShouldRender(TileEntity instance, int pass) {
        return celeritas$worldRenderer.shouldRenderBlockEntity(instance, pass);
    }

    @WrapOperation(method = "renderEntities",
                   at = @At(value = "INVOKE",
                            target = "Lnet/minecraft/client/renderer/tileentity/TileEntityRendererDispatcher;renderTileEntity(Lnet/minecraft/tileentity/TileEntity;F)V"),
                   require = 1)
    private void safeRenderTileEntity(TileEntityRendererDispatcher instance, TileEntity te, float partialTick, Operation<Void> original) {
        try {
            original.call(instance, te, partialTick);
        } catch (RuntimeException e) {
            if (te.isInvalid()) {
                Share.log.error("Suppressing crash from invalid tile entity", e);
            } else {
                throw e;
            }
        }
    }

    /**
     * @reason Replace the debug string
     * @author JellySquid
     */
    @Overwrite
    public String getDebugInfoRenders() {
        return celeritas$worldRenderer.getChunksDebugString();
    }

    @Override
    public CeleritasWorldRenderer celeritas$worldRenderer() {
        return celeritas$worldRenderer;
    }
}

