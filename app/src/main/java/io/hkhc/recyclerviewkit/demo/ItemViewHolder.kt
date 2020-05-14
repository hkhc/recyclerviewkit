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

package io.hkhc.recyclerviewkit.demo

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import io.hkhc.recyclerviewkit.ViewHolderFactory

class ItemViewHolder(view: View, resId: Int = android.R.id.text1) :
    RecyclerView.ViewHolder(view) {

    class Factory : ViewHolderFactory<Any> {
        override fun create(parent: ViewGroup, view: View?): RecyclerView.ViewHolder {
            val rootView = LayoutInflater.from(parent.context)
                .inflate(android.R.layout.simple_list_item_1, parent, false)
            return ItemViewHolder(rootView)
        }

        override fun canHandle(item: Any) = true
        override fun layoutResId() = 0

        override fun bind(viewHolder: RecyclerView.ViewHolder, item: Any, pos: Int, payload: List<Any>?) {
            // do nothing
        }

        override fun onRecycle(viewHolder: RecyclerView.ViewHolder) {
            TODO("Not yet implemented")
        }
    }

    var textView: TextView? = null

    init {
        if (view is TextView) {
            textView = view
        } else {
            textView = view.findViewById(resId)
        }
    }
}
