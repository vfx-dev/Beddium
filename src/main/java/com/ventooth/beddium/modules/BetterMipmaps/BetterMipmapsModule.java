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

package com.ventooth.beddium.modules.BetterMipmaps;

import lombok.AccessLevel;
import lombok.NoArgsConstructor;
import lombok.val;
import org.embeddedt.embeddium.api.util.ColorABGR;
import org.embeddedt.embeddium.impl.texture.MipmapHelper;
import org.embeddedt.embeddium.impl.util.color.ColorSRGB;

@NoArgsConstructor(access = AccessLevel.PRIVATE)
public final class BetterMipmapsModule {
    public static void correctAlpha(String iconName, int[] nativeImage) {
        // TODO: Document why we avoid leaves?
        if (iconName.contains("leaves")) {
            return;
        }

        var r = 0F;
        var g = 0F;
        var b = 0F;
        var totalWeight = 0.0f;

        // Calculate an average color from all pixels that are not completely transparent.
        // This average is weighted based on the (non-zero) alpha value of the pixel.
        for (var y = 0; y < nativeImage.length; y++) {
            val color = nativeImage[y];
            val alpha = ColorABGR.unpackAlpha(color);

            // Ignore all fully-transparent pixels for the purposes of computing an average color.
            if (alpha > 0 && alpha < 255) {
                val weight = (float) alpha;

                // Make sure to convert to linear space so that we don't lose brightness.
                r += ColorSRGB.srgbToLinear(ColorABGR.unpackRed(color)) * weight;
                g += ColorSRGB.srgbToLinear(ColorABGR.unpackGreen(color)) * weight;
                b += ColorSRGB.srgbToLinear(ColorABGR.unpackBlue(color)) * weight;

                totalWeight += weight;
            }
        }

        // Bail if none of the pixels are semi-transparent
        if (totalWeight == 0F) {
            return;
        }

        r /= totalWeight;
        g /= totalWeight;
        b /= totalWeight;

        // Convert that color in linear space back to sRGB.
        // Use an alpha value of zero - this works since we only replace pixels with an alpha value of 0.
        val avgColor = ColorSRGB.linearToSrgb(r, g, b, 0);

        for (var y = 0; y < nativeImage.length; y++) {
            val color = nativeImage[y];
            val alpha = ColorABGR.unpackAlpha(color);
            // Replace the color values of pixels which are fully transparent, since they have no color data.
            if (alpha == 0) {
                nativeImage[y] = avgColor;
            }
        }
    }

    public static int weightedAverageColor(int v1, int v2, int v3, int v4) {
        // First blend horizontally, then blend vertically.
        //
        // This works well for the case where our change is the most impactful (grass side overlays)
        val v1_v2 = MipmapHelper.weightedAverageColor(v1, v2);
        val v3_v4 = MipmapHelper.weightedAverageColor(v3, v4);
        return MipmapHelper.weightedAverageColor(v1_v2, v3_v4);
    }
}
