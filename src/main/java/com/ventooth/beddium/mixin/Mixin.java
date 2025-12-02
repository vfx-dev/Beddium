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

package com.ventooth.beddium.mixin;

import com.falsepattern.lib.mixin.v2.MixinHelper;
import com.falsepattern.lib.mixin.v2.SidedMixins;
import com.falsepattern.lib.mixin.v2.TaggedMod;
import com.gtnewhorizon.gtnhmixins.builders.IMixins;
import com.gtnewhorizon.gtnhmixins.builders.MixinBuilder;
import com.ventooth.beddium.Tags;
import com.ventooth.beddium.config.ModuleConfig;
import com.ventooth.beddium.config.TerrainRenderingConfig;
import lombok.Getter;
import lombok.RequiredArgsConstructor;
import org.intellij.lang.annotations.Language;

import java.util.function.BooleanSupplier;

import static com.falsepattern.lib.mixin.v2.MixinHelper.builder;
import static com.falsepattern.lib.mixin.v2.MixinHelper.require;

@SuppressWarnings("UnstableApiUsage")
@RequiredArgsConstructor
public enum Mixin implements IMixins {
    // @formatter:off

    // region Terrain Rendering
    TerrainRendering(Phase.EARLY,
                      () -> ModuleConfig.TerrainRendering,
                      client("TerrainRendering.ClientChunkManagerMixin",
                             "TerrainRendering.WorldClientMixin",
                             "TerrainRendering.RenderGlobalMixin",
                             "TerrainRendering.TextureAtlasMixin",
                             "TerrainRendering.TextureAtlasSpriteMixin",
                             "TerrainRendering.ForgeHooksClientMixin",
                             "TerrainRendering.MinecraftMixin",
                             "TerrainRendering.EntityRendererMixin",
                             "TerrainRendering.FrustrumMixin")),
    TerrainRendering_SwanSong(Phase.EARLY,
                              () -> ModuleConfig.TerrainRendering,
                              require(TargetMod.Swansong),
                              client("TerrainRendering.ShaderModBridgeMixin",
                                     "TerrainRendering.ShaderEngineMixin")),
    TerrainRendering_LockTessellator(Phase.EARLY,
                                     () -> ModuleConfig.TerrainRendering,
                                     client("TerrainRendering.compat.LockableTessellatorMixin")),

    TerrainRendering_Netherlicious(Phase.LATE,
                                   () -> ModuleConfig.TerrainRendering,
                                   require(TargetMod.Netherlicious),
                                   client("TerrainRendering.compat.netherlicious.RootsRenderMixin",
                                          "TerrainRendering.compat.netherlicious.SporeBlossomRenderMixin")),
    TerrainRendering_iChunDoors(Phase.LATE,
                                () -> ModuleConfig.TerrainRendering,
                                require(TargetMod.iChunDoors),
                                client("TerrainRendering.compat.ichundoors.FrustrumMixin")),
    TerrainRendering_FlenixCities(Phase.LATE,
                                  () -> ModuleConfig.TerrainRendering,
                                  require(TargetMod.FlenixCities),
                                  client("TerrainRendering.compat.flenixcities.ISBRHMixin")),
    // endregion

    // region Biome Color Cache
    BiomeColorCache(Phase.EARLY,
                     () -> ModuleConfig.BiomeColorCache,
                     client("BiomeColorCache.WorldClientMixin",
                            "BiomeColorCache.BlockGrassMixin",
                            "BiomeColorCache.BlockLeavesMixin",
                            "BiomeColorCache.BlockLiquidMixin")),
    // endregion

    // region Conservative Animated Textures
    ConservativeAnimatedTextures(Phase.EARLY,
                                 () -> ModuleConfig.BiomeColorCache,
                                 client("ConservativeAnimatedTextures.TextureAtlasMixin",
                                        "ConservativeAnimatedTextures.TextureAtlasSpriteMixin")),
    // endregion

    // region MEGA Chunks
    MEGAChunks(Phase.EARLY,
                     () -> ModuleConfig.TerrainRendering && TerrainRenderingConfig.MEGAChunks != 0,
                     client("MEGAChunks.AbstractSectionMixin",
                            "MEGAChunks.BiomeColorCacheMixin",
                            "MEGAChunks.DefaultChunkRendererMixin",
                            "MEGAChunks.EntityRendererMixin",
                            "MEGAChunks.OcclusionCullerMixin",
                            "MEGAChunks.PositionUtilMixin",
                            "MEGAChunks.RenderRegionMixin",
                            "MEGAChunks.RenderSectionManagerMixin",
                            "MEGAChunks.SectionPosMixin",
                            "MEGAChunks.SimpleWorldRendererMixin")),
    // endregion

    // @formatter:on

    //region Boilerplate
    ;
    @Getter
    private final MixinBuilder builder;

    Mixin(Phase phase, SidedMixins... mixins) {
        this(builder(mixins).setPhase(phase));
    }

    Mixin(Phase phase, BooleanSupplier cond, SidedMixins... mixins) {
        this(builder(cond, mixins).setPhase(phase));
    }

    Mixin(Phase phase, TaggedMod mod, SidedMixins... mixins) {
        this(builder(mod, mixins).setPhase(phase));
    }

    Mixin(Phase phase, TaggedMod[] mods, SidedMixins... mixins) {
        this(builder(mods, mixins).setPhase(phase));
    }

    Mixin(Phase phase, BooleanSupplier cond, TaggedMod mod, SidedMixins... mixins) {
        this(builder(cond, mod, mixins).setPhase(phase));
    }

    Mixin(Phase phase, BooleanSupplier cond, TaggedMod[] mods, SidedMixins... mixins) {
        this(builder(cond, mods, mixins).setPhase(phase));
    }

    private static SidedMixins common(@Language(value = "JAVA",
                                                prefix = "import " + Tags.ROOT_PKG + ".mixin.mixins.common.",
                                                suffix = ";") String... mixins) {
        return MixinHelper.common(mixins);
    }

    private static SidedMixins client(@Language(value = "JAVA",
                                                prefix = "import " + Tags.ROOT_PKG + ".mixin.mixins.client.",
                                                suffix = ";") String... mixins) {
        return MixinHelper.client(mixins);
    }

    private static SidedMixins server(@Language(value = "JAVA",
                                                prefix = "import " + Tags.ROOT_PKG + ".mixin.mixins.server.",
                                                suffix = ";") String... mixins) {
        return MixinHelper.server(mixins);
    }
    //endregion
}
