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

package com.ventooth.beddium.api.cache;

import com.ventooth.beddium.api.task.WorldRenderRegion;
import com.ventooth.beddium.config.TerrainRenderingConfig;
import com.ventooth.beddium.modules.BiomeColorCache.BiomeColorCacheImpl;
import com.ventooth.beddium.modules.BiomeColorCache.BiomeColorType;
import com.ventooth.beddium.modules.TerrainRendering.cache.ArrayCache;
import lombok.val;
import org.embeddedt.embeddium.impl.biome.BiomeColorCache;

import net.minecraft.block.Block;
import net.minecraft.world.ChunkCache;
import net.minecraft.world.World;
import net.minecraft.world.biome.BiomeGenBase;

public class SimpleChunkCache extends ChunkCache implements BiomeColorCacheCapable, StateAwareCache {
    private final BiomeColorCacheImpl biomeColorCache;
    private final int posX;
    private final int posY;
    private final int posZ;
    private CachedArrays cache;
    private static final ArrayCache arrayCache = new ArrayCache(16);
    private static final int EDGE_SIZE = ((1 << TerrainRenderingConfig.MEGAChunks) * 16 + 4);
    private static final int ARRAY_SIZE = EDGE_SIZE * EDGE_SIZE * EDGE_SIZE;

    public SimpleChunkCache(World world, WorldRenderRegion region) {
        this(world, region.minX, region.minY, region.minZ, region.maxX, region.maxY, region.maxZ);
    }

    public SimpleChunkCache(World world, int minX, int minY, int minZ, int maxX, int maxY, int maxZ) {
        super(world, minX, minY, minZ, maxX, maxY, maxZ, 1);
        this.posX = minX - 1;
        this.posY = minY - 1;
        this.posZ = minZ - 1;
        biomeColorCache = new BiomeColorCacheImpl(this);
    }

    @Override
    public int getLightBrightnessForSkyBlocks(int x, int y, int z, int lightValue) {
        if (cache != null) {
            int index = this.getPositionIndex(x, y, z);
            if (index >= 0 && index < cache.capacity) {
                int light = cache.getLight(index);
                if (light == -1) {
                    light = getLightBrightnessForSkyBlocksUncached(x, y, z, lightValue);
                    cache.putLight(index, light);
                }

                return light;
            }
        }
        return getLightBrightnessForSkyBlocksUncached(x, y, z, lightValue);
    }

    protected int getLightBrightnessForSkyBlocksUncached(int x, int y, int z, int lightValue) {
        return super.getLightBrightnessForSkyBlocks(x, y, z, lightValue);
    }

    @Override
    public Block getBlock(int x, int y, int z) {
        if (cache != null) {
            val blocks = cache.block;
            int index = this.getPositionIndex(x, y, z);
            if (index >= 0 && index < blocks.length) {
                Block block = blocks[index];
                if (block == null) {
                    block = getBlockUncached(x, y, z);
                    blocks[index] = block;
                }

                return block;
            }
        }
        return getBlockUncached(x, y, z);
    }

    protected Block getBlockUncached(int x, int y, int z) {
        return super.getBlock(x, y, z);
    }

    @Override
    public int getBlockMetadata(int x, int y, int z) {
        if (cache != null) {
            val metas = cache.meta;
            int index = this.getPositionIndex(x, y, z);
            if (index >= 0 && index < metas.length) {
                int meta = metas[index];
                if (meta == -1) {
                    meta = getBlockMetadataUncached(x, y, z);
                    metas[index] = meta;
                }

                return meta;
            }
        }
        return getBlockMetadataUncached(x, y, z);
    }

    protected int getBlockMetadataUncached(int x, int y, int z) {
        return super.getBlockMetadata(x, y, z);
    }

    @Override
    public boolean isAirBlock(int x, int y, int z) {
        if (cache != null) {
            val airs = cache.air;
            int index = this.getPositionIndex(x, y, z);
            if (index >= 0 && index < airs.length) {
                byte air = airs[index];
                if (air == 0) {
                    air = isAirBlockUncached(x, y, z) ? (byte) 2 : 1;
                    airs[index] = air;
                }
                return air == 2;
            }
        }
        return isAirBlockUncached(x, y, z);
    }

    protected boolean isAirBlockUncached(int x, int y, int z) {
        return super.isAirBlock(x, y, z);
    }

    private int getPositionIndex(int x, int y, int z) {
        int i = x - this.posX;
        int j = y - this.posY;
        int k = z - this.posZ;
        return i >= 0 && j >= 0 && k >= 0 && i < EDGE_SIZE && j < EDGE_SIZE && k < EDGE_SIZE ? i * (EDGE_SIZE * EDGE_SIZE) + k * EDGE_SIZE + j : -1;
    }

    @Override
    public void renderStart() {
        if (cache == null) {
            cache = arrayCache.allocate(ARRAY_SIZE);
        } else {
            cache.reset();
        }
    }

    @Override
    public void renderFinish() {
        arrayCache.free(cache);
    }

    @Override
    public BiomeColorCache<BiomeGenBase, BiomeColorType> celeritas$biomeCache() {
        return biomeColorCache;
    }
}
