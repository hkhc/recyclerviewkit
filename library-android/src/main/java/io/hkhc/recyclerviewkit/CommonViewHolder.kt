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

import android.content.Context
import android.view.View
import androidx.annotation.ColorRes
import androidx.recyclerview.widget.RecyclerView

open class CommonViewHolder<T>(
    val view: View,
    val itemParam: RecyclerViewItemParam<T>
) : RecyclerView.ViewHolder(view) {

    private fun configBackground(context: Context, view: View, @ColorRes color: Int) {

        val background = ThemeHelper.themeAttributeToDrawable(
            android.R.attr.selectableItemBackground,
            context,
            ThemeHelper.getColorDrawable(context, color)
        )
        background?.let { view.background = it }
    }

    open fun bind(data: T, position: Int) {
        itemView.setOnClickListener { itemParam.onClick.invoke(it, data, layoutPosition) }
    }
}
