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
import com.falsepattern.lib.config.ConfigurationManager;
import lombok.AccessLevel;
import lombok.NoArgsConstructor;
import lombok.SneakyThrows;
import lombok.val;

import cpw.mods.fml.client.config.DummyConfigElement;
import cpw.mods.fml.client.config.IConfigElement;

import java.util.ArrayList;
import java.util.List;

@NoArgsConstructor(access = AccessLevel.PRIVATE)
public final class Configs {
    public static void poke() {
    }

    static {
        init();
    }

    @SneakyThrows
    private static void init() {
        ConfigurationManager.initialize(cfgClasses());
    }

    static Class<?>[] cfgClasses() {
        val categories = cfgCategories(true);
        val types = new ArrayList<Class<?>>();
        for (val category : categories) {
            types.add(category.type());
        }
        return types.toArray(new Class<?>[0]);
    }

    @SuppressWarnings("rawtypes")
    static List<IConfigElement> cfgElements() throws ConfigException {
        val categories = cfgCategories(false);
        val elements = new ArrayList<IConfigElement>();
        for (val category : categories) {
            elements.add(category.asElement());
        }
        return elements;
    }

    static List<CfgCategory> cfgCategories(boolean init) {
        val categories = new ArrayList<CfgCategory>();
        categories.add(new CfgCategory("00_modules", "config.beddium.modules.Modules", ModuleConfig.class));

        if (init || ModuleConfig.TerrainRendering) {
            categories.add(new CfgCategory("01_terrain", "config.beddium.modules.TerrainRendering", TerrainRenderingConfig.class));
        }

        return categories;
    }

    record CfgCategory(String name, String lang, Class<?> type) {
        @SuppressWarnings({
                "rawtypes",
                "unchecked"
        })
        IConfigElement asElement() throws ConfigException {
            val innerElements = ConfigurationManager.getConfigElementsMulti(type);
            return new DummyConfigElement.DummyCategoryElement(name, lang, innerElements);
        }
    }
}
