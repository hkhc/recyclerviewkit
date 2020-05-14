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

import android.util.Log
import android.view.LayoutInflater
import android.view.ViewGroup
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import io.hkhc.demo.demo.R

class LoadingListAdapter(val adapter: RecyclerView.Adapter<ItemViewHolder>) {

    fun onBindHeaderViewHolder(holder: RecyclerView.ViewHolder) {
        Log.d("DataSource", "HFLA onBindHeaderViewHolder")
        (holder as ItemViewHolder).textView?.text = holder.textView?.resources?.getText(R.string.is_header)
    }

    fun onBindFooterViewHolder(holder: RecyclerView.ViewHolder) {
        Log.d("DataSource", "HFLA onBindFooterViewHolder")
        (holder as ItemViewHolder).textView?.text = holder.textView?.resources?.getText(R.string.is_footer)
    }

    fun onCreateHeaderViewHolder(parent: ViewGroup): ItemViewHolder {
        val inflater = LayoutInflater.from(parent.context)
        val view = inflater.inflate(android.R.layout.simple_list_item_1, parent, false)
        view.setBackgroundColor(view.resources.getColor(android.R.color.holo_blue_bright))
        Log.d("DataSource", "onCreateHeaderViewHolder minheight ${view.minimumHeight}")
        return ItemViewHolder(view, android.R.id.text1)
    }

    fun onCreateFooterViewHolder(parent: ViewGroup): ItemViewHolder {
        val inflater = LayoutInflater.from(parent.context)
        val view = inflater.inflate(android.R.layout.simple_list_item_1, parent, false)
        view.setBackgroundColor(view.resources.getColor(android.R.color.holo_purple))
        (view as TextView).text = "Placeholder"
        return ItemViewHolder(view, android.R.id.text1)
    }
}
