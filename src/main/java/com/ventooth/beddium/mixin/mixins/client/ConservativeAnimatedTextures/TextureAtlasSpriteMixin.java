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

import com.llamalad7.mixinextras.injector.ModifyReturnValue;
import com.ventooth.beddium.modules.ConservativeAnimatedTextures.ext.TextureAtlasSpriteExt;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;

import net.minecraft.client.renderer.texture.TextureAtlasSprite;

@Mixin(TextureAtlasSprite.class)
public abstract class TextureAtlasSpriteMixin implements TextureAtlasSpriteExt {
    @Unique
    private boolean celeritas$isActive = false;

    @ModifyReturnValue(method = {
            "getMinU",
            "getInterpolatedU"
    },
                       at = @At("RETURN"))
    private float markActiveWhenGettingCoords(float original) {
        celeritas$markActive();
        return original;
    }

    @Override
    public void celeritas$markActive() {
        this.celeritas$isActive = true;
    }

    @Override
    public boolean celeritas$shouldUpdate() {
        if (celeritas$isActive) {
            celeritas$isActive = false;
            return true;
        } else {
            return false;
        }
    }
}
