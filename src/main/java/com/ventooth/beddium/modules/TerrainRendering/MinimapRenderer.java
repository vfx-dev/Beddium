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

import com.falsepattern.lib.compat.ChunkPos;
import lombok.val;
import org.lwjgl.opengl.GL11;

import net.minecraft.client.Minecraft;
import net.minecraft.client.multiplayer.ChunkProviderClient;
import net.minecraft.world.chunk.EmptyChunk;
import net.minecraftforge.client.event.RenderGameOverlayEvent;
import net.minecraftforge.common.MinecraftForge;
import cpw.mods.fml.common.eventhandler.SubscribeEvent;

public class MinimapRenderer {
    private static final MinimapRenderer INSTANCE = new MinimapRenderer();
    private static volatile boolean enabled = false;

    public static synchronized void toggle() {
        if (enabled) {
            disable();
        } else {
            enable();
        }
    }

    public static synchronized void enable() {
        if (enabled) {
            return;
        }
        enabled = true;
        MinecraftForge.EVENT_BUS.register(INSTANCE);
    }

    public static synchronized void disable() {
        if (!enabled) {
            return;
        }
        enabled = false;
        MinecraftForge.EVENT_BUS.unregister(INSTANCE);
    }

    @SubscribeEvent
    public void onDraw(RenderGameOverlayEvent.Text event) {
        val renderer = CeleritasWorldRenderer.instanceNullable();
        if (renderer == null) {
            return;
        }
        val manager = renderer.getRenderSectionManager();
        val MINECRAFT = Minecraft.getMinecraft();
        float partialTicks = event.partialTicks;
        double pX = MINECRAFT.thePlayer.prevPosX + (MINECRAFT.thePlayer.posX - MINECRAFT.thePlayer.prevPosX) * partialTicks;
        double pY = MINECRAFT.thePlayer.prevPosY + (MINECRAFT.thePlayer.posY - MINECRAFT.thePlayer.prevPosY) * partialTicks;
        double pZ = MINECRAFT.thePlayer.prevPosZ + (MINECRAFT.thePlayer.posZ - MINECRAFT.thePlayer.prevPosZ) * partialTicks;


        GL11.glPushAttrib(GL11.GL_ALL_ATTRIB_BITS);
        GL11.glPushMatrix();
        GL11.glScaled(2, 2, 1);
        GL11.glTranslated(32, 32, 0);

        GL11.glDisable(GL11.GL_LIGHTING);
        GL11.glDisable(GL11.GL_TEXTURE_2D);
        GL11.glDisable(GL11.GL_DEPTH_TEST);
        GL11.glEnable(GL11.GL_BLEND);
        GL11.glBlendFunc(GL11.GL_SRC_ALPHA, GL11.GL_ONE_MINUS_SRC_ALPHA);
        GL11.glHint(GL11.GL_LINE_SMOOTH_HINT, GL11.GL_NICEST);

        GL11.glBegin(GL11.GL_QUADS);

        int playerChunkX = ((int) Math.floor(pX)) >> 4;
        int playerChunkZ = ((int) Math.floor(pZ)) >> 4;
        int range = 32;
        for (int x = playerChunkX - range; x < playerChunkX + range; x++) {
            for (int z = playerChunkZ - range; z < playerChunkZ + range; z++) {
                drawChunk(new ChunkPos(x, z), playerChunkX, playerChunkZ, manager);
            }
        }

        GL11.glEnd();

        GL11.glEnable(GL11.GL_LIGHTING);
        GL11.glEnable(GL11.GL_DEPTH_TEST);
        GL11.glDisable(GL11.GL_TEXTURE_2D);
        GL11.glPopMatrix();
        GL11.glPopAttrib();
    }

    private void drawChunk(ChunkPos chunk, int pX, int pZ, ArchaicRenderSectionManager manager) {
        val cX = chunk.x;
        val cZ = chunk.z;
        boolean initialized = false;
        double r = 0;
        double g = 0;
        double b = 0;
        for (int i = 0; i < 16; i++) {
            if (manager.isSectionBuilt(cX, i, cZ) && !manager.isSectionVisuallyEmpty(cX, i, cZ)) {
                initialized = true;
                g = 1;
                if (manager.isSectionVisible(cX, i, cZ)) {
                    b = 1;
                    break;
                }
            }
        }
        if (!initialized) {
            val chunkProviderClient = (ChunkProviderClient) Minecraft.getMinecraft().theWorld.getChunkProvider();
            val theChunk = chunkProviderClient.provideChunk(chunk.x, chunk.z);
            if (theChunk instanceof EmptyChunk) {
                r = 1;
            } else {
                b = 1;
                initialized = true;
            }
        }
        if (!initialized && Minecraft.getMinecraft().isIntegratedServerRunning()) {
            val chunkProviderIntegratedServer =
                    Minecraft.getMinecraft().getIntegratedServer().worldServerForDimension(Minecraft.getMinecraft().thePlayer.dimension).getChunkProvider();
            if (chunkProviderIntegratedServer.chunkExists(chunk.x, chunk.z)) {
                val theChunk = chunkProviderIntegratedServer.provideChunk(chunk.x, chunk.z);
                if (!theChunk.isLightPopulated) {
                    b = 1;
                } else {
                    g = 0.5;
                }
            }
        }
        GL11.glColor4d(r, g, b, 0.8);

        int xStart = chunk.x - pX;
        int zStart = chunk.z - pZ;
        int xEnd = xStart + 1;
        int zEnd = zStart + 1;

        GL11.glVertex3d(xStart, zStart, 0);
        GL11.glVertex3d(xStart, zEnd, 0);
        GL11.glVertex3d(xEnd, zEnd, 0);
        GL11.glVertex3d(xEnd, zStart, 0);


    }
}