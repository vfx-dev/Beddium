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

package com.ventooth.beddium.config;

import com.falsepattern.lib.config.ConfigException;
import com.falsepattern.lib.config.SimpleGuiFactory;
import com.ventooth.beddium.Tags;
import lombok.NoArgsConstructor;

import net.minecraft.client.gui.GuiScreen;

import cpw.mods.fml.client.config.GuiConfig;

import static com.ventooth.beddium.config.Configs.cfgElements;

@NoArgsConstructor
@SuppressWarnings("unused")
public final class ConfigGuiFactory implements SimpleGuiFactory {
    private static final String CONFIG_GUI_NAME = Tags.MOD_NAME + " Config";

    @Override
    public Class<? extends GuiScreen> mainConfigGuiClass() {
        return MyGuiConfig.class;
    }

    public static final class MyGuiConfig extends GuiConfig {
        public MyGuiConfig(GuiScreen parent) throws ConfigException {
            super(parent, cfgElements(), Tags.MOD_ID, false, false, CONFIG_GUI_NAME);
        }
    }
}