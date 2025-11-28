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

import com.ventooth.beddium.Tags;
import lombok.NoArgsConstructor;
import org.intellij.lang.annotations.Language;

import cpw.mods.fml.relauncher.IFMLLoadingPlugin;

import java.util.Map;

@IFMLLoadingPlugin.Name(Tags.MOD_NAME + "|ASM Plugin")
@IFMLLoadingPlugin.MCVersion("1.7.10")
@IFMLLoadingPlugin.SortingIndex(200_000)
@IFMLLoadingPlugin.TransformerExclusions(Tags.ROOT_PKG + ".asm")
@NoArgsConstructor
public final class CoreLoadingPlugin implements IFMLLoadingPlugin {
    @Language(value = "JAVA",
              prefix = "import ",
              suffix = ";")
    private static final String TRANSFORMER = Tags.ROOT_PKG + ".asm.BeddiumTransformer";

    static {
        try {
            ConfigCompat.executeConfigFixes();
        } catch (Throwable ignored) {}
    }

    @Override
    public String[] getASMTransformerClass() {
        return new String[]{TRANSFORMER};
    }

    // region Unused
    @Override
    public String getModContainerClass() {
        return null;
    }

    @Override
    public String getSetupClass() {
        return null;
    }

    @Override
    public void injectData(Map<String, Object> data) {
    }

    @Override
    public String getAccessTransformerClass() {
        return null;
    }
    // endregion
}
