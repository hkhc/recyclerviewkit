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

package io.hkhc.recyclerviewkit.headfoot

import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import io.hkhc.log.l
import io.hkhc.recyclerviewkit.HasDelegate
import io.hkhc.recyclerviewkit.ViewHolderFactory
import io.hkhc.recyclerviewkit.internal.ItemRegistry

@Suppress("TooManyFunctions")
class HeaderFooterListAdapter<WrappedVH : RecyclerView.ViewHolder>(
    @Suppress("unused") val delegateAdapter: RecyclerView.Adapter<WrappedVH>
) : RecyclerView.Adapter<RecyclerView.ViewHolder>(), HasDelegate {

    /*
                                  +----------------+
                                + | Header 1       | +
                                | | Header 2       | | Header factory count
                                | | Header 3       | +
                                | +----------------+
                                | |                |
                                | |                |
                                | |                |
                                | |                |
                  adapter count | |                |
                                | |                |
                              + | |                | <--+ pos
                              | | |                |
                              | | |                |
      adapter count - 1 - pos | | +----------------+
                              | | | Footer 3       | +
                              | | | Footer 2       | | Footer factory count
                              + + | Footer 1       | +
                                  +----------------+
    */

    class HeaderItemRegistry<WrappedVH : RecyclerView.ViewHolder>(
        adapter: HeaderFooterListAdapter<WrappedVH>
    ) : ItemRegistry<WrappedVH>(adapter, false) {
        override fun getFirstItemPos(visible: Boolean) = 0
    }

    class FooterItemRegistry<WrappedVH : RecyclerView.ViewHolder>(
        adapter: HeaderFooterListAdapter<WrappedVH>
    ) : ItemRegistry<WrappedVH>(adapter, true) {
        override fun getFirstItemPos(visible: Boolean): Int {
            return if (visible) {
                adapterCount - factoryCount
            } else {
                adapterCount
            }
        }
    }

    private val headerItemRegistry = HeaderItemRegistry(this)
    private val footerItemRegistry = FooterItemRegistry(this)

    var footerReverseOrder: Boolean
        get() = footerItemRegistry.reverseOrder
        set(value) {
            with(footerItemRegistry) {
                reverseOrder = value
                if (isVisible) {
                    notifyItemRangeChanged(getFirstItemPos(true), factoryCount)
                }
            }
        }

    @Suppress("unused")
    fun registerHeader(factory: ViewHolderFactory<Any>) {
        headerItemRegistry.register(factory)
    }

    @Suppress("unused")
    fun registerFooter(factory: ViewHolderFactory<Any>) {
        footerItemRegistry.register(factory)
    }

    // pos is relative to the group of header
    @Suppress("unused")
    fun unregisterHeader(pos: Int) {
        headerItemRegistry.unregister(pos)
    }

    // pos is relative to the group of footer
    @Suppress("unused")
    fun unregisterFooter(pos: Int) {
        footerItemRegistry.unregister(pos)
    }

    private var _hasHeader: Boolean = false
    var hasHeader: Boolean
        get() = headerItemRegistry.isVisible
        set(value) {
            headerItemRegistry.isVisible = value
        }

    private var _hasFooter: Boolean = false
    var hasFooter: Boolean
        get() = footerItemRegistry.isVisible
        set(value) {
            footerItemRegistry.isVisible = value
        }

    /**
     * Calculate the position in delegated adapter given the projected item position
     */
    private fun delegatedPosition(pos: Int): Int {
        return if (hasHeader) {
            pos - headerItemRegistry.factoryCount
        } else {
            pos
        }
    }

    /**
     * Calculate the projected item position given the position from delegated adapter
     */
    private fun realPosition(pos: Int): Int {
        return if (hasHeader) {
            pos + headerItemRegistry.factoryCount
        } else {
            pos
        }
    }

    override fun getItemCount(): Int {
        var baseCount = delegateAdapter.itemCount
        if (hasHeader) baseCount += headerItemRegistry.factoryCount
        if (hasFooter) baseCount += footerItemRegistry.factoryCount
        return baseCount
    }

    override fun onBindViewHolder(holder: RecyclerView.ViewHolder, position: Int) {
        l.debug("onBindViewHolder pos A $position")
        when {
            headerItemRegistry.inRangeAndVisible(position) -> headerItemRegistry.bind(holder)
            footerItemRegistry.inRangeAndVisible(position) -> footerItemRegistry.bind(holder)
            else -> {
                @Suppress("UNCHECKED_CAST")
                return delegateAdapter.onBindViewHolder(holder as WrappedVH, delegatedPosition((position)))
            }
        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): RecyclerView.ViewHolder {
        l.debug("onCreateViewHolder $viewType")
        return when {
            headerItemRegistry.isValidType(viewType) -> {
                headerItemRegistry.createViewHolder(viewType, parent, null)
                    ?: throw IllegalStateException("failed to find view holder factory")
            }
            footerItemRegistry.isValidType(viewType) -> {
                footerItemRegistry.createViewHolder(viewType, parent, null)
                    ?: throw IllegalStateException("failed to find view holder factory")
            }
            else -> delegateAdapter.onCreateViewHolder(parent, viewType)
        }
    }

    // TODO what would the payloads be
    override fun onBindViewHolder(
        holder: RecyclerView.ViewHolder,
        position: Int,
        payloads: MutableList<Any>
    ) {
        when {
            headerItemRegistry.inRangeAndVisible(position) ->
                headerItemRegistry.bind(holder, payloads)
            footerItemRegistry.inRangeAndVisible(position) ->
                footerItemRegistry.bind(holder, payloads)
            else -> {
                @Suppress("UNCHECKED_CAST")
                delegateAdapter.onBindViewHolder(holder as WrappedVH, delegatedPosition((position)), payloads)
            }
        }
    }

    override fun getItemId(position: Int): Long {
        return when {
            headerItemRegistry.inRangeAndVisible(position) ->
                headerItemRegistry.getIdByPosition(position)
            footerItemRegistry.inRangeAndVisible(position) ->
                footerItemRegistry.getIdByPosition(position)
            else -> delegateAdapter.getItemId(delegatedPosition(position))
        }
    }

    override fun setHasStableIds(hasStableIds: Boolean) {
        delegateAdapter.setHasStableIds(hasStableIds)
    }

    override fun getItemViewType(position: Int): Int {
        return when {
            headerItemRegistry.inRangeAndVisible(position) ->
                headerItemRegistry.getTypeByPosition(position)
            footerItemRegistry.inRangeAndVisible(position) ->
                footerItemRegistry.getTypeByPosition(position)
            else -> delegateAdapter.getItemViewType(delegatedPosition(position))
        }
    }

    // Recycle

    override fun onViewRecycled(holder: RecyclerView.ViewHolder) {
        if (headerItemRegistry.isAlive(holder)) {
            headerItemRegistry.recycleViewHolder(holder)
        } else if (footerItemRegistry.isAlive(holder)) {
            footerItemRegistry.recycleViewHolder(holder)
        } else {
            @Suppress("UNCHECKED_CAST")
            delegateAdapter.onViewRecycled(holder as WrappedVH)
        }
    }

    override fun onFailedToRecycleView(holder: RecyclerView.ViewHolder): Boolean {
        // TODO clean up the view for recycling
        // TODO it may cause type cast exception when recycling the header/footer view holder
        @Suppress("UNCHECKED_CAST")
        return delegateAdapter.onFailedToRecycleView(holder as WrappedVH)
    }

    // Attachment

    override fun onAttachedToRecyclerView(recyclerView: RecyclerView) {
        delegateAdapter.onAttachedToRecyclerView(recyclerView)
    }

    override fun onDetachedFromRecyclerView(recyclerView: RecyclerView) {
        delegateAdapter.onDetachedFromRecyclerView(recyclerView)
    }

    override fun onViewAttachedToWindow(holder: RecyclerView.ViewHolder) {
        if (!headerItemRegistry.isAlive(holder) && !footerItemRegistry.isAlive(holder)) {
            @Suppress("UNCHECKED_CAST")
            delegateAdapter.onViewAttachedToWindow(holder as WrappedVH)
        }
    }

    override fun onViewDetachedFromWindow(holder: RecyclerView.ViewHolder) {
        // TODO it may cause type cast exception when recycling the header/footer view holder
        if (!headerItemRegistry.isAlive(holder) && !footerItemRegistry.isAlive(holder)) {
            @Suppress("UNCHECKED_CAST")
            delegateAdapter.onViewDetachedFromWindow(holder as WrappedVH)
        }
    }

    // DataObserver

    // This intercept the notify* call from delegated Adapter, the caller of this observer does
    // not know the extra item from header and footer. So we are adjusting the positions so that
    // it affect proper range from io.hkhc.recyclerview perspective.

    /*
               +--------------+       +-------------------------+        +-------------------+
               |              |       |                         |        |                   |
               | RecyclerView +------>+ HeaderFooterListAdapter +------->+ delegated adapter |
               |              |       |                         |        |                   |
               +------+-------+       +-----------+-------------+        +---------+---------+
                      |                           |                                |
                      |                           |                                |
                      v                           v                                v
        +-------------+-------+       +-----------+-----------+          +---------+-----------+
        |                     |       |                       |          |                     |
        | AdapterDataObserver +<------+ BridgingDataObserver  +<---------+ AdapterDataObserver |
        |                     |       |                       |          |                     |
        +---------------------+       +-----------------------+          +---------------------+

     */

    class BridingObserver(
        private val adapter: HeaderFooterListAdapter<*>,
        private val delegatedObserver: RecyclerView.AdapterDataObserver
    ) : RecyclerView.AdapterDataObserver() {

        override fun onChanged() {
            // do nothing intentionally here.
            // It affect globally and let SelfObserver to handle the job
        }

        override fun onItemRangeRemoved(positionStart: Int, itemCount: Int) {
            delegatedObserver.onItemRangeRemoved(adapter.realPosition(positionStart), itemCount)
        }

        override fun onItemRangeMoved(fromPosition: Int, toPosition: Int, itemCount: Int) {
            delegatedObserver.onItemRangeMoved(
                adapter.realPosition(fromPosition),
                adapter.realPosition(toPosition), itemCount)
        }

        override fun onItemRangeInserted(positionStart: Int, itemCount: Int) {
            delegatedObserver.onItemRangeInserted(adapter.realPosition(positionStart), itemCount)
        }

        override fun onItemRangeChanged(positionStart: Int, itemCount: Int) {
            delegatedObserver.onItemRangeChanged(adapter.realPosition(positionStart), itemCount)
        }

        override fun onItemRangeChanged(positionStart: Int, itemCount: Int, payload: Any?) {
            delegatedObserver.onItemRangeChanged(adapter.realPosition(positionStart), itemCount, payload)
        }
    }

    private val delegatedObservers = mutableMapOf<RecyclerView.AdapterDataObserver, BridingObserver>()


    override fun registerAdapterDataObserver(observer: RecyclerView.AdapterDataObserver) {

        val bridgeObserver = BridingObserver(this, observer)
        delegatedObservers[observer] = bridgeObserver
        delegateAdapter.registerAdapterDataObserver(bridgeObserver)
        super.registerAdapterDataObserver(observer)
    }

    override fun unregisterAdapterDataObserver(observer: RecyclerView.AdapterDataObserver) {
        delegatedObservers[observer]?.apply {
            delegateAdapter.unregisterAdapterDataObserver(this)
        }
        super.unregisterAdapterDataObserver(observer)
    }

    // HasDelegate

    override fun getDelegated(): RecyclerView.Adapter<*> {
        if (delegateAdapter is HasDelegate) {
            @Suppress("UNCHECKED_CAST")
            (delegateAdapter as HasDelegate).also {
                return@getDelegated delegateAdapter.getDelegated()
            }
        } else {
            return delegateAdapter
        }
    }

    // Kotlin.Any

    override fun equals(other: Any?): Boolean {
        return delegateAdapter == other
    }

    // TODO we should have another hash code
    override fun hashCode(): Int {
        return delegateAdapter.hashCode()
    }

    // TODO we should have another toString
    override fun toString(): String {
        return delegateAdapter.toString()
    }
}
