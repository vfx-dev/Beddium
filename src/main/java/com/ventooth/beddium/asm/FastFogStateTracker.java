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

import com.falsepattern.lib.turboasm.ClassNodeHandle;
import com.falsepattern.lib.turboasm.TurboClassTransformer;
import com.ventooth.beddium.Tags;
import lombok.NoArgsConstructor;
import lombok.val;
import org.jetbrains.annotations.NotNull;
import org.lwjgl.opengl.GL11;
import org.objectweb.asm.Opcodes;
import org.objectweb.asm.tree.AbstractInsnNode;
import org.objectweb.asm.tree.FieldInsnNode;
import org.objectweb.asm.tree.InsnList;
import org.objectweb.asm.tree.InsnNode;
import org.objectweb.asm.tree.IntInsnNode;
import org.objectweb.asm.tree.MethodInsnNode;

@NoArgsConstructor
@SuppressWarnings("UnstableApiUsage")
public final class FastFogStateTracker implements TurboClassTransformer {
    @Override
    public String owner() {
        return Tags.MOD_NAME;
    }

    @Override
    public String name() {
        return "FastFogStateTracker";
    }

    @Override
    public boolean shouldTransformClass(@NotNull String className, @NotNull ClassNodeHandle classNode) {
        // TODO: Set this to a whitelist
        return className.equals("com.salvestrom.w2theJungle.JungleLivingEvent");
    }

    @Override
    public boolean transformClass(@NotNull String className, @NotNull ClassNodeHandle classNode) {
        val methods = classNode.getNode().methods;
        var modified = false;
        for (val method : methods) {
            val instList = method.instructions;
            var instNode = instList.getFirst();
            while (instNode != null) {
                modified |= tryHook(instList, instNode);
                instNode = instNode.getNext();
            }
        }
        return modified;
    }

    private static boolean tryHook(InsnList insnList, AbstractInsnNode instNode) {
        if (!(instNode instanceof MethodInsnNode methodInsnNode)) {
            return false; // Looking for functions
        }
        if (!"org/lwjgl/opengl/GL11".equals(methodInsnNode.owner)) {
            return false; // That belong to GL11
        }
        return switch (methodInsnNode.name) {
            case "glEnable" -> hook_glEnable(insnList, methodInsnNode);
            case "glDisable" -> hook_glDisable(insnList, methodInsnNode);
            case "glFog" -> hook_glFog(insnList, methodInsnNode);
            case "glFogi" -> hook_glFogi(insnList, methodInsnNode);
            case "glFogf" -> hook_glFogf(insnList, methodInsnNode);
            default -> false;
        };
    }

    private static boolean hook_glEnable(InsnList insnList, MethodInsnNode methodInsnNode) {
        if (!"(I)V".equals(methodInsnNode.desc)) {
            return false;
        }

        // Make sure cap is GL_FOG
        val prevInstNode = methodInsnNode.getPrevious();
        if (!(prevInstNode instanceof IntInsnNode intInsnNode)) {
            return false;
        }
        if (intInsnNode.getOpcode() != Opcodes.SIPUSH) {
            return false;
        }
        if (intInsnNode.operand != GL11.GL_FOG) {
            return false;
        }

        // After the call: GLStateManagerFogServiceFast.fog=true
        val instA = new InsnNode(Opcodes.ICONST_1);
        val instB = new FieldInsnNode(Opcodes.PUTSTATIC,
                                      "com/ventooth/beddium/modules/TerrainRendering/services/GLStateManagerFogServiceFast",
                                      "fog",
                                      "Z");
        insnList.insert(methodInsnNode, instA);
        insnList.insert(instA, instB);
        return true;
    }

    private static boolean hook_glDisable(InsnList insnList, MethodInsnNode methodInsnNode) {
        if (!"(I)V".equals(methodInsnNode.desc)) {
            return false;
        }

        // Make sure cap is GL_FOG
        val prevInstNode = methodInsnNode.getPrevious();
        if (!(prevInstNode instanceof IntInsnNode intInsnNode)) {
            return false;
        }
        if (intInsnNode.getOpcode() != Opcodes.SIPUSH) {
            return false;
        }
        if (intInsnNode.operand != GL11.GL_FOG) {
            return false;
        }

        // After the call: GLStateManagerFogServiceFast.fog=false
        val instA = new InsnNode(Opcodes.ICONST_0);
        val instB = new FieldInsnNode(Opcodes.PUTSTATIC,
                                      "com/ventooth/beddium/modules/TerrainRendering/services/GLStateManagerFogServiceFast",
                                      "fog",
                                      "Z");
        insnList.insert(methodInsnNode, instA);
        insnList.insert(instA, instB);
        return true;
    }

    private static boolean hook_glFog(InsnList insnList, MethodInsnNode methodInsnNode) {
        if (!"(ILjava/nio/FloatBuffer;)V".equals(methodInsnNode.desc)) {
            return false;
        }

        // Make sure pname is GL_FOG_COLOR
        val prevInstNode = methodInsnNode.getPrevious()
                                         .getPrevious();
        if (!(prevInstNode instanceof IntInsnNode intInsnNode)) {
            return false;
        }
        if (intInsnNode.getOpcode() != Opcodes.SIPUSH) {
            return false;
        }
        if (intInsnNode.operand != GL11.GL_FOG_COLOR) {
            return false;
        }

        // No need to dupe, function will passthrough
        val instA = new FieldInsnNode(Opcodes.PUTSTATIC,
                                      "com/ventooth/beddium/modules/TerrainRendering/services/GLStateManagerFogServiceFast",
                                      "setColor",
                                      "(Ljava/nio/FloatBuffer;)Ljava/nio/FloatBuffer;");
        insnList.insertBefore(methodInsnNode, instA);
        return true;
    }

    private static boolean hook_glFogi(InsnList insnList, MethodInsnNode methodInsnNode) {
        if (!"(II)V".equals(methodInsnNode.desc)) {
            return false;
        }

        // Make sure pname is GL_FOG_MODE
        val prevInstNode = methodInsnNode.getPrevious()
                                         .getPrevious();
        if (!(prevInstNode instanceof IntInsnNode intInsnNode)) {
            return false;
        }
        if (intInsnNode.getOpcode() != Opcodes.SIPUSH) {
            return false;
        }
        if (intInsnNode.operand != GL11.GL_FOG_MODE) {
            return false;
        }

        // Dupe the param
        val instA = new InsnNode(Opcodes.DUP);
        // Put param where we need it
        val instB = new FieldInsnNode(Opcodes.PUTSTATIC,
                                      "com/ventooth/beddium/modules/TerrainRendering/services/GLStateManagerFogServiceFast",
                                      "fogMode",
                                      "I");
        insnList.insertBefore(methodInsnNode, instA);
        insnList.insert(instA, instB);
        return true;
    }

    private static boolean hook_glFogf(InsnList insnList, MethodInsnNode methodInsnNode) {
        if (!"(IF)V".equals(methodInsnNode.desc)) {
            return false;
        }

        // Grab pname
        val prevInstNode = methodInsnNode.getPrevious()
                                         .getPrevious();
        if (!(prevInstNode instanceof IntInsnNode intInsnNode)) {
            return false;
        }
        if (intInsnNode.getOpcode() != Opcodes.SIPUSH) {
            return false;
        }

        // Identify...
        val fieldName = switch (intInsnNode.operand) {
            case GL11.GL_FOG_START -> "fogStart";
            case GL11.GL_FOG_END -> "fogEnd";
            case GL11.GL_FOG_DENSITY -> "fogDensity";
            default -> null;
        };
        if (fieldName == null) {
            return false;
        }

        // Dupe the param
        val instA = new InsnNode(Opcodes.DUP);
        // Put param where we need it
        val instB = new FieldInsnNode(Opcodes.PUTSTATIC,
                                      "com/ventooth/beddium/modules/TerrainRendering/services/GLStateManagerFogServiceFast",
                                      fieldName,
                                      "F");
        insnList.insertBefore(methodInsnNode, instA);
        insnList.insert(instA, instB);
        return true;
    }
}
