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
import androidx.recyclerview.widget.RecyclerView
import io.hkhc.log.l
import io.hkhc.recyclerviewkit.HasDelegate
import io.hkhc.recyclerviewkit.ListSink
import io.hkhc.recyclerviewkit.ListSource
import io.hkhc.recyclerviewkit.ViewHolderFactory

open class CommonAdapter<T> :
    RecyclerView.Adapter<RecyclerView.ViewHolder>(),
    ListSink<T>,
    HasDelegate {

    val viewHolderCollection = ViewHolderFactoryCollection<T>()
    private var mData: ListSource<T> = ListSource.EmptyListSource()
    override var listData: ListSource<T>
        get() = mData
        set(value) {
            l.debug("set list Count ${mData.size}")
            mData = value
            notifyDataSetChanged()
        }

    fun registerViewHolderFactory(factory: ViewHolderFactory<T>) {
        l.debug("addViewHolderFactory ${factory::class.java.name}")
        viewHolderCollection.register(factory)
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
        return viewHolderCollection.matchViewHolderFactory(mData[position])
    }

    override fun getItemCount(): Int {
        l.debug("getItem Count ${mData.size}")
        return mData.size
    }

    override fun onBindViewHolder(holder: RecyclerView.ViewHolder, position: Int) {

        val factory = viewHolderCollection.findViewHolderFactory(getItemViewType(position))
        factory?.bind(holder, mData[holder.adapterPosition], holder.adapterPosition)
    }

    override fun getDelegated(): RecyclerView.Adapter<RecyclerView.ViewHolder> {
        return this
    }
}
