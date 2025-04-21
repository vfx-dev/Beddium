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

package com.ventooth.beddium.config;

import com.falsepattern.lib.config.Config;
import com.ventooth.beddium.Tags;
import lombok.AccessLevel;
import lombok.NoArgsConstructor;

@Config.Comment("Toggles for all Beddium modules")
@Config(modid = Tags.MOD_ID,
        category = "00_modules")
@Config.LangKey
@NoArgsConstructor(access = AccessLevel.PRIVATE)
public final class ModuleConfig {
    static {
        Configs.poke();
    }

    @Config.Name("TerrainRendering")
    @Config.Comment({"Celeritas based terrain rendering"})
    @Config.LangKey("config.beddium.modules.TerrainRendering")
    @Config.RequiresMcRestart
    @Config.DefaultBoolean(true)
    public static boolean TerrainRendering;

    @Config.Name("BiomeColorCache")
    @Config.Comment({"Caching of biome color selection when baking chunks"})
    @Config.LangKey("config.beddium.modules.BiomeColorCache")
    @Config.RequiresMcRestart
    @Config.DefaultBoolean(true)
    public static boolean BiomeColorCache;

    @Config.Name("ConservativeAnimatedTextures")
    @Config.Comment({"Only update visible animated textures"})
    @Config.LangKey("config.beddium.modules.ConservativeAnimatedTextures")
    @Config.RequiresMcRestart
    @Config.DefaultBoolean(false) // TODO: Ship enabled
    public static boolean ConservativeAnimatedTextures;

    @Config.Name("BetterMipmaps")
    @Config.Comment({"Better looking mipmaps"})
    @Config.LangKey("config.beddium.modules.BetterMipmaps")
    @Config.RequiresMcRestart
    @Config.DefaultBoolean(false) // TODO: Ship enabled
    public static boolean BetterMipmaps;

    @Config.Name("Debug")
    @Config.Comment({"Helpful debug features"})
    @Config.LangKey("config.beddium.modules.Debug")
    @Config.RequiresMcRestart
    @Config.DefaultBoolean(true) //TODO: Ship disabled
    public static boolean Debug;
}
