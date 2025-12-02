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

import com.gtnewhorizon.gtnhmixins.IEarlyMixinLoader;
import com.gtnewhorizon.gtnhmixins.builders.IMixins;
import com.ventooth.beddium.config.Configs;
import com.ventooth.beddium.config.TerrainRenderingConfig;
import com.ventooth.beddium.mixin.Mixin;
import lombok.NoArgsConstructor;
import lombok.val;
import org.spongepowered.asm.launch.GlobalProperties;
import org.spongepowered.asm.service.mojang.MixinServiceLaunchWrapper;

import net.minecraft.launchwrapper.Launch;
import cpw.mods.fml.relauncher.IFMLLoadingPlugin;

import java.util.List;
import java.util.Map;
import java.util.Set;

@IFMLLoadingPlugin.Name(ShareAsm.ASM_NAME)
@IFMLLoadingPlugin.MCVersion("1.7.10")
@IFMLLoadingPlugin.SortingIndex(200_000)
@IFMLLoadingPlugin.TransformerExclusions(ShareAsm.ASM_PKG)
@NoArgsConstructor
@SuppressWarnings("unused")
public final class CoreLoadingPlugin implements IFMLLoadingPlugin, IEarlyMixinLoader {
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
        val mixinTweakClasses = GlobalProperties.<List<String>>get(MixinServiceLaunchWrapper.BLACKBOARD_KEY_TWEAKCLASSES);
        if (mixinTweakClasses != null) {
            if (!mixinTweakClasses.contains(ShareAsm.TWEAKER)) {
                mixinTweakClasses.add(ShareAsm.TWEAKER);
                Launch.blackboard.put(ShareAsm.TRANSFORMER, new PostMixinTransformers());
                ShareAsm.log.debug("Registered PostMixinTweaker");
            }
        } else {
            ShareAsm.log.error("Failed to register PostMixinTweaker, things might not work as planned!");
        }
        return new String[0];
    }

    @Override
    public String getMixinConfig() {
        return "mixins.beddium.early.json";
    }

    @Override
    public List<String> getMixins(Set<String> loadedCoreMods) {
        return IMixins.getEarlyMixins(Mixin.class, loadedCoreMods);
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
