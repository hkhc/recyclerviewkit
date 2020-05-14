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

import androidx.recyclerview.widget.RecyclerView

abstract class ParamViewHolderFactory<T> : ViewHolderFactory<T>, ParamSink<T> {

    override lateinit var itemParam: RecyclerViewItemParam<T>

    override fun canHandle(item: T) = itemParam.canHandle.invoke(item)
    override fun layoutResId() = itemParam.itemLayout
    override fun bind(viewHolder: RecyclerView.ViewHolder, item: T, pos: Int, payload: List<Any>?) {
        viewHolder.itemView.setOnClickListener {
            itemParam.onClick.invoke(it, item, viewHolder.layoutPosition)
        }
    }
}
