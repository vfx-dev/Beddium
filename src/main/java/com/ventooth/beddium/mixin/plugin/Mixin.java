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

package com.ventooth.beddium.mixin.plugin;

import com.falsepattern.lib.mixin.IMixin;
import com.falsepattern.lib.mixin.ITargetedMod;
import com.ventooth.beddium.config.TerrainRenderingConfig;
import lombok.AllArgsConstructor;
import lombok.Getter;

import java.util.List;
import java.util.function.Predicate;

import static com.falsepattern.lib.mixin.IMixin.PredicateHelpers.always;
import static com.falsepattern.lib.mixin.IMixin.PredicateHelpers.condition;
import static com.falsepattern.lib.mixin.IMixin.PredicateHelpers.require;

@AllArgsConstructor
enum Mixin implements IMixin {
    // region Terrain Rendering
    TerrainRendering_ClientChunkManagerMixin(Side.CLIENT, Cfg.TerrainRendering),
    TerrainRendering_WorldClientMixin(Side.CLIENT, Cfg.TerrainRendering),
    TerrainRendering_RenderGlobalMixin(Side.CLIENT, Cfg.TerrainRendering),
    TerrainRendering_TextureAtlasMixin(Side.CLIENT, Cfg.TerrainRendering),
    TerrainRendering_TextureAtlasSpriteMixin(Side.CLIENT, Cfg.TerrainRendering),
    TerrainRendering_ForgeHooksClientMixin(Side.CLIENT, Cfg.TerrainRendering),
    TerrainRendering_MinecraftMixin(Side.CLIENT, Cfg.TerrainRendering),
    TerrainRendering_EntityRendererFastFogMixin(Side.CLIENT, Cfg.TerrainRendering.and(condition(() -> TerrainRenderingConfig.FastFog))),

    TerrainRendering_ShaderModBridgeMixin(Side.CLIENT, Cfg.TerrainRendering.and(require(TargetedMod.SWANSONG))),
    TerrainRendering_ShaderEngineMixin(Side.CLIENT, Cfg.TerrainRendering.and(require(TargetedMod.SWANSONG))),
    // endregion

    // region Biome Color Cache
    BiomeColorCache_WorldClientMixin(Side.CLIENT, Cfg.BiomeColorCache),
    BiomeColorCache_BlockGrassMixin(Side.CLIENT, Cfg.BiomeColorCache),
    BiomeColorCache_BlockLeavesMixin(Side.CLIENT, Cfg.BiomeColorCache),
    BiomeColorCache_BlockLiquidMixin(Side.CLIENT, Cfg.BiomeColorCache),
    // endregion

    // region Conservative Animated Textures
    ConservativeAnimatedTextures_TextureAtlasMixin(Side.CLIENT, Cfg.ConservativeAnimatedTextures),
    ConservativeAnimatedTextures_TextureAtlasSpriteMixin(Side.CLIENT, Cfg.ConservativeAnimatedTextures),
    // endregion

    // region Better Mipmaps
    BetterMipmaps_TextureAtlasSpriteMixin(Side.CLIENT, Cfg.BetterMipmaps),
    BetterMipmaps_TextureUtilMixin(Side.CLIENT, Cfg.BetterMipmaps),
    // endregion

    // region MEGA Chunks
    MEGAChunks_AbstractSectionMixin(Side.CLIENT, Cfg.MEGAChunks),
    MEGAChunks_BiomeColorCacheMixin(Side.CLIENT, Cfg.MEGAChunks),
    MEGAChunks_DefaultChunkRendererMixin(Side.CLIENT, Cfg.MEGAChunks),
    MEGAChunks_EntityRendererMixin(Side.CLIENT, Cfg.MEGAChunks),
    MEGAChunks_OcclusionCullerMixin(Side.CLIENT, Cfg.MEGAChunks),
    MEGAChunks_PositionUtilMixin(Side.CLIENT, Cfg.MEGAChunks),
    MEGAChunks_RenderRegionMixin(Side.CLIENT, Cfg.MEGAChunks),
    MEGAChunks_RenderSectionManagerMixin(Side.CLIENT, Cfg.MEGAChunks),
    MEGAChunks_SectionPosMixin(Side.CLIENT, Cfg.MEGAChunks),
    MEGAChunks_SimpleWorldRendererMixin(Side.CLIENT, Cfg.MEGAChunks),
    // endregion

    // region Lwjgl3 Compat
    Lwjgl3_celeritas_GlFenceMixin(Side.CLIENT, Cfg.Lwjgl3),
    Lwjgl3_celeritas_MultidrawFunctionsMixin(Side.CLIENT, Cfg.Lwjgl3),
    Lwjgl3_celeritas_ShaderWorkaroundsMixin(Side.CLIENT, Cfg.Lwjgl3),
    // endregion
    ;

    @Getter
    private final Side side;
    @Getter
    private final Predicate<List<ITargetedMod>> filter;
    @Getter
    private final String mixin;

    Mixin(Side side, Predicate<List<ITargetedMod>> filter) {
        this.side = side;
        this.filter = filter;
        this.mixin = name().replace('_', '.');
    }
}

