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

package com.ventooth.beddium.asm;

import com.falsepattern.lib.turboasm.MergeableTurboTransformer;
import com.falsepattern.lib.turboasm.TurboClassTransformer;
import com.ventooth.beddium.config.TerrainRenderingConfig;
import lombok.val;

import java.util.ArrayList;
import java.util.List;

@SuppressWarnings("UnstableApiUsage")
public final class PostMixinTransformers extends MergeableTurboTransformer {
    public PostMixinTransformers() {
        super(transformers());
    }

    private static List<TurboClassTransformer> transformers() {
        val transformers = new ArrayList<TurboClassTransformer>();
        if (TerrainRenderingConfig.FastFogAsm) {
            val transformer = new FogStateAsmHookInjector();
            transformers.add(transformer);
            ShareAsm.log.debug("Registered PostMixinTransformer: {}", transformer.name());
        }
        return transformers;
    }
}