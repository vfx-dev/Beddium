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

package com.ventooth.beddium.modules.TerrainRendering.compile.task;

import com.ventooth.beddium.api.cache.SimpleChunkCache;
import com.ventooth.beddium.api.task.SimpleChunkBuilderMeshingTask;
import com.ventooth.beddium.api.task.WorldRenderRegion;
import org.embeddedt.embeddium.impl.render.chunk.RenderSection;
import org.joml.Vector3d;

import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.Tessellator;
import net.minecraft.world.ChunkCache;

public class DefaultChunkBuilderMeshingTask extends SimpleChunkBuilderMeshingTask {
    public DefaultChunkBuilderMeshingTask(RenderSection render, int time, Vector3d camera) {
        super(render, WorldRenderRegion.forChunkAt(render), time, camera);
    }

    @Override
    protected ChunkCache createChunkCache(WorldRenderRegion region) {
        return new SimpleChunkCache(Minecraft.getMinecraft().theWorld, region);
    }

    @Override
    protected Tessellator getTessellator() {
        return Tessellator.instance;
    }

    @Override
    protected void setRenderPass(int pass) {
        SimpleChunkBuilderMeshingTask.setForgeRenderPass(pass);
    }
}
