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

package com.ventooth.beddium.helper;

import lombok.AccessLevel;
import lombok.NoArgsConstructor;
import org.embeddedt.embeddium.impl.util.collections.quadtree.Rect2i;
import org.jetbrains.annotations.Nullable;

import net.minecraft.client.renderer.texture.TextureAtlasSprite;

import java.util.List;

@NoArgsConstructor(access = AccessLevel.PRIVATE)
public final class TextureAtlasSpriteHelper {
    public static int maxSpriteLength(TextureAtlasSprite sprite) {
        return Math.max(sprite.getIconWidth(), sprite.getIconHeight());
    }

    public static Rect2i spriteBounds(TextureAtlasSprite sprite) {
        return new Rect2i(sprite.getOriginX(), sprite.getOriginY(), sprite.getIconWidth(), sprite.getIconHeight());
    }

    public static boolean spriteFrameHasData(@Nullable List<int[][]> framesTextureData) {
        return framesTextureData != null && !framesTextureData.isEmpty() && framesTextureData.getFirst() != null;
    }
}
