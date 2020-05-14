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

import android.view.View
import androidx.annotation.IdRes
import androidx.annotation.LayoutRes
import io.hkhc.demo.R

class RecyclerViewItemParam<T> {

    var onClick: (View, T, Int) -> Unit = { _, _, _ -> }

    var onToggle: (View, Boolean, Int) -> (Unit) = { _, _, _ -> }

    var canHandle: (T) -> Boolean = { true }

    @LayoutRes var itemLayout: Int = android.R.layout.simple_list_item_1

    @IdRes var itemView1: Int = android.R.id.text1

    @IdRes var itemView2: Int = android.R.id.text2

    @IdRes var itemCheckable: Int = R.id.radio

    var mapper1: (T) -> (CharSequence) = { it.toString() }

    var mapper2: (T) -> (CharSequence) = { it.toString() }

    var mapperCheckable: (T) -> (Boolean) = { it.toString().isEmpty() }

    fun copy(): RecyclerViewItemParam<T> {
        return RecyclerViewItemParam<T>().also {
            it.onClick = onClick
            it.onToggle = onToggle
            it.itemLayout = itemLayout
            it.itemView1 = itemView1
            it.itemView2 = itemView2
            it.itemCheckable = itemCheckable
            it.mapper1 = mapper1
            it.mapper2 = mapper2
            it.mapperCheckable = mapperCheckable
        }
    }
}
