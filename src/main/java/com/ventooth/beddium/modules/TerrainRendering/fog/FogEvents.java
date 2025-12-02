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

package com.ventooth.beddium.modules.TerrainRendering.fog;

import com.ventooth.beddium.config.TerrainRenderingConfig;
import lombok.NoArgsConstructor;

import net.minecraftforge.client.event.EntityViewRenderEvent;
import cpw.mods.fml.common.eventhandler.EventPriority;
import cpw.mods.fml.common.eventhandler.SubscribeEvent;

/**
 * Captures parts of the fog state from forge events for slightly improved tracking
 */
@NoArgsConstructor
public final class FogEvents {
    @SubscribeEvent(priority = EventPriority.LOWEST)
    public void onFogColor(EntityViewRenderEvent.FogColors event) {
        if (TerrainRenderingConfig.FastFog) {
            FogState.red = event.red;
            FogState.green = event.red;
            FogState.blue = event.blue;
        }
    }

    @SubscribeEvent(priority = EventPriority.LOWEST,
                    receiveCanceled = true)
    public void onFogDensity(EntityViewRenderEvent.FogDensity event) {
        if (TerrainRenderingConfig.FastFog) {
            FogState.density = event.density;
        }
    }
}
