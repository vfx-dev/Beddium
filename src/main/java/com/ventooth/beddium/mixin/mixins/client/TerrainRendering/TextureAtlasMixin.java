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

package com.ventooth.beddium.mixin.mixins.client.TerrainRendering;

import com.llamalad7.mixinextras.sugar.Local;
import com.ventooth.beddium.helper.TextureAtlasSpriteHelper;
import com.ventooth.beddium.modules.TerrainRendering.ext.TextureMapExt;
import lombok.val;
import org.embeddedt.embeddium.impl.util.collections.quadtree.QuadTree;
import org.embeddedt.embeddium.impl.util.collections.quadtree.Rect2i;
import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

import net.minecraft.client.renderer.texture.Stitcher;
import net.minecraft.client.renderer.texture.TextureAtlasSprite;
import net.minecraft.client.renderer.texture.TextureMap;
import net.minecraftforge.client.event.TextureStitchEvent;

import java.util.Map;

@Mixin(TextureMap.class)
public abstract class TextureAtlasMixin implements TextureMapExt {
    @Shadow
    @Final
    private Map<String, TextureAtlasSprite> mapUploadedSprites;

    @Unique
    private QuadTree<TextureAtlasSprite> celeritas$quadTree;
    @Unique
    private int celeritas$width;
    @Unique
    private int celeritas$height;

    /**
     * @implNote Can't be replaced with a {@link TextureStitchEvent.Post} event hook because we need a reference to the {@link Stitcher}.
     */
    @Inject(method = "loadTextureAtlas",
            at = @At("RETURN"))
    private void generateQuadTree(CallbackInfo ci, @Local(ordinal = 0) Stitcher stitcher) {
        celeritas$width = stitcher.getCurrentWidth();
        celeritas$height = stitcher.getCurrentHeight();
        val treeRect = new Rect2i(0, 0, celeritas$width, celeritas$height);
        val uploaded = this.mapUploadedSprites.values();
        val minSize = uploaded.stream().mapToInt(TextureAtlasSpriteHelper::maxSpriteLength).min().orElse(0);
        celeritas$quadTree = new QuadTree<>(treeRect, minSize, uploaded, TextureAtlasSpriteHelper::spriteBounds);
    }

    @Override
    public TextureAtlasSprite celeritas$findFromUV(float u, float v) {
        val x = Math.round(u * celeritas$width);
        val y = Math.round(v * celeritas$height);
        return celeritas$quadTree.find(x, y);
    }
}
