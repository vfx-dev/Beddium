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

import com.falsepattern.lib.mixin.ITargetedMod;
import com.ventooth.beddium.config.ModuleConfig;
import com.ventooth.beddium.config.TerrainRenderingConfig;
import lombok.AllArgsConstructor;

import java.util.List;
import java.util.function.Predicate;
import java.util.function.Supplier;

@AllArgsConstructor
enum Cfg implements Predicate<List<ITargetedMod>> {
    TerrainRendering(() -> ModuleConfig.TerrainRendering),
    BiomeColorCache(() -> ModuleConfig.BiomeColorCache),
    ConservativeAnimatedTextures(() -> ModuleConfig.ConservativeAnimatedTextures),
    BetterMipmaps(() -> ModuleConfig.BetterMipmaps),
    MEGAChunks(() -> ModuleConfig.TerrainRendering && TerrainRenderingConfig.MEGAChunks != 0),
    Lwjgl3(new Supplier<>() {
        private Boolean value = null;

        @Override
        public Boolean get() {
            Boolean v = value;
            if (v == null) {
                try {
                    Class.forName("org.lwjgl.system.MemoryUtil", false, Cfg.class.getClassLoader());
                    v = true;
                } catch (Throwable ignored) {
                    v = false;
                }
                value = v;
            }
            return v;
        }
    })
    ;

    private final Supplier<Boolean> enabled;

    @Override
    public boolean test(List<ITargetedMod> iTargetedMods) {
        return enabled.get();
    }
}
