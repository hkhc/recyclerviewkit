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
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import io.hkhc.recyclerviewkit.CommonViewHolder
import io.hkhc.recyclerviewkit.ParamViewHolderFactory
import io.hkhc.recyclerviewkit.RecyclerViewItemParam

class SimpleViewHolderFactory<T>(
    val creator: (ViewGroup, View?, RecyclerViewItemParam<T>) -> CommonViewHolder<T>
) : ParamViewHolderFactory<T>() {

    override fun create(parent: ViewGroup, view: View?): CommonViewHolder<T> {
        // assert itemParam has been inited
        return creator.invoke(parent, view, itemParam)
    }

    override fun bind(viewHolder: RecyclerView.ViewHolder, item: T, pos: Int, payload: List<Any>?) {
        super.bind(viewHolder, item, pos, payload)

        // assert itemParam has been inited
        // TODO Try to avoid to use pos and use viewHolder.adapterPosition, let's see if we can remove that param

        @Suppress("UNCHECKED_CAST")
        val commonViewHolder = viewHolder as CommonViewHolder<T>
        commonViewHolder.bind(item, pos)
    }

    override fun onRecycle(viewHolder: RecyclerView.ViewHolder) {
        TODO("Not yet implemented")
    }
}
