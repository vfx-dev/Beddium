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

package com.ventooth.beddium.modules.TerrainRendering;

import com.ventooth.beddium.Compat;
import com.ventooth.beddium.api.task.ChunkTaskRegistry;
import com.ventooth.beddium.modules.ConservativeAnimatedTextures.ConservativeAnimatedTexturesModule;
import com.ventooth.beddium.modules.MEGAChunks.MegaChunkMetadata;
import com.ventooth.beddium.modules.TerrainRendering.compile.ArchaicChunkBuildContext;
import com.ventooth.beddium.modules.TerrainRendering.render.CompatibleChunkRenderer;
import lombok.val;
import org.embeddedt.embeddium.impl.gl.device.CommandList;
import org.embeddedt.embeddium.impl.render.chunk.RenderPassConfiguration;
import org.embeddedt.embeddium.impl.render.chunk.RenderSection;
import org.embeddedt.embeddium.impl.render.chunk.RenderSectionManager;
import org.embeddedt.embeddium.impl.render.chunk.compile.ChunkBuildOutput;
import org.embeddedt.embeddium.impl.render.chunk.compile.tasks.ChunkBuilderTask;
import org.embeddedt.embeddium.impl.render.chunk.lists.SectionTicker;
import org.embeddedt.embeddium.impl.render.chunk.lists.SortedRenderLists;
import org.embeddedt.embeddium.impl.render.chunk.occlusion.AsyncOcclusionMode;
import org.embeddedt.embeddium.impl.render.chunk.sprite.GenericSectionSpriteTicker;
import org.embeddedt.embeddium.impl.render.viewport.Viewport;
import org.jetbrains.annotations.Nullable;

import net.minecraft.client.multiplayer.WorldClient;
import net.minecraft.world.chunk.Chunk;


public class ArchaicRenderSectionManager extends RenderSectionManager {
    private static final boolean USE_ASYNC_OCCLUSION = true;          // TODO: Wire up config
    private static final boolean USE_FOG_OCCLUSION = true;            // TODO: Wire up config

    private final WorldClient world;
    private final boolean alwaysDeferChunkUpdates;

    public ArchaicRenderSectionManager(RenderPassConfiguration<?> configuration, WorldClient world, int renderDistance, CommandList commandList, int minSection, int maxSection, int requestedThreads, boolean alwaysDeferChunkUpdates) {
        super(configuration, () -> new ArchaicChunkBuildContext(world, configuration), CompatibleChunkRenderer::get, renderDistance, commandList, minSection, maxSection,
              requestedThreads, Compat.shadowPassExists());
        this.world = world;
        this.alwaysDeferChunkUpdates = alwaysDeferChunkUpdates;
    }

    public static ArchaicRenderSectionManager create(WorldClient world, int renderDistance, CommandList commandList) {
        val provider = ChunkTaskRegistry.getProvider();
        return new ArchaicRenderSectionManager(ArchaicRenderPassConfigurationBuilder.build(), world, renderDistance, commandList, 0, 16 / MegaChunkMetadata.EBS_PER_WR_EDGE,
                                               provider.threadCount(), provider.alwaysDeferChunkUpdates());
    }

    @Override
    protected AsyncOcclusionMode getAsyncOcclusionMode() {
        if (USE_ASYNC_OCCLUSION) {
            if (Compat.isSwansongInitialized() && Compat.shadowPassExists()) {
                return AsyncOcclusionMode.ONLY_SHADOW;
            } else {
                return AsyncOcclusionMode.EVERYTHING;
            }
        } else {
            return AsyncOcclusionMode.NONE;
        }
    }

    @Override
    protected boolean shouldRespectUpdateTaskQueueSizeLimit() {
        return true;
    }

    @Override
    protected boolean useFogOcclusion() {
        return USE_FOG_OCCLUSION;
    }

    @Override
    protected boolean shouldUseOcclusionCulling(Viewport positionedViewport, boolean spectator) {
        if (Compat.isSwansongInitialized() && Compat.shadowPassActive()) {
            return false;
        }

        final boolean useOcclusionCulling;
        var camBlockPos = positionedViewport.getBlockCoord();

        useOcclusionCulling = !spectator || !this.world.getBlock(camBlockPos.x(), camBlockPos.y(), camBlockPos.z()).isOpaqueCube();

        return useOcclusionCulling;
    }

    @Override
    protected boolean isSectionVisuallyEmpty(int x, int y, int z) {
        boolean hasBlocks = false;
        x *= MegaChunkMetadata.EBS_PER_WR_EDGE;
        y *= MegaChunkMetadata.EBS_PER_WR_EDGE;
        z *= MegaChunkMetadata.EBS_PER_WR_EDGE;
        for (int cX = 0; cX < MegaChunkMetadata.EBS_PER_WR_EDGE; cX++) {
            val X = x + cX;
            for (int cZ = 0; cZ < MegaChunkMetadata.EBS_PER_WR_EDGE; cZ++) {
                val Z = z + cZ;
                Chunk chunk = this.world.getChunkFromChunkCoords(X, Z);
                if (chunk.isEmpty()) {
                    return true;
                }
                var array = chunk.getBlockStorageArray();
                for (int cY = 0; cY < MegaChunkMetadata.EBS_PER_WR_EDGE; cY++) {
                    val Y = y + cY;
                    if (Y >= 0 && Y < array.length) {
                        if (array[Y] != null && !array[Y].isEmpty()) {
                            hasBlocks = true;
                        }
                    }
                }

            }
        }
        return !hasBlocks;
    }

    @Override
    protected @Nullable ChunkBuilderTask<ChunkBuildOutput> createRebuildTask(RenderSection render, int frame) {
        if (isSectionVisuallyEmpty(render.getChunkX(), render.getChunkY(), render.getChunkZ())) {
            return null;
        }

        return ChunkTaskRegistry.getProvider().createRebuildTask(render, frame, cameraPosition);
    }

    @Override
    protected boolean allowImportantRebuilds() {
        return alwaysDeferChunkUpdates;
    }

    @Override
    protected @Nullable SectionTicker createSectionTicker() {
        return new GenericSectionSpriteTicker<>(ConservativeAnimatedTexturesModule::markSpriteActive);
    }

    // TODO: `this.renderListManager` has protected access in future release
    private boolean bypassShadowPass = false;

    public SortedRenderLists getNonShadowRenderLists() {
        bypassShadowPass = true;
        val renderLists = this.getRenderLists();
        bypassShadowPass = false;
        return renderLists;
    }

    @Override
    public boolean isInShadowPass() {
        if (bypassShadowPass) {
            return false;
        } else {
            return Compat.isSwansongInitialized() && Compat.shadowPassActive();
        }
    }
}
