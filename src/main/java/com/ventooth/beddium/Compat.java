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

package com.ventooth.beddium;

import com.ventooth.swansong.api.ShaderStateInfo;
import lombok.AccessLevel;
import lombok.NoArgsConstructor;

import net.minecraft.launchwrapper.Launch;

import java.io.IOException;

@NoArgsConstructor(access = AccessLevel.PRIVATE)
public final class Compat {
    public static boolean rpleInstalled() {
        return RPLE.PRESENT;
    }

    public static boolean fluidloggedInstalled() {
        return FLUIDLOGGED.PRESENT;
    }

    public static boolean isSwansongInstalled() {
        return SWANSONG.PRESENT;
    }

    public static boolean shadowPassActive() {
        return SWANSONG.PRESENT && SWANSONG.shadowPassActive();
    }

    public static boolean shadowPassExists() {
        return SWANSONG.PRESENT && SWANSONG.shadowPassExists();
    }

    // region Internal
    private static class RPLE {
        private static final boolean PRESENT;

        static {
            boolean present;
            try {
                present = Launch.classLoader.getClassBytes("com.falsepattern.rple.internal.asm.ASMLoadingPlugin") != null;
            } catch (IOException e) {
                e.printStackTrace();
                present = false;
            }
            PRESENT = present;
        }
    }

    private static class FLUIDLOGGED {
        private static final boolean PRESENT;

        static {
            boolean present;
            try {
                present = Launch.classLoader.getClassBytes("mega.fluidlogged.internal.core.CoreLoadingPlugin") != null;
            } catch (IOException e) {
                e.printStackTrace();
                present = false;
            }
            PRESENT = present;
        }
    }

    private static class SWANSONG {
        private static final boolean PRESENT;

        static {
            boolean present;
            try {
                present = Launch.classLoader.getClassBytes("com.ventooth.swansong.Share") != null;
            } catch (IOException e) {
                e.printStackTrace();
                present = false;
            }
            PRESENT = present;
        }

        private static boolean shadowPassExists() {
            return ShaderStateInfo.shadowPassExists();
        }

        private static boolean shadowPassActive() {
            return ShaderStateInfo.shadowPassActive();
        }
    }
    // endregion
}
