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

package io.hkhc.recyclerviewkit.internal

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.paging.PagedListAdapter
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.RecyclerView
import io.hkhc.log.l
import io.hkhc.recyclerviewkit.HasDelegate
import io.hkhc.recyclerviewkit.RecyclerViewBuilder
import io.hkhc.recyclerviewkit.ViewHolderFactory


class CommonPagedListAdapter<T>(diffUtil: DiffUtil.ItemCallback<T>) :
    PagedListAdapter<T, RecyclerView.ViewHolder>(diffUtil),
    ViewHolderConsumer<T>,
    HasDelegate {

    private val viewHolderCollection = ViewHolderFactoryCollection<T>()

    override fun registerViewHolderFactory(factory: ViewHolderFactory<T>) {
        l.debug("registerViewHolderFactory ${factory::class.java.name}")
        viewHolderCollection.register(factory)
    }

    override fun unregisterViewHolderFactory(factory: ViewHolderFactory<T>) {
        l.debug("unregisterViewHolderFactory ${factory::class.java.name}")
        viewHolderCollection.unregister(factory)
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): RecyclerView.ViewHolder {

        val factory = viewHolderCollection.findViewHolderFactory(viewType)

        val view = if (factory != null) {
            if (factory.layoutResId() != 0) {
                LayoutInflater.from(parent.context).inflate(factory.layoutResId(), parent, false)
            } else {
                null
            }
        } else {
            null
        }

        factory?.let {
            return it.create(parent, view)
        } ?: throw IllegalStateException("no ViewHolderFactory is set")
    }

    override fun getItemViewType(position: Int): Int {
        return viewHolderCollection.matchViewHolderFactory(getItem(position)!!)
    }

    override fun onBindViewHolder(holder: RecyclerView.ViewHolder, position: Int) {
        val factory = viewHolderCollection.findViewHolderFactory(getItemViewType(position))
        factory?.bind(holder, getItem(holder.adapterPosition)!!, holder.adapterPosition)
    }

    override fun getDelegated(): RecyclerView.Adapter<RecyclerView.ViewHolder> {
        return this
    }

}

fun <T> RecyclerViewBuilder<T>.pagedAdapter(diffUtil: DiffUtil.ItemCallback<T>) {
    adapterFactory { CommonPagedListAdapter(diffUtil) }
}