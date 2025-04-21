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

import com.ventooth.beddium.helper.TextureAtlasSpriteHelper;
import com.ventooth.beddium.modules.TerrainRendering.TerrainRenderingModule;
import com.ventooth.beddium.modules.TerrainRendering.ext.TextureAtlasSpriteExt;
import lombok.val;
import org.embeddedt.embeddium.impl.render.chunk.sprite.SpriteTransparencyLevel;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

import net.minecraft.client.renderer.texture.TextureAtlasSprite;

import java.util.List;

// TODO: Jdoc, priority is because mipmap.TextureAtlasSprite runs next
@Mixin(value = TextureAtlasSprite.class,
       priority = 900)
public abstract class TextureAtlasSpriteMixin implements TextureAtlasSpriteExt {
    @Shadow
    protected List<int[][]> framesTextureData;

    @Unique
    private SpriteTransparencyLevel celeritas$transparencyLevel = SpriteTransparencyLevel.TRANSLUCENT;

    @Inject(method = "generateMipmaps",
            at = @At("HEAD"))
    private void decideTransparencyLevel(int level, CallbackInfo ci) {
        if (TextureAtlasSpriteHelper.spriteFrameHasData(this.framesTextureData)) {
            val nativeImage = this.framesTextureData.getFirst()[0];
            celeritas$transparencyLevel = TerrainRenderingModule.getSpriteTranslucencyLevel(nativeImage);
        }
    }

    @Override
    public SpriteTransparencyLevel celeritas$transparencyLevel() {
        return celeritas$transparencyLevel;
    }
}
