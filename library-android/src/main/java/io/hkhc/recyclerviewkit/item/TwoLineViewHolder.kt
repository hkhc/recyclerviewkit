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

package io.hkhc.recyclerviewkit.item

import android.view.View
import android.widget.TextView
import io.hkhc.recyclerviewkit.CommonViewHolder
import io.hkhc.recyclerviewkit.DefaultLayout
import io.hkhc.recyclerviewkit.RecyclerViewItemParam
import io.hkhc.recyclerviewkit.getView
import io.hkhc.recyclerviewkit.setTextViewText

@DefaultLayout(android.R.layout.simple_list_item_2)
class TwoLineViewHolder<T>(
    v: View,
    p: RecyclerViewItemParam<T>
) : CommonViewHolder<T>(v, p) {

    var valueView1: TextView? = getView(v, itemParam.itemView1, TextView::class.java)
    var valueView2: TextView? = getView(v, itemParam.itemView2, TextView::class.java)

    override fun bind(data: T, position: Int) {
        valueView1?.let { tv ->
            setTextViewText(tv, itemParam.mapper1.invoke(data))
        }
        valueView2?.let { tv ->
            setTextViewText(tv, itemParam.mapper2.invoke(data))
        }
        itemView.setOnClickListener { itemParam.onClick.invoke(it, data, position) }
    }
}
