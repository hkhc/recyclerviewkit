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
import androidx.annotation.LayoutRes
import androidx.recyclerview.widget.RecyclerView
import io.hkhc.recyclerviewkit.ViewHolderFactory

class LayoutItem(@LayoutRes private val resId: Int) : ViewHolderFactory<Any> {

    class LayoutViewHolder(view: View) : RecyclerView.ViewHolder(view)

    override fun create(parent: ViewGroup, view: View?): RecyclerView.ViewHolder {
        assert(view != null)
        return LayoutViewHolder(view!!)
    }

    override fun canHandle(item: Any) = true

    override fun bind(viewHolder: RecyclerView.ViewHolder, item: Any, pos: Int, payload: List<Any>?) {
        // do nothing intentionally
    }

    override fun onRecycle(viewHolder: RecyclerView.ViewHolder) {
        // do nothing intentionally
    }

    override fun layoutResId() = resId
}
