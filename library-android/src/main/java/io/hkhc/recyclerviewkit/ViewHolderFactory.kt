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

import android.view.View
import android.view.ViewGroup
import androidx.annotation.LayoutRes
import androidx.recyclerview.widget.RecyclerView

interface ViewHolderFactory<T> {

    /**
     * create a ViewHolder associated with a view as a child of "parent" paremeter.
     * If layoutResId() provided a layout resource ID, it will be inflated implicitly and
     * pass the view to the "view" parameter. Otherwise, "view" will be null
     */
    fun create(parent: ViewGroup, view: View?): RecyclerView.ViewHolder

    /**
     * Determine if this factory can handle creation of view holder for a row based on the data
     * provided.
     */
    fun canHandle(item: T): Boolean
    fun bind(viewHolder: RecyclerView.ViewHolder, item: T, pos: Int, payload: List<Any>? = null)
    fun onRecycle(viewHolder: RecyclerView.ViewHolder)
    @LayoutRes fun layoutResId(): Int
}
