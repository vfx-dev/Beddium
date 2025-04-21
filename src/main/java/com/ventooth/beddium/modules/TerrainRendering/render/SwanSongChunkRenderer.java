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
import org.embeddedt.embeddium.impl.gl.device.RenderDevice;
import org.embeddedt.embeddium.impl.gl.shader.GlProgram;
import org.embeddedt.embeddium.impl.gl.shader.ShaderBindingContext;
import org.embeddedt.embeddium.impl.gl.tessellation.GlPrimitiveType;
import org.embeddedt.embeddium.impl.render.chunk.RenderPassConfiguration;
import org.embeddedt.embeddium.impl.render.chunk.compile.sorting.ChunkPrimitiveType;
import org.embeddedt.embeddium.impl.render.chunk.compile.sorting.QuadPrimitiveType;
import org.embeddedt.embeddium.impl.render.chunk.shader.ChunkShaderInterface;
import org.embeddedt.embeddium.impl.render.chunk.shader.ChunkShaderOptions;
import org.embeddedt.embeddium.impl.render.chunk.shader.ChunkShaderTextureSlot;
import org.embeddedt.embeddium.impl.render.chunk.terrain.TerrainRenderPass;
import org.joml.Matrix4fc;
import org.lwjgl.opengl.GL11;

final class SwanSongChunkRenderer extends CompatibleChunkRenderer {
    private GlPrimitiveType primitiveType;

    SwanSongChunkRenderer(RenderDevice device, RenderPassConfiguration<?> renderPassConfiguration) {
        super(device, renderPassConfiguration);
        this.activeProgram = new SwanSongProgram();
        this.primitiveType = null;
    }

    @Override
    protected boolean useBlockFaceCulling() {
        return !Compat.shadowPassActive(); // TODO: Block face culling for shadow too?
    }

    @Override
    protected void begin(TerrainRenderPass pass) {
        pass.startDrawing();

        setPrimitiveType(pass.primitiveType());

        GL11.glMatrixMode(GL11.GL_MODELVIEW);
        GL11.glPushMatrix();
    }

    private void setPrimitiveType(ChunkPrimitiveType chunkPrimitiveType) {
        if (chunkPrimitiveType == QuadPrimitiveType.DIRECT) {
            primitiveType = GlPrimitiveType.QUADS;
        } else if (chunkPrimitiveType == QuadPrimitiveType.TRIANGULATED) {
            primitiveType = GlPrimitiveType.TRIANGLES;
        } else {
            throw new IllegalArgumentException("Unknown primitive type");
        }
    }

    @Override
    protected void end(TerrainRenderPass pass) {
        GL11.glMatrixMode(GL11.GL_MODELVIEW);
        GL11.glPopMatrix();

        pass.endDrawing();
    }

    private void setRegionOffset(float x, float y, float z) {
        GL11.glMatrixMode(GL11.GL_MODELVIEW);

        GL11.glPopMatrix();  // We assume that the matrix was already pushed by begin()
        GL11.glPushMatrix(); // So we pop it, then push again before setting state

        // TODO: [SWAN_SONG] Move translation math to shader? Figure out how Iris does it?
        GL11.glTranslatef(x, y, z);
    }

    @Override
    protected GlProgram<ChunkShaderInterface> compileProgram(ChunkShaderOptions options) {
        throw new UnsupportedOperationException("Shaders managed by SwanSong");
    }

    @Override
    protected GlProgram<ChunkShaderInterface> createShader(String path, ChunkShaderOptions options) {
        throw new UnsupportedOperationException("Shaders managed by SwanSong");
    }

    @Override
    protected void configureShaderInterface(ChunkShaderInterface shader) {
        // NO-OP
    }

    private final class SwanSongProgram extends GlProgram<ChunkShaderInterface> {
        static final int FAKE_PROGRAM_NAME = 10_000_000;

        SwanSongProgram() {
            super(FAKE_PROGRAM_NAME, SwanSongChunkShaderInterface::new);
        }

        // region NO-OP

        @Override
        public void bind() {
            // NO-OP
        }

        @Override
        public void unbind() {
            // NO-OP
        }

        @Override
        protected void destroyInternal() {
            // NO-OP
        }

        // endregion
    }

    private final class SwanSongChunkShaderInterface implements ChunkShaderInterface {
        @Override
        public GlPrimitiveType getPrimitiveType() {
            if (SwanSongChunkRenderer.this.primitiveType == null) {
                throw new IllegalStateException("Primitive type not set");
            }
            return SwanSongChunkRenderer.this.primitiveType;
        }

        @Override
        public void setRegionOffset(float x, float y, float z) {
            SwanSongChunkRenderer.this.setRegionOffset(x, y, z);
        }

        // region NO-OP

        @Override
        public void setProjectionMatrix(Matrix4fc matrix) {
            // NO-OP
        }

        @Override
        public void setModelViewMatrix(Matrix4fc matrix) {
            // NO-OP
        }

        private SwanSongChunkShaderInterface(ShaderBindingContext ctx) {
            // NO-OP
        }

        @Override
        public void setupState(TerrainRenderPass pass) {
            // NO-OP
        }

        @Override
        public void setTextureSlot(ChunkShaderTextureSlot slot, int val) {
            // NO-OP
        }

        // endregion
    }
}
