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

import com.ventooth.beddium.modules.TerrainRendering.CeleritasWorldRenderer;
import lombok.AccessLevel;
import lombok.NoArgsConstructor;
import org.embeddedt.embeddium.impl.common.util.MathUtil;
import org.embeddedt.embeddium.impl.common.util.NativeBuffer;

import net.minecraft.client.Minecraft;
import net.minecraft.util.EnumChatFormatting;
import net.minecraftforge.client.event.RenderGameOverlayEvent;
import net.minecraftforge.common.MinecraftForge;
import cpw.mods.fml.common.eventhandler.SubscribeEvent;

import java.lang.management.ManagementFactory;

@NoArgsConstructor(access = AccessLevel.PRIVATE)
public final class F3Display {
    public static void init() {
        MinecraftForge.EVENT_BUS.register(new F3Display());
    }

    @SubscribeEvent
    public void onF3Text(RenderGameOverlayEvent.Text event) {
        if (!Minecraft.getMinecraft().gameSettings.showDebugInfo) {
            return;
        }

        var renderer = CeleritasWorldRenderer.instanceNullable();

        var strings = event.right;
        strings.add("");
        strings.add(getBranding(renderer != null));

        if (renderer != null) {
            strings.addAll(renderer.getDebugStrings());
        }

        for (int i = 0; i < strings.size(); i++) {
            String str = strings.get(i);

            if (str != null && str.startsWith("Allocated memory:")) {
                strings.add(i + 1, getNativeMemoryString());

                break;
            }
        }
    }

    private static String getBranding(boolean isActive) {
        return "%s%s (%s)".formatted(isActive ? EnumChatFormatting.GREEN : EnumChatFormatting.RED, Tags.MOD_NAME, Tags.MOD_VERSION);
    }

    private static String getNativeMemoryString() {
        return "Off-Heap: +" + MathUtil.toMib(getNativeMemoryUsage()) + "MB";
    }

    private static long getNativeMemoryUsage() {
        return ManagementFactory.getMemoryMXBean().getNonHeapMemoryUsage().getUsed() + NativeBuffer.getTotalAllocated();
    }
}
