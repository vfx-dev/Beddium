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

package com.ventooth.beddium.asm;

import lombok.val;
import org.objectweb.asm.ClassReader;
import org.objectweb.asm.ClassVisitor;
import org.objectweb.asm.ClassWriter;
import org.objectweb.asm.commons.Remapper;
import org.objectweb.asm.commons.RemappingClassAdapter;
import org.objectweb.asm.tree.AnnotationNode;
import org.objectweb.asm.tree.ClassNode;

import net.minecraft.launchwrapper.IClassTransformer;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashSet;
import java.util.Set;
import java.util.concurrent.atomic.AtomicBoolean;

public class Lwjgl3ifyTransformer implements IClassTransformer {
    private static final String PFX = "org/embeddedt/embeddium/impl/lwjgl2";
    private static final String REPLACE = "org/lwjgl/system";
    @Override
    public byte[] transform(String name, String transformedName, byte[] basicClass) {
        if (!transformedName.startsWith("org.embeddedt.embeddium") && !transformedName.startsWith("com.ventooth.beddium"))
            return basicClass;
        if (basicClass == null)
            return null;
        val w = new ClassWriter(0);
        val isFence = "org.embeddedt.embeddium.impl.gl.sync.GlFence".equals(transformedName);
        AtomicBoolean didRemap = new AtomicBoolean(false);
        var reader = new ClassReader(basicClass);
        var adapter = new RemappingClassAdapter(w, new Remapper() {
            @Override
            public String map(String typeName) {
                if (typeName.startsWith(PFX)) {
                    didRemap.set(true);
                    return REPLACE + typeName.substring(PFX.length());
                }
                if (isFence && "org/lwjgl/opengl/GLSync".equals(typeName)) {
                    return "org/lwjglx/opengl/GLSync";
                }
                return super.map(typeName);
            }

            @Override
            public String mapMethodName(String owner, String name, String descriptor) {
                return switch (name) {
                    case "glUniform1" -> "glUniform1fv";
                    case "glUniformMatrix3" -> "glUniformMatrix3fv";
                    case "glUniformMatrix4" -> "glUniformMatrix4fv";
                    default -> name;
                };
            }
        });
        reader.accept(adapter, ClassReader.EXPAND_FRAMES);
        if (didRemap.get()) {
            w.visitAnnotation("Lme/eigenraven/lwjgl3ify/api/Lwjgl3Aware;", false);
            return w.toByteArray();
        }
        return basicClass;
    }
}
