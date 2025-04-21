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

package com.ventooth.beddium.mixin.mixins.client.ConservativeAnimatedTextures;

import com.google.common.collect.Iterators;
import com.llamalad7.mixinextras.injector.ModifyExpressionValue;
import com.ventooth.beddium.modules.ConservativeAnimatedTextures.ext.TextureAtlasSpriteExt;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;

import net.minecraft.client.renderer.texture.TextureAtlasSprite;
import net.minecraft.client.renderer.texture.TextureMap;

import java.util.Iterator;

@Mixin(value = TextureMap.class, priority = 1010)
public abstract class TextureAtlasMixin {
    @ModifyExpressionValue(method = "updateAnimations",
                           at = @At(value = "INVOKE",
                                    target = "Ljava/util/List;iterator()Ljava/util/Iterator;"))
    private Iterator<TextureAtlasSprite> getFilteredIterator(Iterator<TextureAtlasSprite> it) {
        return Iterators.filter(it, s -> TextureAtlasSpriteExt.of(s).celeritas$shouldUpdate());
    }
}
