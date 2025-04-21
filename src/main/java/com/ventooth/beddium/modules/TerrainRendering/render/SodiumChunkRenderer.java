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

import com.ventooth.beddium.modules.TerrainRendering.shader.ArchaicChunkShaderInterface;
import org.embeddedt.embeddium.impl.gl.device.RenderDevice;
import org.embeddedt.embeddium.impl.gl.shader.GlProgram;
import org.embeddedt.embeddium.impl.gl.shader.GlShader;
import org.embeddedt.embeddium.impl.gl.shader.ShaderConstants;
import org.embeddedt.embeddium.impl.gl.shader.ShaderType;
import org.embeddedt.embeddium.impl.render.chunk.RenderPassConfiguration;
import org.embeddedt.embeddium.impl.render.chunk.shader.ChunkShaderBindingPoints;
import org.embeddedt.embeddium.impl.render.chunk.shader.ChunkShaderInterface;
import org.embeddedt.embeddium.impl.render.chunk.shader.ChunkShaderOptions;
import org.embeddedt.embeddium.impl.render.chunk.shader.ChunkShaderTextureSlot;
import org.embeddedt.embeddium.impl.render.shader.ShaderLoader;

final class SodiumChunkRenderer extends CompatibleChunkRenderer {
    SodiumChunkRenderer(RenderDevice device, RenderPassConfiguration<?> renderPassConfiguration) {
        super(device, renderPassConfiguration);
    }

    @Override
    protected GlProgram<ChunkShaderInterface> createShader(String path, ChunkShaderOptions options) {
        ShaderConstants constants = options.constants();

        GlShader vertShader = ShaderLoader.loadShader(ShaderType.VERTEX, "beddium:" + path + ".vsh", constants);

        GlShader fragShader = ShaderLoader.loadShader(ShaderType.FRAGMENT, "beddium:" + path + ".fsh", constants);

        try {
            var builder = GlProgram.builder("beddium:chunk_shader").attachShader(vertShader).attachShader(fragShader);
            int i = 0;
            for (var attr : options.pass().vertexType().getVertexFormat().getAttributes()) {
                builder.bindAttribute(attr.getName(), i++);
            }
            builder.bindFragmentData("fragColor", ChunkShaderBindingPoints.FRAG_COLOR);
            return builder.link((shader) -> new ArchaicChunkShaderInterface(shader, options));
        } finally {
            vertShader.delete();
            fragShader.delete();
        }
    }

    @Override
    protected void configureShaderInterface(ChunkShaderInterface shader) {
        // TODO: Can the RPLE textures be relocated here?
        shader.setTextureSlot(ChunkShaderTextureSlot.BLOCK, 0);
        shader.setTextureSlot(ChunkShaderTextureSlot.LIGHT, 1);
    }
}
