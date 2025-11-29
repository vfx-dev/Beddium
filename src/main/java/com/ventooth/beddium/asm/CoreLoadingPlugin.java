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
import com.ventooth.beddium.config.Configs;
import com.ventooth.beddium.config.TerrainRenderingConfig;
import lombok.NoArgsConstructor;
import lombok.val;
import org.spongepowered.asm.launch.GlobalProperties;
import org.spongepowered.asm.service.mojang.MixinServiceLaunchWrapper;

import net.minecraft.launchwrapper.Launch;
import cpw.mods.fml.relauncher.IFMLLoadingPlugin;

import javax.swing.JOptionPane;
import java.util.List;
import java.util.Map;

@IFMLLoadingPlugin.Name(ShareAsm.ASM_NAME)
@IFMLLoadingPlugin.MCVersion("1.7.10")
@IFMLLoadingPlugin.SortingIndex(200_000)
@IFMLLoadingPlugin.TransformerExclusions(ShareAsm.ASM_PKG)
@NoArgsConstructor
@SuppressWarnings("unused")
public final class CoreLoadingPlugin implements IFMLLoadingPlugin {
    static {
        Configs.poke();
        Launch.blackboard.put(ShareAsm.TRACKED_FOG_STATE_ASM_EXCLUSIONS_KEY, TerrainRenderingConfig.FastFogAsmExclusions);

        try {
            ConfigCompat.executeConfigFixes();
        } catch (Throwable ignored) {
        }
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
        }

        val mixinTweakClasses = GlobalProperties.<List<String>>get(MixinServiceLaunchWrapper.BLACKBOARD_KEY_TWEAKCLASSES);
        if (mixinTweakClasses != null) {
            if (!mixinTweakClasses.contains(ShareAsm.TWEAKER)) {
                mixinTweakClasses.add(ShareAsm.TWEAKER);
                ShareAsm.log.debug("Registered PostMixinTweaker");
            }
        } else {
            ShareAsm.log.error("Failed to register PostMixinTweaker, things might not work as planned!");
        }
        return new String[0];
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
