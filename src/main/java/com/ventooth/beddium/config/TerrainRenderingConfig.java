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

@Config.Comment("Celeritas based terrain rendering")
@Config(modid = Tags.MOD_ID,
        category = "01_terrain")
@Config.LangKey
@NoArgsConstructor(access = AccessLevel.PRIVATE)
public final class TerrainRenderingConfig {
    static {
        Configs.poke();
    }

    @Config.Name("MEGAChunks")
    @Config.Comment({
            "Larger chunk renderers. One minecraft 16x256x16 chunk is 16 subchunks",
            "0 - 1x1x1 - 1 subchunk per renderer (disabled)",
            "1 - 2x2x2 - 8 subchunks per renderer",
            "2 - 4x4x4 - 64 subchunks per renderer",
            "3 - 8x8x8 - 512 subchunks per renderer (implementation limit)"
    })
    @Config.LangKey("config.beddium.terrainrendering.MEGAChunks")
    @Config.RequiresMcRestart
    @Config.DefaultInt(0)
    @Config.RangeInt(min = 0, max = 3)
    public static int MEGAChunks;

    @Config.Name("ChunkDrawMode")
    @Config.Comment({
            "Changes the way chunks at the world edge are rendered.",
            "Vanilla: Renders \"chunk cliffs\" at the world edge. Slightly slower,",
            "         but fixes issues such as invisible chunks in minigame servers,",
            "         or reduced render distance.",
            "Fast: Only renders chunks if all of the neighboring chunks are loaded.",
            "      A bit faster than safe, but on some servers this might cause missing",
            "      chunks at map edges,",
            "      and it reduces visual render distance by 1 (the chunks are still present).",
    })
    @Config.LangKey("config.beddium.terrainrendering.ChunkDrawMode")
    @Config.RequiresWorldRestart
    @Config.DefaultEnum("Vanilla")
    public static DrawModeEnum ChunkDrawMode;

    @Config.Name("UseMultiDrawIndirect")
    @Config.Comment({
            "Controls if chunks should be drawn using MultiDrawIndirect.",
            "This option is a trade off between CPU and GPU overhead when rendering chunks.",
            "It is best left disabled in most cases, particularly on Intel iGPUs.",
            "Can improve performance on render distances beyond 16 if you are CPU limited.",
    })
    @Config.LangKey("config.beddium.modules.UseMultiDrawIndirect")
    @Config.RequiresWorldRestart
    @Config.DefaultBoolean(false)
    public static boolean UseMultiDrawIndirect;

    public enum DrawModeEnum {
        Vanilla,
        Fast
    }
}
