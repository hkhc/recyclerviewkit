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

import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import io.hkhc.recyclerviewkit.ViewHolderFactory

class HeaderViewHolderFactory(val content: String) : ViewHolderFactory<Any> {

    class HeadViewHolder(view: View) : RecyclerView.ViewHolder(view) {
        val textView: TextView = itemView.findViewById(android.R.id.text1)
    }

    override fun create(parent: ViewGroup, view: View?) =
        HeadViewHolder(view!!)
    override fun canHandle(item: Any) = true
    override fun layoutResId() = android.R.layout.simple_list_item_1

    override fun bind(viewHolder: RecyclerView.ViewHolder, item: Any, pos: Int, payload: List<Any>?) {
        if (viewHolder is HeadViewHolder) {
            viewHolder.textView.text = content
        }
    }

    override fun onRecycle(viewHolder: RecyclerView.ViewHolder) {
        TODO("Not yet implemented")
    }
}
