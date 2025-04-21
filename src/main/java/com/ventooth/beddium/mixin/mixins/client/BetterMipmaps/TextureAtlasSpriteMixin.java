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

package com.ventooth.beddium.mixin.mixins.client.BetterMipmaps;

import com.ventooth.beddium.helper.TextureAtlasSpriteHelper;
import com.ventooth.beddium.modules.BetterMipmaps.BetterMipmapsModule;
import lombok.val;
import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

import net.minecraft.client.renderer.texture.TextureAtlasSprite;

import java.util.List;

@Mixin(value = TextureAtlasSprite.class)
public abstract class TextureAtlasSpriteMixin {
    @Shadow
    @Final
    private String iconName;
    @Shadow
    protected List<int[][]> framesTextureData;

    @Inject(method = "generateMipmaps",
            at = @At("HEAD"))
    private void correctAlpha(int level, CallbackInfo ci) {
        if (TextureAtlasSpriteHelper.spriteFrameHasData(this.framesTextureData) && level > 0) {
            val nativeImage = this.framesTextureData.getFirst()[0];
            BetterMipmapsModule.correctAlpha(this.iconName, nativeImage);
        }
    }
}
