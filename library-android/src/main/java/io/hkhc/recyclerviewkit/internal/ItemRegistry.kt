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

import android.os.Handler
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import io.hkhc.log.l
import io.hkhc.recyclerviewkit.ViewHolderFactory
import io.hkhc.recyclerviewkit.headfoot.HeaderFooterListAdapter
import java.util.WeakHashMap

@Suppress("TooManyFunctions")
open abstract class ItemRegistry<WrappedVH : RecyclerView.ViewHolder>(
    private val adapter: HeaderFooterListAdapter<WrappedVH>,
    var reverseOrder: Boolean = false
) {

    companion object {
        private var nextType: Int = 100000
        private var nextId: Long = 100000L
    }

    private val handler = Handler()

    private val holderMap: MutableMap<RecyclerView.ViewHolder, ViewHolderFactory<Any>> = WeakHashMap()

    // hold the sequential position of view holder factory
    private val factories = mutableListOf<ViewHolderFactory<Any>>()
    // retrieve view holder factory by id
    private val idMap = mutableMapOf<Long, ViewHolderFactory<Any>>()
    // retrieve view holder factory by type
    private val typeMap = mutableMapOf<Int, ViewHolderFactory<Any>>()

    fun register(factory: ViewHolderFactory<Any>) {
        // reuse the same factory type
        factories.add(factory)
        idMap[nextId] = factory
        typeMap[nextType] = factory
        nextId++
        nextType++
    }

    fun unregister(pos: Int) {
        if (!inRange(pos)) return
        factories.removeAt(listPosToGroupPos(pos))
        idMap.remove(getIdByPosition(pos))
        typeMap.remove(getTypeByPosition(pos))
    }

    val factoryCount: Int
        get() = factories.size

    val adapterCount: Int
        get() = adapter.itemCount

    private var _visible: Boolean = false
    var isVisible: Boolean
        get() = _visible
        set(value) {
            // show header
            if (value != _visible) {
                // TODO avoid duplicated post
                if (value) {
                    handler.post {
                        val firstItemPosToBeVisibled = getFirstItemPos(false)
                        _visible = value
                        adapter.notifyItemRangeInserted(firstItemPosToBeVisibled, factoryCount)
                    }
                } else {
                    handler.post {
                        val firstItemPosToBeInvisibled = getFirstItemPos(true)
                        _visible = value
                        adapter.notifyItemRangeRemoved(firstItemPosToBeInvisibled, factoryCount)
                    }
                }
            }
        }


    fun isValidType(type: Int): Boolean {
        val result = typeMap.keys.contains(type)
        l.debug("isValidType $type $result")
        return result
    }

    fun getFactoryByType(type: Int): ViewHolderFactory<Any>? {
        return typeMap[type]
    }

    fun createViewHolder(type: Int, parent: ViewGroup, view: View?): RecyclerView.ViewHolder? {

        val factory = getFactoryByType(type)

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
            val viewHolder = it.create(parent, view)
            if (viewHolder != null) {
                holderMap[viewHolder] = factory
            }
            return viewHolder
        } ?: throw IllegalStateException("no ViewHolderFactory is set")
    }

    fun isAlive(viewholder: RecyclerView.ViewHolder): Boolean {
        return holderMap[viewholder] != null
    }

    fun recycleViewHolder(viewHolder: RecyclerView.ViewHolder) {

        holderMap[viewHolder]?.let {
            it.onRecycle(viewHolder)
        }

        holderMap.remove(viewHolder)
    }

    fun onRecycleViewHolder(holder: RecyclerView.ViewHolder) {
        TODO("To be implemented")
    }

    /**
     * we may assumed that pos must be in range in implementation, as the base class will always
     * invoke inRange before getFactoryByAdapterPosition
     */
    private fun getFactoryByAdapterPosition(pos: Int): ViewHolderFactory<Any>? {
        return factories[listPosToGroupPos(pos)]
    }

    fun bind(viewHolder: RecyclerView.ViewHolder, pos: Int, payloads: List<Any>? = null) {
        if (!inRange(pos)) {
            throw IllegalStateException("Invalid position (${pos})")
        }
        getFactoryByAdapterPosition(pos)
            ?.bind(viewHolder, Any(), pos, payloads)
    }

    // return type or -1 if it is not header
    // position is the position of the whole list
    fun getTypeByPosition(pos: Int): Int {
        return if (!inRange(pos)) {
            throw IllegalStateException("invalid position ($pos)")
        } else {
            val factory = getFactoryByAdapterPosition(pos)
            try {
                typeMap.entries.first { it.value == factory }.key
            } catch (e: NoSuchElementException) {
                throw IllegalStateException("invalid position ($pos)")
            }
        }
    }

    // return id or -1 if it is not header
    fun getIdByPosition(pos: Int): Long {
        return if (!inRange(pos)) {
            throw IllegalStateException("invalid position ($pos)")
        } else {
            val factory = getFactoryByAdapterPosition(pos)
            try {
                idMap.entries.first { it.value == factory }.key
            } catch (e: NoSuchElementException) {
                throw IllegalStateException("invalid position ($pos)")
            }
        }
    }

    fun inRangeAndVisible(pos: Int) = isVisible && inRange(pos)

    /**
     * When visible is true, the function shall assume that the group is visible and return the
     * position of first item in this group
     * When visible is false, the function shall assume the group is invisible and return the
     * position of first item expected to be shown
     */
    abstract fun getFirstItemPos(visible: Boolean): Int
    fun inRange(pos: Int) = pos in getFirstItemPos(true) until getFirstItemPos(true)+factoryCount

    /**
     * we may assumed that pos must be in range in implementation, as the base class will always
     * invoke inRange before listPostToGroupPos
     */
    fun listPosToGroupPos(pos: Int): Int {
        return if (reverseOrder) {
            (getFirstItemPos(true) + factoryCount - 1) - pos
        } else {
            pos - getFirstItemPos(true)
        }
    }
}
