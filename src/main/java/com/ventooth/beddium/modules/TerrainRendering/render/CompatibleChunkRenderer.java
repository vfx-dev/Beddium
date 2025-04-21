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

package com.ventooth.beddium.modules.TerrainRendering.render;

import com.ventooth.beddium.Compat;
import com.ventooth.beddium.config.TerrainRenderingConfig;
import com.ventooth.beddium.modules.TerrainRendering.vertex.CompatibleChunkVertex;
import lombok.val;
import org.embeddedt.embeddium.impl.gl.array.GlVertexArray;
import org.embeddedt.embeddium.impl.gl.device.CommandList;
import org.embeddedt.embeddium.impl.gl.device.RenderDevice;
import org.embeddedt.embeddium.impl.gl.tessellation.GlTessellation;
import org.embeddedt.embeddium.impl.render.chunk.DefaultChunkRenderer;
import org.embeddedt.embeddium.impl.render.chunk.RenderPassConfiguration;
import org.embeddedt.embeddium.impl.render.chunk.multidraw.DirectMultiDrawEmitter;
import org.embeddedt.embeddium.impl.render.chunk.multidraw.IndirectMultiDrawEmitter;
import org.embeddedt.embeddium.impl.render.chunk.region.RenderRegion;

public abstract sealed class CompatibleChunkRenderer extends DefaultChunkRenderer permits SodiumChunkRenderer, SwanSongChunkRenderer {
    CompatibleChunkRenderer(RenderDevice device, RenderPassConfiguration<?> renderPassConfiguration) {
        super(device, renderPassConfiguration, TerrainRenderingConfig.UseMultiDrawIndirect ? new IndirectMultiDrawEmitter() : new DirectMultiDrawEmitter());
    }

    public static CompatibleChunkRenderer get(RenderDevice device, RenderPassConfiguration<?> renderPassConfiguration) {
        if (Compat.isSwansongInstalled()) {
            return new SwanSongChunkRenderer(device, renderPassConfiguration);
        } else {
            return new SodiumChunkRenderer(device, renderPassConfiguration);
        }
    }

    @Override
    protected GlTessellation createRegionTessellation(CommandList commandList, RenderRegion.DeviceResources resources) {
        val bindings = makeTessellationBindingArray(commandList, resources);
        val tessellation = CompatibleChunkVertex.get().getTessellation(new GlVertexArray(), bindings);
        tessellation.init(commandList);
        return tessellation;
    }
}
