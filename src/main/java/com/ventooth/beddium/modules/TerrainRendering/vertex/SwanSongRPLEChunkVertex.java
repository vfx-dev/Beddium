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

package com.ventooth.beddium.modules.TerrainRendering.vertex;

import com.ventooth.beddium.modules.MEGAChunks.MegaChunkMetadata;
import lombok.AccessLevel;
import lombok.NoArgsConstructor;
import lombok.val;
import org.embeddedt.embeddium.impl.gl.array.GlVertexArray;
import org.embeddedt.embeddium.impl.gl.attribute.GlVertexAttributeBinding;
import org.embeddedt.embeddium.impl.gl.attribute.GlVertexAttributeFormat;
import org.embeddedt.embeddium.impl.gl.attribute.GlVertexFormat;
import org.embeddedt.embeddium.impl.gl.device.CommandList;
import org.embeddedt.embeddium.impl.gl.tessellation.GlAbstractTessellation;
import org.embeddedt.embeddium.impl.gl.tessellation.GlVertexArrayTessellation;
import org.embeddedt.embeddium.impl.gl.tessellation.TessellationBinding;
import org.embeddedt.embeddium.impl.render.chunk.LocalSectionIndex;
import org.embeddedt.embeddium.impl.render.chunk.terrain.material.Material;
import org.embeddedt.embeddium.impl.render.chunk.vertex.format.ChunkVertexEncoder;
import org.lwjgl.opengl.GL11;
import org.lwjgl.opengl.GL13;
import org.lwjgl.opengl.GL20;
import org.embeddedt.embeddium.impl.lwjgl2.MemoryUtil;

@SuppressWarnings("PointlessArithmeticExpression")
@NoArgsConstructor(access = AccessLevel.PRIVATE)
final class SwanSongRPLEChunkVertex implements CompatibleChunkVertex {
    static final CompatibleChunkVertex INSTANCE = new SwanSongRPLEChunkVertex();

    // POS              3 floats 12 bytes offset 0
    // TEXTURE          2 floats  8 bytes offset 12
    // COLOR            4 bytes   4 bytes offset 20
    // BRIGHTNESS_R     2 shorts  4 bytes offset 24
    // ENTITY_DATA_2    3 shorts  6 bytes offset 28
    // NORMAL           3 floats 12 bytes offset 36
    // TANGENT          4 floats 16 bytes offset 48
    // MIDTEXTURE       2 floats  8 bytes offset 64
    // EDGETEXURE       2 floats  8 bytes offset 72
    // BRIGHTNESS_G     2 shorts  4 bytes offset 80
    // BRIGHTNESS_B     2 shorts  4 bytes offset 84
    private static final int ATTRIB_STRIDE = (3 + 2 + 1 + 1 + 2 + 3 + 4 + 2 + 2 + 2 + 2) * 4;
    private static final GlVertexFormat VERTEX_FORMAT = GlVertexFormat.builder(ATTRIB_STRIDE)
                                                                      .addElement("POS", 0, GlVertexAttributeFormat.FLOAT, 3, false, false)
                                                                      .addElement("TEXTURE", 12, GlVertexAttributeFormat.FLOAT, 2, true, false)
                                                                      .addElement("COLOR", 20, GlVertexAttributeFormat.UNSIGNED_BYTE, 4, false, false)
                                                                      .addElement("BRIGHTNESS_R", 24, GlVertexAttributeFormat.SHORT, 2, false, false)
                                                                      .addElement("ENTITY_DATA", 28, GlVertexAttributeFormat.SHORT, 3, false, false)
                                                                      .addElement("NORMAL", 36, GlVertexAttributeFormat.FLOAT, 3, false, false)
                                                                      .addElement("TANGENT", 48, GlVertexAttributeFormat.FLOAT, 4, false, false)
                                                                      .addElement("MIDTEXTURE", 64, GlVertexAttributeFormat.FLOAT, 2, false, false)
                                                                      .addElement("EDGETEXURE", 72, GlVertexAttributeFormat.FLOAT, 2, false, false)
                                                                      .addElement("BRIGHTNESS_G", 80, GlVertexAttributeFormat.SHORT, 2, false, false)
                                                                      .addElement("BRIGHTNESS_B", 84, GlVertexAttributeFormat.SHORT, 2, false, false)
                                                                      .build();


    // pos + uv + color + brightness_r + entityData + normal + tangent + midtexture + brightness_g + brightness_b
    private static final int RAWBUF_STRIDE = 3 + 2 + 1 + 1 + 2 + 3 + 4 + 2 + 2 + 1 + 1;

    // Tess->Vert
    private static void decode(Vertex vertex, int[] rawBuffer, int ptr) {
        vertex.x = Float.intBitsToFloat(rawBuffer[ptr + 0]);
        vertex.y = Float.intBitsToFloat(rawBuffer[ptr + 1]);
        vertex.z = Float.intBitsToFloat(rawBuffer[ptr + 2]);

        vertex.u = Float.intBitsToFloat(rawBuffer[ptr + 3]);
        vertex.v = Float.intBitsToFloat(rawBuffer[ptr + 4]);

        vertex.color = rawBuffer[ptr + 5];
        vertex.rple$lightR = rawBuffer[ptr + 6];

        vertex.entityData = rawBuffer[ptr + 7];
        vertex.entityData2 = rawBuffer[ptr + 8];

        vertex.normalX = Float.intBitsToFloat(rawBuffer[ptr + 9]);
        vertex.normalY = Float.intBitsToFloat(rawBuffer[ptr + 10]);
        vertex.normalZ = Float.intBitsToFloat(rawBuffer[ptr + 11]);

        vertex.tangentX = Float.intBitsToFloat(rawBuffer[ptr + 12]);
        vertex.tangentY = Float.intBitsToFloat(rawBuffer[ptr + 13]);
        vertex.tangentZ = Float.intBitsToFloat(rawBuffer[ptr + 14]);
        vertex.tangentW = Float.intBitsToFloat(rawBuffer[ptr + 15]);

        vertex.midTextureU = Float.intBitsToFloat(rawBuffer[ptr + 16]);
        vertex.midTextureV = Float.intBitsToFloat(rawBuffer[ptr + 17]);
        vertex.edgeTextureU = Float.intBitsToFloat(rawBuffer[ptr + 18]);
        vertex.edgeTextureV = Float.intBitsToFloat(rawBuffer[ptr + 19]);

        vertex.rple$lightG = rawBuffer[ptr + 20];
        vertex.rple$lightB = rawBuffer[ptr + 21];
    }

    // Vert->Native
    @SuppressWarnings("unused")
    private static void encode(long ptr, Material material, Vertex vertex, int sectionIndex) {
        // TODO: [SWAN_SONG] Move translation math to shader? Figure out how Iris does it?
        MemoryUtil.memPutFloat(ptr + 0, vertex.x + (LocalSectionIndex.unpackX(sectionIndex) * MegaChunkMetadata.BLOCKS_PER_WR_EDGE_FLOAT));
        MemoryUtil.memPutFloat(ptr + 4, vertex.y + (LocalSectionIndex.unpackY(sectionIndex) * MegaChunkMetadata.BLOCKS_PER_WR_EDGE_FLOAT));
        MemoryUtil.memPutFloat(ptr + 8, vertex.z + (LocalSectionIndex.unpackZ(sectionIndex) * MegaChunkMetadata.BLOCKS_PER_WR_EDGE_FLOAT));

        MemoryUtil.memPutFloat(ptr + 12, vertex.u);
        MemoryUtil.memPutFloat(ptr + 16, vertex.v);

        MemoryUtil.memPutInt(ptr + 20, vertex.color); // 4 bytes
        MemoryUtil.memPutInt(ptr + 24, vertex.rple$lightR); // 2 shorts

        MemoryUtil.memPutInt(ptr + 28, vertex.entityData);  // 2 shorts
        MemoryUtil.memPutInt(ptr + 32, vertex.entityData2); // 2 shorts

        MemoryUtil.memPutFloat(ptr + 36, vertex.normalX);
        MemoryUtil.memPutFloat(ptr + 40, vertex.normalY);
        MemoryUtil.memPutFloat(ptr + 44, vertex.normalZ);

        MemoryUtil.memPutFloat(ptr + 48, vertex.tangentX);
        MemoryUtil.memPutFloat(ptr + 52, vertex.tangentY);
        MemoryUtil.memPutFloat(ptr + 56, vertex.tangentZ);
        MemoryUtil.memPutFloat(ptr + 60, vertex.tangentW);

        MemoryUtil.memPutFloat(ptr + 64, vertex.midTextureU);
        MemoryUtil.memPutFloat(ptr + 68, vertex.midTextureV);
        MemoryUtil.memPutFloat(ptr + 72, vertex.edgeTextureU);
        MemoryUtil.memPutFloat(ptr + 76, vertex.edgeTextureV);

        MemoryUtil.memPutInt(ptr + 80, vertex.rple$lightG); // 2 shorts
        MemoryUtil.memPutInt(ptr + 84, vertex.rple$lightB); // 2 shorts
    }

    private static void bindLegacyAttribute(GlVertexAttributeBinding attrib) {
        val name = attrib.getName();

        val size = attrib.getCount();
        val type = attrib.getFormat().typeId();
        val stride = attrib.getStride();
        val pointer = attrib.getPointer();
        switch (name) {
            case "POS" -> {
                GL11.glVertexPointer(size, type, stride, pointer);
                GL11.glEnableClientState(GL11.GL_VERTEX_ARRAY);
            }
            case "TEXTURE" -> {
                GL13.glClientActiveTexture(GL13.GL_TEXTURE0);
                GL11.glTexCoordPointer(size, type, stride, pointer);
                GL11.glEnableClientState(GL11.GL_TEXTURE_COORD_ARRAY);
            }
            case "COLOR" -> {
                GL11.glColorPointer(size, type, stride, pointer);
                GL11.glEnableClientState(GL11.GL_COLOR_ARRAY);
            }
            case "BRIGHTNESS_R" -> {
                GL13.glClientActiveTexture(GL13.GL_TEXTURE1);
                GL11.glTexCoordPointer(size, type, stride, pointer);
                GL11.glEnableClientState(GL11.GL_TEXTURE_COORD_ARRAY);
                GL13.glClientActiveTexture(GL13.GL_TEXTURE0);
            }
            case "BRIGHTNESS_G" -> {
                GL13.glClientActiveTexture(GL13.GL_TEXTURE6);
                GL11.glTexCoordPointer(size, type, stride, pointer);
                GL11.glEnableClientState(GL11.GL_TEXTURE_COORD_ARRAY);
                GL13.glClientActiveTexture(GL13.GL_TEXTURE0);
            }
            case "BRIGHTNESS_B" -> {
                GL13.glClientActiveTexture(GL13.GL_TEXTURE7);
                GL11.glTexCoordPointer(size, type, stride, pointer);
                GL11.glEnableClientState(GL11.GL_TEXTURE_COORD_ARRAY);
                GL13.glClientActiveTexture(GL13.GL_TEXTURE0);
            }
            case "ENTITY_DATA" -> {
                val entityAttrib = 10;
                GL20.glVertexAttribPointer(entityAttrib, size, type, false, stride, pointer);
                GL20.glEnableVertexAttribArray(entityAttrib);
            }
            case "TANGENT" -> {
                val tangentAttrib = 12;
                GL20.glVertexAttribPointer(tangentAttrib, size, type, false, stride, pointer);
                GL20.glEnableVertexAttribArray(tangentAttrib);
            }
            case "MIDTEXTURE" -> {
                val midTexCoordAttrib = 11;
                GL20.glVertexAttribPointer(midTexCoordAttrib, size, type, false, stride, pointer);
                GL20.glEnableVertexAttribArray(midTexCoordAttrib);
            }
            case "NORMAL" -> {
                GL11.glNormalPointer(type, stride, pointer);
                GL11.glEnableClientState(GL11.GL_NORMAL_ARRAY);
            }
            case "EDGETEXURE" -> {
                val edgeTexCoordAttrib = 13;
                GL20.glVertexAttribPointer(edgeTexCoordAttrib, size, type, false, stride, pointer);
                GL20.glEnableVertexAttribArray(edgeTexCoordAttrib);
            }
            default -> throw new IllegalStateException("Unexpected value: " + name);
        }
    }

    @Override
    public int attribStride() {
        return ATTRIB_STRIDE;
    }

    @Override
    public int rawBufStride() {
        return RAWBUF_STRIDE;
    }

    @Override
    public GlVertexFormat getVertexFormat() {
        return VERTEX_FORMAT;
    }

    @Override
    public ChunkVertexEncoder createEncoder() {
        return (ptr, material, baseVertex, sectionIndex) -> {
            if (baseVertex instanceof Vertex vertex) {
                encode(ptr, material, vertex, sectionIndex);
                return ptr + ATTRIB_STRIDE;
            }
            if (baseVertex == null) {
                throw new NullPointerException("Cannot encode null vertex");
            }
            val expected = Vertex.class.getName();
            val actual = baseVertex.getClass().getName();
            throw new IllegalArgumentException("Wrong vertex type, expected: %s got: %s".formatted(expected, actual));
        };
    }

    @Override
    public CompatibleChunkVertex.Vertex[] uninitializedQuad() {
        return new Vertex[]{
                new Vertex(),
                new Vertex(),
                new Vertex(),
                new Vertex()
        };
    }

    @Override
    public CompatibleChunkVertex.Tessellation getTessellation(GlVertexArray array, TessellationBinding[] bindings) {
        return new SwanSongRPLEChunkVertex.Tessellation(array, bindings);
    }

    private static class Vertex extends CompatibleChunkVertex.Vertex {
        public int rple$lightR;

        public int entityData;
        public int entityData2;

        public float normalX;
        public float normalY;
        public float normalZ;

        public float tangentX;
        public float tangentY;
        public float tangentZ;
        public float tangentW;

        public float midTextureU;
        public float midTextureV;
        public float edgeTextureU;
        public float edgeTextureV;

        public int rple$lightG;
        public int rple$lightB;

        @Override
        public void copyRawBuffer(int[] rawBuffer, int ptr) {
            decode(this, rawBuffer, ptr);
        }
    }

    private static class Tessellation extends CompatibleChunkVertex.Tessellation {
        Tessellation(GlVertexArray array, TessellationBinding[] bindings) {
            super(array, bindings);
        }

        @Override
        protected void bindAttributes(CommandList commandList) {
            for (val binding : this.bindings) {
                commandList.bindBuffer(binding.target(), binding.buffer());
                for (val attrib : binding.attributeBindings()) {
                    bindLegacyAttribute(attrib);
                }
            }
        }
    }
}
