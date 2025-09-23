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

import com.ventooth.beddium.Share;
import com.ventooth.beddium.Tags;
import lombok.val;

import cpw.mods.fml.relauncher.IFMLLoadingPlugin;

import javax.swing.JOptionPane;
import java.util.Map;

@IFMLLoadingPlugin.TransformerExclusions(Tags.ROOT_PKG + ".asm")
public class CoreLoadingPlugin implements IFMLLoadingPlugin {
    static {
        try {
            ConfigCompat.executeConfigFixes();
        } catch (Throwable ignored) {}
    }

    @Override
    public String[] getASMTransformerClass() {
        boolean lwjgl3 = false;
        try {
            Class.forName("org.lwjgl.system.MemoryUtil", false, CoreLoadingPlugin.class.getClassLoader());
            lwjgl3 = true;
        } catch (Throwable ignored) {}
        if (lwjgl3) {
            val msg = "You're trying to use the java 8 version of beddium on modern java!\nPlease replace beddium version " + Tags.MOD_VERSION + " with " + Tags.MOD_VERSION.replace("j8", "j21") + "!";
            Share.log.fatal(msg);
            try {
                JOptionPane.showMessageDialog(null, msg, "Wrong Beddium Version!", JOptionPane.ERROR_MESSAGE);
            } catch (Throwable ignored) {}
            System.exit(1);
            throw new Error(msg);
            //TODO uncouple j21 builds and lwjgl3
            // return new String[]{Tags.ROOT_PKG + ".asm.Lwjgl3ifyTransformer"};
        } else {
            return new String[0];
        }
    }

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
}
