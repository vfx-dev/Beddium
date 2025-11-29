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

import lombok.AccessLevel;
import lombok.NoArgsConstructor;
import lombok.val;
import org.embeddedt.embeddium.impl.gl.attribute.GlVertexAttributeFormat;
import org.embeddedt.embeddium.impl.gl.attribute.GlVertexFormat;
import org.embeddedt.embeddium.impl.render.chunk.terrain.material.Material;
import org.embeddedt.embeddium.impl.render.chunk.vertex.format.ChunkVertexEncoder;
import org.embeddedt.embeddium.impl.lwjgl2.MemoryUtil;

import java.util.Map;

import static com.ventooth.beddium.modules.TerrainRendering.vertex.CompatibleChunkVertex.encodeDrawParameters;
import static com.ventooth.beddium.modules.TerrainRendering.vertex.CompatibleChunkVertex.encodeLightRPLE;
import static com.ventooth.beddium.modules.TerrainRendering.vertex.CompatibleChunkVertex.encodeTexture;

@SuppressWarnings({
        "PointlessArithmeticExpression",
        "PointlessBitwiseExpression"
})
@NoArgsConstructor(access = AccessLevel.PRIVATE)
final class RPLEChunkVertex implements CompatibleChunkVertex {
    static final CompatibleChunkVertex INSTANCE = new RPLEChunkVertex();

    private static final int ATTRIB_STRIDE = 28 + (2 * 4);
    private static final GlVertexFormat VERTEX_FORMAT = GlVertexFormat.builder(ATTRIB_STRIDE)
                                                                      .addElement("a_PosId", 0, GlVertexAttributeFormat.FLOAT, 3, false, false)
                                                                      .addElement("a_Color", 12, GlVertexAttributeFormat.UNSIGNED_BYTE, 4, true, false)
                                                                      .addElement("a_TexCoord", 16, GlVertexAttributeFormat.FLOAT, 2, false, false)
                                                                      .addElement("a_LightCoord_r", 24, GlVertexAttributeFormat.UNSIGNED_INT, 1, false, true)
                                                                      .addElement("a_LightCoord_g", 28, GlVertexAttributeFormat.UNSIGNED_INT, 1, false, true)
                                                                      .addElement("a_LightCoord_b", 32, GlVertexAttributeFormat.UNSIGNED_INT, 1, false, true)
                                                                      .build();

    private static final int RAWBUF_STRIDE = 8 + 2;

    // Tess->Vert
    private static void decode(RPLEChunkVertex.Vertex vertex, int[] rawBuffer, int ptr) {
        vertex.x = Float.intBitsToFloat(rawBuffer[ptr + 0]);
        vertex.y = Float.intBitsToFloat(rawBuffer[ptr + 1]);
        vertex.z = Float.intBitsToFloat(rawBuffer[ptr + 2]);

        vertex.u = Float.intBitsToFloat(rawBuffer[ptr + 3]);
        vertex.v = Float.intBitsToFloat(rawBuffer[ptr + 4]);

        vertex.color = rawBuffer[ptr + 5];
        vertex.vanillaNormal = rawBuffer[ptr + 6];

        vertex.rple$lightR = rawBuffer[ptr + 7];
        vertex.rple$lightG = rawBuffer[ptr + 8];
        vertex.rple$lightB = rawBuffer[ptr + 9];
    }

    // Vert->Native
    private static void encode(long ptr, Material material, RPLEChunkVertex.Vertex vertex, int sectionIndex) {
        MemoryUtil.memPutFloat(ptr + 0, vertex.x);
        MemoryUtil.memPutFloat(ptr + 4, vertex.y);
        MemoryUtil.memPutFloat(ptr + 8, vertex.z);
        MemoryUtil.memPutInt(ptr + 12, vertex.color);
        MemoryUtil.memPutFloat(ptr + 16, encodeTexture(vertex.u));
        MemoryUtil.memPutFloat(ptr + 20, encodeTexture(vertex.v));

        MemoryUtil.memPutInt(ptr + 24, (encodeDrawParameters(material, sectionIndex) << 0) | (encodeLightRPLE(vertex.rple$lightR) << 16));
        // TODO: Pack both of these bad bois into a single value, they range from 0 to 240 anyway!
        MemoryUtil.memPutInt(ptr + 28, encodeLightRPLE(vertex.rple$lightG) << 16);
        MemoryUtil.memPutInt(ptr + 32, encodeLightRPLE(vertex.rple$lightB) << 16);
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
            if (baseVertex instanceof RPLEChunkVertex.Vertex vertex) {
                encode(ptr, material, vertex, sectionIndex);
                return ptr + ATTRIB_STRIDE;
            }
            if (baseVertex == null) {
                throw new NullPointerException("Cannot encode null vertex");
            }
            val expected = RPLEChunkVertex.Vertex.class.getName();
            val actual = baseVertex.getClass().getName();
            throw new IllegalArgumentException("Wrong vertex type, expected: %s got: %s".formatted(expected, actual));
        };
    }

    @Override
    public Map<String, String> getDefines() {
        var map = CompatibleChunkVertex.super.getDefines();
        map.put("RPLE", "");
        return map;
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

    private static class Vertex extends CompatibleChunkVertex.Vertex {
        public int rple$lightR;
        public int rple$lightG;
        public int rple$lightB;

        @Override
        public void copyRawBuffer(int[] rawBuffer, int ptr) {
            decode(this, rawBuffer, ptr);
        }
    }
}
