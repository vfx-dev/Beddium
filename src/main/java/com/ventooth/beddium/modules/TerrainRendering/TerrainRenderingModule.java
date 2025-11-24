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

package com.ventooth.beddium.modules.TerrainRendering;

import com.ventooth.beddium.Share;
import com.ventooth.beddium.config.ModuleConfig;
import com.ventooth.beddium.config.TerrainRenderingConfig;
import com.ventooth.beddium.modules.TerrainRendering.command.ToggleMapCommand;
import com.ventooth.beddium.modules.TerrainRendering.command.TogglePassCommand;
import com.ventooth.beddium.modules.TerrainRendering.command.ToggleWireframeCommand;
import com.ventooth.beddium.modules.TerrainRendering.ext.RenderGlobalExt;
import it.unimi.dsi.fastutil.objects.ObjectArraySet;
import it.unimi.dsi.fastutil.objects.ObjectSet;
import lombok.AccessLevel;
import lombok.NoArgsConstructor;
import lombok.val;
import org.embeddedt.embeddium.api.util.ColorABGR;
import org.embeddedt.embeddium.impl.gl.device.GLRenderDevice;
import org.embeddedt.embeddium.impl.gl.device.RenderDevice;
import org.embeddedt.embeddium.impl.render.chunk.sprite.SpriteTransparencyLevel;
import org.lwjgl.opengl.GL11;
import org.lwjgl.opengl.GL15C;

import net.minecraft.client.Minecraft;
import net.minecraft.launchwrapper.Launch;
import net.minecraftforge.client.ClientCommandHandler;
import net.minecraftforge.client.event.TextureStitchEvent;
import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.event.world.WorldEvent;
import cpw.mods.fml.common.eventhandler.SubscribeEvent;

import java.util.Arrays;
import java.util.concurrent.atomic.AtomicInteger;

@NoArgsConstructor(access = AccessLevel.PRIVATE)
public final class TerrainRenderingModule {
    private static final AtomicInteger chunkUpdateCounter = new AtomicInteger(0);
    private static final ObjectSet<String> alwaysTranslucentSprites = new ObjectArraySet<>();

    public static boolean DEBUG_WIREFRAME_MODE = false;

    public static void init() {
        GLRenderDevice.VANILLA_STATE_RESETTER = () -> {
            GL15C.glBindBuffer(GL15C.GL_ARRAY_BUFFER, 0);
        };

        if (ModuleConfig.Debug || (Boolean) Launch.blackboard.get("fml.deobfuscatedEnvironment")) {
            ClientCommandHandler.instance.registerCommand(new TogglePassCommand());
            ClientCommandHandler.instance.registerCommand(new ToggleWireframeCommand());
            ClientCommandHandler.instance.registerCommand(new ToggleMapCommand());
        }

        MinecraftForge.EVENT_BUS.register(new TerrainRenderingModule());
    }

    /**
     * Required to handle de-init on world leave, in particular to ensure thread shutdown.
     */
    @SubscribeEvent
    public void onWorldUnload(WorldEvent.Unload event) {
        if (Minecraft.getMinecraft().theWorld != event.world) {
            return;
        }

        val rg = Minecraft.getMinecraft().renderGlobal;
        if (rg instanceof RenderGlobalExt rgExt) {
            val renderer = rgExt.celeritas$worldRenderer();
            if (renderer != null) {
                RenderDevice.enterManagedCode();
                try {
                    renderer.setWorld(null);
                } finally {
                    RenderDevice.exitManagedCode();
                }
            }
        }
    }

    /**
     * @implNote Fires before the terrain atlas, where it actually makes a difference.
     */
    @SubscribeEvent
    public void refreshAlwaysTranslucentSprites(TextureStitchEvent.Pre event) {
        if (event.map.getTextureType() == 0) {
            val list = Arrays.asList(TerrainRenderingConfig.AlwaysTranslucentSprites);

            alwaysTranslucentSprites.clear();
            alwaysTranslucentSprites.addAll(list);

            list.forEach(name -> Share.log.debug("Sprite: [{}] marked as always translucent", name));
        }
    }

    public static void incrementChunkUpdateCounter() {
        chunkUpdateCounter.getAndIncrement();
    }

    public static int readChunkUpdateCounter() {
        return chunkUpdateCounter.getAndSet(0);
    }

    public static void toggleWireframe(boolean enable) {
        if (enable) {
            GL11.glPolygonMode(GL11.GL_FRONT, GL11.GL_LINE);
            GL11.glPolygonMode(GL11.GL_BACK, GL11.GL_LINE);
        } else {
            GL11.glPolygonMode(GL11.GL_FRONT, GL11.GL_FILL);
            GL11.glPolygonMode(GL11.GL_BACK, GL11.GL_FILL);
        }
    }

    public static SpriteTransparencyLevel getSpriteTranslucencyLevel(String name, int[] nativeImage) {
        if (alwaysTranslucentSprites.contains(name)) {
            return SpriteTransparencyLevel.TRANSLUCENT;
        }

        // Decide the transparency level, defaulting to opaque
        var level = SpriteTransparencyLevel.OPAQUE;
        for (var y = 0; y < nativeImage.length; y++) {
            val color = nativeImage[y];
            val alpha = ColorABGR.unpackAlpha(color);
            // Ignore all fully-transparent pixels for the purposes of computing an ave
            if (alpha > 0) {
                if (alpha < 255) {
                    level = level.chooseNextLevel(SpriteTransparencyLevel.TRANSLUCENT);
                } else {
                    level = level.chooseNextLevel(SpriteTransparencyLevel.OPAQUE);
                }
            } else {
                level = level.chooseNextLevel(SpriteTransparencyLevel.TRANSPARENT);
            }
        }
        return level;
    }

    // TODO: Move stuff from RenderGlobalMixin? Or unify into CeleritasWorldRenderer?
}
