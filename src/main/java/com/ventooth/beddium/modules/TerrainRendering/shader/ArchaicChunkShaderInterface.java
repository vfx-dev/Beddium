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

package com.ventooth.beddium.modules.TerrainRendering.shader;

import com.ventooth.beddium.Compat;
import org.embeddedt.embeddium.impl.gl.shader.ShaderBindingContext;
import org.embeddedt.embeddium.impl.gl.shader.uniform.GlUniformInt;
import org.embeddedt.embeddium.impl.render.chunk.shader.ChunkShaderOptions;
import org.embeddedt.embeddium.impl.render.chunk.shader.DefaultChunkShaderInterface;
import org.embeddedt.embeddium.impl.render.chunk.terrain.TerrainRenderPass;

// TODO: Contact Embeddedt about this, it sucks.
public class ArchaicChunkShaderInterface extends DefaultChunkShaderInterface {
    private GlUniformInt lightTexG;
    private GlUniformInt lightTexB;

    public ArchaicChunkShaderInterface(ShaderBindingContext context, ChunkShaderOptions options) {
        super(context, options);

        if (Compat.rpleInstalled()) {
            // TODO: Uniforms SHOULD NOT HARD FAIL
            lightTexG = context.bindUniform("u_LightTex_g", GlUniformInt::new);
            lightTexB = context.bindUniform("u_LightTex_b", GlUniformInt::new);
        }
    }

    @Override
    public void setupState(TerrainRenderPass pass) {
        super.setupState(pass);
        if (Compat.rpleInstalled()) {
            lightTexG.setInt(2);
            lightTexB.setInt(3);
        }
    }
}
