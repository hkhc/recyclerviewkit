/*
 * Copyright (c) 2020. Herman Cheung
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 * http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 *
 *
 */

package io.hkhc.recyclerviewkit

import android.annotation.TargetApi
import android.content.Context
import android.graphics.drawable.ColorDrawable
import android.graphics.drawable.Drawable
import android.os.Build
import android.util.TypedValue
import androidx.annotation.ColorRes
import androidx.core.content.ContextCompat

object ThemeHelper {

    fun themeAttributeToDrawable(
        themeAttributeId: Int,
        context: Context,
        fallbackDrawable: Drawable?
    ): Drawable? {
        val outValue = TypedValue()
        val theme = context.theme
        val wasResolved = theme.resolveAttribute(themeAttributeId, outValue, true)
        return if (wasResolved) {
            ContextCompat.getDrawable(context, outValue.resourceId)
        } else {
            // fallback colour handling
            fallbackDrawable
        }
    }

    @Suppress("DEPRECATION")
    fun getColorDrawable1(context: Context, @ColorRes color: Int) =
        ColorDrawable(context.resources.getColor(color))

    @TargetApi(Build.VERSION_CODES.M)
    fun getColorDrawable23(context: Context, @ColorRes color: Int) =
        ColorDrawable(context.resources.getColor(color, context.theme))

    fun getColorDrawable(context: Context, @ColorRes color: Int) =
        if (Build.VERSION.SDK_INT < Build.VERSION_CODES.M) {
            getColorDrawable1(context, color)
        } else {
            getColorDrawable23(context, color)
        }
}
