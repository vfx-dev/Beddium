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
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.intellij.lang.annotations.Language;

public final class ShareAsm {
    public static final String ASM_NAME = Tags.MOD_NAME + "|ASM";
    public static final String ASM_PKG = Tags.ROOT_PKG + ".asm";
    public static final Logger log = LogManager.getLogger(ASM_NAME);

    @Language(value = "JAVA",
              prefix = "import ",
              suffix = ";")
    public static final String TWEAKER = ASM_PKG + ".PostMixinTweaker";
    @Language(value = "JAVA",
              prefix = "import ",
              suffix = ";")
    public static final String TRANSFORMER = ASM_PKG + ".PostMixinTransformers";

    public static final String TRACKED_FOG_STATE_ASM_EXCLUSIONS_KEY = Tags.MOD_ID + ".TrackedFogStateAsmExclusions";

    private ShareAsm() {
        throw new UnsupportedOperationException();
    }
}
