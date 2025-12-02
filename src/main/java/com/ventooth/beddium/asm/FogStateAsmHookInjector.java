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
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.intellij.lang.annotations.Language;
import org.jetbrains.annotations.NotNull;
import org.objectweb.asm.tree.MethodInsnNode;
import org.objectweb.asm.tree.MethodNode;

import net.minecraft.launchwrapper.Launch;

import java.util.ArrayList;
import java.util.function.Predicate;

@NoArgsConstructor
@SuppressWarnings("UnstableApiUsage")
public final class FogStateAsmHookInjector implements TurboClassTransformer {
    private static final Logger log = LogManager.getLogger(ShareAsm.ASM_NAME + "|" + FogStateAsmHookInjector.class.getSimpleName());

    @Language(value = "JAVA",
              prefix = "import ",
              suffix = ";")
    private static final String HOOK_CLASS = Tags.ROOT_PKG + ".modules.TerrainRendering.fog.FogAsmHooks";

    // region Mappings
    // @formatter:off
    private static final String HOOK_OWNER      = HOOK_CLASS.replace('.', '/');

    private static final String NAME_GL_ENABLE  = "glEnable";
    private static final String NAME_GL_DISABLE = "glDisable";
    private static final String NAME_GL_FOG     = "glFog";
    private static final String NAME_GL_FOG_FV  = "glFogfv";
    private static final String NAME_GL_FOG_I   = "glFogi";
    private static final String NAME_GL_FOG_F   = "glFogf";

    private static final String DESC_GL_ENABLE  = "(I)V";
    private static final String DESC_GL_DISABLE = "(I)V";
    private static final String DESC_GL_FOG     = "(ILjava/nio/FloatBuffer;)V";
    private static final String DESC_GL_FOG_I   = "(II)V";
    private static final String DESC_GL_FOG_F   = "(IF)V";

    private static final String HOOK_GL_ENABLE  = "beddium$glEnable";
    private static final String HOOK_GL_DISABLE = "beddium$glDisable";
    private static final String HOOK_GL_FOG     = "beddium$glFog";
    private static final String HOOK_GL_FOG_I   = "beddium$glFogi";
    private static final String HOOK_GL_FOG_F   = "beddium$glFogf";

    private static final MethodReference SRC_GL_ENABLE  = new MethodReference(NAME_GL_ENABLE,   DESC_GL_ENABLE);
    private static final MethodReference SRC_GL_DISABLE = new MethodReference(NAME_GL_DISABLE,  DESC_GL_DISABLE);
    private static final MethodReference SRC_GL_FOG     = new MethodReference(NAME_GL_FOG,      DESC_GL_FOG);
    private static final MethodReference SRC_GL_FOG_FV  = new MethodReference(NAME_GL_FOG_FV,   DESC_GL_FOG);
    private static final MethodReference SRC_GL_FOG_I   = new MethodReference(NAME_GL_FOG_I,    DESC_GL_FOG_I);
    private static final MethodReference SRC_GL_FOG_F   = new MethodReference(NAME_GL_FOG_F,    DESC_GL_FOG_F);

    private static final MethodReference DST_GL_ENABLE  = new MethodReference(HOOK_GL_ENABLE,   DESC_GL_ENABLE);
    private static final MethodReference DST_GL_DISABLE = new MethodReference(HOOK_GL_DISABLE,  DESC_GL_DISABLE);
    private static final MethodReference DST_GL_FOG     = new MethodReference(HOOK_GL_FOG,      DESC_GL_FOG);
    private static final MethodReference DST_GL_FOG_I   = new MethodReference(HOOK_GL_FOG_I,    DESC_GL_FOG_I);
    private static final MethodReference DST_GL_FOG_F   = new MethodReference(HOOK_GL_FOG_F,    DESC_GL_FOG_F);

    private static final MethodMapping[] MAPPINGS = {new MethodMapping(SRC_GL_ENABLE,   DST_GL_ENABLE),
                                                     new MethodMapping(SRC_GL_DISABLE,  DST_GL_DISABLE),
                                                     new MethodMapping(SRC_GL_FOG,      DST_GL_FOG),
                                                     new MethodMapping(SRC_GL_FOG_FV,   DST_GL_FOG),
                                                     new MethodMapping(SRC_GL_FOG_I,    DST_GL_FOG_I),
                                                     new MethodMapping(SRC_GL_FOG_F,    DST_GL_FOG_F)};
    // @formatter:on
    // endregion

    private static final Predicate<String>[] EXCLUSIONS;

    static {
        val list = new ArrayList<Predicate<String>>();

        {
            list.add(s -> s.equals(HOOK_CLASS));
            log.debug("Added root exclusion: {}", HOOK_CLASS);
        }

        val exclusionCfg = (String[]) Launch.blackboard.get(ShareAsm.TRACKED_FOG_STATE_ASM_EXCLUSIONS_KEY);
        if (exclusionCfg != null) {
            for (val exclusion : exclusionCfg) {
                if (exclusion.endsWith("*")) {
                    val prefix = exclusion.substring(0, exclusion.length() - 1);

                    list.add(s -> s.startsWith(prefix));
                } else {
                    list.add(s -> s.equals(exclusion));
                }
                log.debug("Added exclusion: {}", exclusion);
            }
        }

        //noinspection unchecked
        EXCLUSIONS = list.toArray(new Predicate[0]);
    }

    @Override
    public String owner() {
        return Tags.MOD_NAME;
    }

    @Override
    public String name() {
        return FogStateAsmHookInjector.class.getSimpleName();
    }

    @Override
    public boolean shouldTransformClass(@NotNull String className, @NotNull ClassNodeHandle classNode) {
        for (val exclusion : EXCLUSIONS) {
            if (exclusion.test(className)) {
                return false;
            }
        }
        return true;
    }

    @Override
    public boolean transformClass(@NotNull String className, @NotNull ClassNodeHandle classNode) {
        val methods = classNode.getNode().methods;
        var numHooks = 0;
        for (val method : methods) {
            numHooks += transformMethod(className, method);
        }

        if (numHooks > 0) {
            log.debug("Applied [{}] hooks into class: {}", numHooks, className);
            return true;
        }
        return false;
    }

    private static int transformMethod(String className, MethodNode methodNode) {
        var numHooks = 0;

        val instList = methodNode.instructions;
        var instNode = instList.getFirst();
        while (instNode != null) {
            if (instNode instanceof MethodInsnNode methodInsnNode) {
                if (transformMethodUse(className, methodNode, methodInsnNode)) {
                    numHooks++;
                }
            }
            instNode = instNode.getNext();
        }

        if (numHooks > 0) {
            log.debug("Applied [{}] hooks into method: {}#{}{}", numHooks, className, methodNode.name, methodNode.desc);
        }
        return numHooks;
    }

    private static boolean transformMethodUse(String className, MethodNode methodNode, MethodInsnNode methodInsnNode) {
        if (!isGlClass(methodInsnNode.owner)) {
            return false;
        }
        for (val mapping : MAPPINGS) {
            if (mapping.tryApply(className, methodNode, methodInsnNode, HOOK_OWNER)) {
                return true;
            }
        }
        return false;
    }

    private static boolean isGlClass(String className) {
        return className.startsWith("org/lwjgl/opengl/") || className.startsWith("org/lwjglx/opengl/");
    }

    private record MethodReference(String name, String desc) {
        private boolean matches(MethodInsnNode node) {
            return name.equals(node.name) && desc.equals(node.desc);
        }

        private void apply(MethodInsnNode node) {
            node.name = name;
            node.desc = desc;
        }
    }

    private record MethodMapping(MethodReference src, MethodReference dst) {
        private boolean tryApply(String className, MethodNode methodNode, MethodInsnNode node, String dstOwner) {
            if (src.matches(node)) {
                dst.apply(node);
                node.owner = dstOwner;
                log.debug("In method [{}#{}{}], remapped method use: [{}{}]->[{}{}]", className, methodNode.name, methodNode.desc, src.name, src.desc, dst.name, dst.desc);
                return true;
            }
            return false;
        }
    }
}
