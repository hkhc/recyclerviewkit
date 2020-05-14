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

import android.R
import android.util.Log
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.paging.PagedListAdapter
import androidx.recyclerview.widget.DiffUtil

class ItemListAdapter : PagedListAdapter<Item, io.hkhc.recyclerviewkit.demo.ItemViewHolder>(
    REPO_COMPARATOR
) {

    companion object {
        private val REPO_COMPARATOR = object : DiffUtil.ItemCallback<Item>() {
            override fun areItemsTheSame(oldItem: Item, newItem: Item): Boolean =
                oldItem.id == newItem.id

            override fun areContentsTheSame(oldItem: Item, newItem: Item): Boolean =
                oldItem == newItem
        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): io.hkhc.recyclerviewkit.demo.ItemViewHolder {
        val inflater = LayoutInflater.from(parent.context)
        val view = inflater.inflate(android.R.layout.simple_list_item_1, parent, false)
        Log.d("DataSource", "onCreateViewHolder minheight ${view.minimumHeight}")
        return io.hkhc.recyclerviewkit.demo.ItemViewHolder(
            view,
            R.id.text1
        )
    }

    override fun onBindViewHolder(holder: io.hkhc.recyclerviewkit.demo.ItemViewHolder, position: Int) {
        Log.d("DataSource", "PLA onBindViewHolder position $position")
        holder.textView?.text = getItem(position)?.desc
    }
}
