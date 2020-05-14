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
import android.widget.Checkable
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import io.hkhc.recyclerviewkit.ParamViewHolderFactory
import io.hkhc.recyclerviewkit.RecyclerViewItemParam
import io.hkhc.recyclerviewkit.getView
import io.hkhc.recyclerviewkit.setCheckable
import io.hkhc.recyclerviewkit.setTextViewText

class TwoLineOneChoiceItem<T> : ParamViewHolderFactory<T>() {

    class SimpleViewHolder<T>(view: View, itemParam: RecyclerViewItemParam<T>) :
        RecyclerView.ViewHolder(view) {
        var valueView1: TextView? = getView(view!!, itemParam.itemView1, TextView::class.java)
        var valueView2: TextView? = getView(view!!, itemParam.itemView2, TextView::class.java)
        val checkableValue: Checkable? = getView(view!!, itemParam.itemCheckable, Checkable::class.java)
    }

    override fun create(parent: ViewGroup, view: View?): RecyclerView.ViewHolder {
        return SimpleViewHolder<T>(view!!, itemParam)
    }

    override fun bind(viewHolder: RecyclerView.ViewHolder, item: T, pos: Int, payload: List<Any>?) {
        super.bind(viewHolder, item, pos, payload)
        @Suppress("UNCHECKED_CAST")
        (viewHolder as SimpleViewHolder<T>).let {
            it.valueView1?.let { tv ->
                setTextViewText(tv, itemParam.mapper1.invoke(item))
            }
            it.valueView2?.let { tv ->
                setTextViewText(tv, itemParam.mapper2.invoke(item))
            }
            it.checkableValue?.let { tv ->
                setCheckable(tv, itemParam.mapperCheckable.invoke(item))
            }
        }
    }

    override fun onRecycle(viewHolder: RecyclerView.ViewHolder) {
        // do nothing intentionally
    }
}
