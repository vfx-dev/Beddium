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

package com.ventooth.beddium.api.task;

import com.ventooth.beddium.Compat;
import com.ventooth.beddium.Share;
import com.ventooth.beddium.api.cache.StateAwareCache;
import com.ventooth.beddium.config.TerrainRenderingConfig;
import com.ventooth.beddium.mixin.mixins.client.TerrainRendering.ForgeHooksClientMixin;
import com.ventooth.beddium.modules.BiomeColorCache.BiomeColorCacheModule;
import com.ventooth.beddium.modules.MEGAChunks.MEGASectionVisibilityBuilder;
import com.ventooth.beddium.modules.TerrainRendering.CeleritasWorldRenderer;
import com.ventooth.beddium.modules.TerrainRendering.TerrainRenderingModule;
import com.ventooth.beddium.modules.TerrainRendering.compile.ArchaicChunkBuildContext;
import it.unimi.dsi.fastutil.ints.IntArrayList;
import it.unimi.dsi.fastutil.ints.IntList;
import it.unimi.dsi.fastutil.objects.Reference2ReferenceMap;
import lombok.RequiredArgsConstructor;
import lombok.val;
import mega.fluidlogged.api.FLBlockAccess;
import org.embeddedt.embeddium.impl.render.chunk.RenderSection;
import org.embeddedt.embeddium.impl.render.chunk.compile.ChunkBuildBuffers;
import org.embeddedt.embeddium.impl.render.chunk.compile.ChunkBuildContext;
import org.embeddedt.embeddium.impl.render.chunk.compile.ChunkBuildOutput;
import org.embeddedt.embeddium.impl.render.chunk.compile.tasks.ChunkBuilderTask;
import org.embeddedt.embeddium.impl.render.chunk.data.BuiltSectionMeshParts;
import org.embeddedt.embeddium.impl.render.chunk.data.MinecraftBuiltRenderSectionData;
import org.embeddedt.embeddium.impl.render.chunk.occlusion.SectionVisibilityBuilder;
import org.embeddedt.embeddium.impl.render.chunk.terrain.TerrainRenderPass;
import org.embeddedt.embeddium.impl.util.task.CancellationToken;
import org.jetbrains.annotations.Nullable;
import org.joml.Vector3d;

import net.minecraft.block.Block;
import net.minecraft.block.material.Material;
import net.minecraft.client.renderer.RenderBlocks;
import net.minecraft.client.renderer.Tessellator;
import net.minecraft.client.renderer.texture.TextureAtlasSprite;
import net.minecraft.client.renderer.tileentity.TileEntityRendererDispatcher;
import net.minecraft.crash.CrashReport;
import net.minecraft.crash.CrashReportCategory;
import net.minecraft.init.Blocks;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.ReportedException;
import net.minecraft.world.ChunkCache;
import net.minecraft.world.IBlockAccess;

import java.util.concurrent.CompletableFuture;
import java.util.concurrent.CompletionException;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.TimeoutException;

/**
 * @implNote No longer fires: {@link net.minecraftforge.client.event.RenderWorldEvent RenderWorldEvent}, may lead to compat issues.
 */
public abstract class SimpleChunkBuilderMeshingTask extends ChunkBuilderTask<ChunkBuildOutput> {
    protected final RenderSection render;
    protected final int buildTime;
    protected final Vector3d camera;
    protected final WorldRenderRegion region;
    protected final ChunkCache chunkCache;

    public SimpleChunkBuilderMeshingTask(RenderSection render, WorldRenderRegion region, int time, Vector3d camera) {
        this.render = render;
        this.buildTime = time;
        this.camera = camera;
        this.region = region;
        this.chunkCache = createChunkCache(region);
    }

    /**
     * Should not access instance fields
     */
    protected abstract ChunkCache createChunkCache(WorldRenderRegion region);

    protected abstract Tessellator getTessellator();

    protected abstract void setRenderPass(int pass);

    /**
     * Sets the render pass within: {@link net.minecraftforge.client.ForgeHooksClient ForgeHooksClient#worldRenderPass}
     *
     * @param pass current render pass
     */
    protected static void setForgeRenderPass(int pass) {
        ForgeHooksClientMixin.setWorldRenderPass(pass);
    }

    /**
     * Increments the beddium chunk update counter
     */
    public static void incrementChunkUpdateCounter() {
        TerrainRenderingModule.incrementChunkUpdateCounter();
    }

    @Override
    public ChunkBuildOutput execute(ChunkBuildContext context, CancellationToken cancellationToken) {
        ArchaicChunkBuildContext buildContext = (ArchaicChunkBuildContext) context;
        MinecraftBuiltRenderSectionData<TextureAtlasSprite, TileEntity> renderData = new MinecraftBuiltRenderSectionData<>();

        // I WILL GIVE YOU [3 Easy Payments of $9.99!]
        boolean megaChunks = TerrainRenderingConfig.MEGAChunks != 0;
        Object occluder = megaChunks ? new MEGASectionVisibilityBuilder() : new SectionVisibilityBuilder();

        ChunkBuildBuffers buffers = buildContext.buffers;
        buffers.init(renderData, this.render.getSectionIndex());

        int minX = region.minX;
        int minY = region.minY;
        int minZ = region.minZ;

        int maxX = region.maxX;
        int maxY = region.maxY;
        int maxZ = region.maxZ;

        // Initialise with minX/minY/minZ so initial getBlockState crash context is correct
        int blockX = minX;
        int blockY = minY;
        int blockZ = minZ;

        var chunkCache = this.chunkCache;
        if (chunkCache instanceof StateAwareCache state) {
            state.renderStart();
        }
        BiomeColorCacheModule.update(chunkCache, this.render.getChunkX(), this.render.getChunkY(), this.render.getChunkZ());

        var renderBlocks = new RenderBlocks(chunkCache);
        var tessellator = getTessellator();

        tessellator.setTranslation(-minX, -minY, -minZ);

        BiomeColorCacheModule.toggleCacheActive(true);

        try {
            boolean threaded = isThreaded();
            IntList deferredWork = null;
            boolean hasDeferredWork = false;
            for (int y = minY; y < maxY; y++) {
                if (cancellationToken.isCancelled()) {
                    return null;
                }

                for (int z = minZ; z < maxZ; z++) {
                    for (int x = minX; x < maxX; x++) {
                        blockX = x;
                        blockY = y;
                        blockZ = z;

                        var block = chunkCache.getBlock(x, y, z);

                        if (block.getMaterial() == Material.air) {
                            continue;
                        }

                        if (block.hasTileEntity(chunkCache.getBlockMetadata(x, y, z))) {
                            TileEntity tileEntity = chunkCache.getTileEntity(x, y, z);
                            if (TileEntityRendererDispatcher.instance.hasSpecialRenderer(tileEntity)) {
                                renderData.globalBlockEntities.add(tileEntity);
                            }
                        }

                        if (!threaded || canRenderOffThread(0, block, x, y, z)) {
                            tryRenderBlock(tessellator, renderBlocks, 0, block, x, y, z);
                        } else {
                            if (deferredWork == null) {
                                deferredWork = new IntArrayList(384);
                            }
                            deferredWork.add(x);
                            deferredWork.add(y);
                            deferredWork.add(z);
                            hasDeferredWork = true;
                        }

                        if (block.isOpaqueCube()) {
                            if (megaChunks) {
                                ((MEGASectionVisibilityBuilder) occluder).markOpaque(blockX, blockY, blockZ);
                            } else {
                                ((SectionVisibilityBuilder) occluder).markOpaque(blockX, blockY, blockZ);
                            }
                        }
                    }
                }
            }
            if (threaded && hasDeferredWork) {
                runWorkOnMainThread(0, deferredWork, tessellator, renderBlocks, cancellationToken);
                if (cancellationToken.isCancelled()) {
                    return null;
                }
                hasDeferredWork = false;
                deferredWork.clear();
            }

            if (tessellator.isDrawing) {
                buildContext.copyRawBuffer(tessellator.rawBuffer, tessellator.vertexCount, buffers, buffers.getRenderPassConfiguration().getMaterialForRenderType(0));
                tessellator.isDrawing = false;
                tessellator.reset();
                setRenderPass(-1);
            }
            for (int pass = 1; pass < ArchaicChunkBuildContext.NUM_PASSES; pass++) {
                for (int y = minY; y < maxY; y++) {
                    if (cancellationToken.isCancelled()) {
                        return null;
                    }

                    for (int z = minZ; z < maxZ; z++) {
                        for (int x = minX; x < maxX; x++) {
                            blockX = x;
                            blockY = y;
                            blockZ = z;

                            var block = chunkCache.getBlock(x, y, z);

                            if (block.getMaterial() == Material.air) {
                                continue;
                            }

                            if (!threaded || canRenderOffThread(pass, block, x, y, z)) {
                                tryRenderBlock(tessellator, renderBlocks, pass, block, x, y, z);
                            } else {
                                if (deferredWork == null) {
                                    deferredWork = new IntArrayList(384);
                                }
                                deferredWork.add(x);
                                deferredWork.add(y);
                                deferredWork.add(z);
                                hasDeferredWork = true;
                            }
                        }
                    }
                }
                if (threaded && hasDeferredWork) {
                    runWorkOnMainThread(pass, deferredWork, tessellator, renderBlocks, cancellationToken);
                    if (cancellationToken.isCancelled()) {
                        return null;
                    }
                    hasDeferredWork = false;
                    deferredWork.clear();
                }
                if (tessellator.isDrawing) {
                    buildContext.copyRawBuffer(tessellator.rawBuffer, tessellator.vertexCount, buffers, buffers.getRenderPassConfiguration().getMaterialForRenderType(pass));
                    tessellator.isDrawing = false;
                    tessellator.reset();
                    setRenderPass(-1);
                }
            }
        } catch (ReportedException ex) {
            // Propagate existing crashes (add context)
            throw fillCrashInfo(ex.getCrashReport(), chunkCache, blockX, blockY, blockZ);
        } catch (Throwable ex) {
            // Create a new crash report for other exceptions (e.g. thrown in getQuads)
            throw fillCrashInfo(CrashReport.makeCrashReport(ex, "Encountered exception while building chunk meshes"), chunkCache, blockX, blockY, blockZ);
        } finally {
            BiomeColorCacheModule.toggleCacheActive(false);
            tessellator.setTranslation(0, 0, 0);
            if (chunkCache instanceof StateAwareCache state) {
                state.renderFinish();
            }
            if (tessellator.isDrawing) {
                tessellator.isDrawing = false;
                tessellator.reset();
                setRenderPass(-1);
            }
        }


        Reference2ReferenceMap<TerrainRenderPass, BuiltSectionMeshParts> meshes =
                BuiltSectionMeshParts.groupFromBuildBuffers(buffers, (float) camera.x - minX, (float) camera.y - minY, (float) camera.z - minZ);

        if (!meshes.isEmpty()) {
            renderData.hasBlockGeometry = true;
        }

        if (megaChunks) {
            renderData.visibilityData = ((MEGASectionVisibilityBuilder) occluder).computeVisibilityEncoding();
        } else {
            renderData.visibilityData = ((SectionVisibilityBuilder) occluder).computeVisibilityEncoding();
        }

        incrementChunkUpdateCounter();
        return new ChunkBuildOutput(this.render, renderData, meshes, this.buildTime);
    }

    protected MainThreadWork createMainThreadWork(int pass, IntList coords, Tessellator tessellator, RenderBlocks renderBlocks, CancellationToken cancellationToken) {
        return new MainThreadWork(pass, coords, cancellationToken, tessellator, renderBlocks);
    }

    protected void runWorkOnMainThread(int pass, IntList coords, Tessellator tessellator, RenderBlocks renderBlocks, CancellationToken cancellationToken) {
        val work = createMainThreadWork(pass, coords, tessellator, renderBlocks, cancellationToken);
        val manager = CeleritasWorldRenderer.instance().getRenderSectionManager();
        val task = CompletableFuture.runAsync(work, manager::scheduleAsyncTask);
        while (!cancellationToken.isCancelled()) {
            try {
                task.get(2, TimeUnit.SECONDS);
                break;
            } catch (InterruptedException | TimeoutException ignored) {
            } catch (ExecutionException e) {
                throw new CompletionException(e);
            }
        }
    }

    protected boolean isThreaded() {
        return false;
    }

    protected boolean canRenderOffThread(int pass, Block block, int x, int y, int z) {
        return false;
    }

    protected void tryRenderBlock(Tessellator tessellator, RenderBlocks renderBlocks, int pass, Block block, int x, int y, int z) {
        try {
            val fluidBlock = getFluidBlock(x, y, z);
            val canFluidBlockRender = fluidBlock != null && fluidBlock.canRenderInPass(pass);
            val canBlockRender = block.canRenderInPass(pass);
            if (!canFluidBlockRender && !canBlockRender) {
                return;
            }

            if (!tessellator.isDrawing) {
                setRenderPass(pass);
                tessellator.startDrawingQuads();
            }

            if (canFluidBlockRender) {
                renderBlocks.renderBlockByRenderType(fluidBlock, x, y, z);
            }

            if (canBlockRender) {
                renderBlocks.renderBlockByRenderType(block, x, y, z);
            }
        } catch (Exception e) {
            Share.log.error("Failed to render block at ({} {} {})", x, y, z);
            Share.log.error("Stacktrace:", e);
        }
    }

    protected ReportedException fillCrashInfo(CrashReport report, IBlockAccess slice, int x, int y, int z) {
        CrashReportCategory crashReportSection = report.makeCategory("Block being rendered");

        Block state = Blocks.air;
        int meta = 0;
        try {
            state = slice.getBlock(x, y, z);
            meta = slice.getBlockMetadata(x, y, z);
        } catch (Exception ignored) {
        }
        CrashReportCategory.func_147153_a(crashReportSection, x, y, z, state, meta);

        crashReportSection.addCrashSection("Chunk section", this.render);
        /*
        if (this.renderContext != null) {
            crashReportSection.addCrashSection("Render context volume", this.renderContext.getVolume());
        }

         */

        return new ReportedException(report);
    }

    protected @Nullable Block getFluidBlock(int x, int y, int z) {
        if (Compat.fluidloggedInstalled()) {
            return FluidLoggedCompat.getFluidBlock(chunkCache, x, y, z);
        } else {
            return null;
        }
    }

    private static class FluidLoggedCompat {
        public static @Nullable Block getFluidBlock(ChunkCache chunkCache, int x, int y, int z) {
            @SuppressWarnings("UnstableApiUsage") val fluid = FLBlockAccess.of(chunkCache).fl$getFluid(x, y, z);

            if (fluid == null) {
                return null;
            } else {
                return fluid.getBlock();
            }
        }
    }

    @RequiredArgsConstructor
    protected class MainThreadWork implements Runnable {
        protected final int pass;
        protected final IntList coords;
        protected final CancellationToken cancellationToken;
        protected final Tessellator tessellator;
        protected final RenderBlocks renderBlocks;

        @Override
        public void run() {
            int len = coords.size();
            for (int i = 0; i < len; i += 3) {
                if (cancellationToken.isCancelled()) {
                    return;
                }
                int x = coords.getInt(i);
                int y = coords.getInt(i + 1);
                int z = coords.getInt(i + 2);

                var block = chunkCache.getBlock(x, y, z);
                SimpleChunkBuilderMeshingTask.this.tryRenderBlock(tessellator, renderBlocks, pass, block, x, y, z);
            }
        }
    }
}
