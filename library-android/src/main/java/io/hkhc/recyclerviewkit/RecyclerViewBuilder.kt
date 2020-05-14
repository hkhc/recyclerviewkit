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

import androidx.recyclerview.widget.GridLayoutManager
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import io.hkhc.log.l
import io.hkhc.recyclerviewkit.internal.CommonAdapter

/**
 * It encapsulate details of RecyclerView, and configure RecyclerView in easy way.
 *
 * e.g.
 *         RecyclerViewBuilder<Book>()
 *           .viewHolder(::BookItemViewHolder)
 *           .layout(R.layout.item_book_list)
 *           .onClick { _, book, _ -> getBook(book) }
 *           .reuseData()
 *           .build(recyclerView)
 *
 * where ::BuildItemViewHolder represent the method reference of constructor of BuildItemViewHolder
 * class
 *
 */
@Suppress("TooManyFunctions")
open class RecyclerViewBuilder<T> : ListSink<T> {

    override var listData: ListSource<T> = ListSource.EmptyListSource()

    // implementing ListSink<T>
//    override var listData: ListSource<T>
//        get() = adapterParam.listData ?: ListSource.EmptyListSource()
//        set(value) { adapterParam.listData = value }

    /**
     * Use to create ViewHolder instance. It could be a lambda an method reference of constructor
     * of ViewHolder with property parameters. It is used by CommonAdapter to get new ViewHolder
     * instances.
     *
     * e.g.
     * class BookItemViewHolder(view: View, param: RecyclerViewParam<Book>): ViewHolder<Book> {
     *  ...
     * }
     *
     * val viewHolderFactory: ViewHolderFactory<Book> = { view, param ->
     *     BookItemViewHolder(view, param)
     * }
     * or
     * val viewHolderFactory: ViewHolderFactory<Book> = ::BookItemViewHolder
     *
     */
    private var viewHolderFactories = mutableListOf<ViewHolderFactory<T>>()
    private var factoryConfigurators = mutableMapOf<
        ViewHolderFactory<T>,
        (RecyclerViewItemParamBuilder<T>) -> Unit
        >()

    private val layoutManagerBuilder = LayoutManagerBuilder()
    private var itemDecolatorBuilder = ItemDecolatorBuilder()

    private var defaultParam = RecyclerViewItemParam<T>()

    private var adapterFactory: () -> CommonAdapter<T> = { CommonAdapter() }

    private var configBlock: (RecyclerView, CommonAdapter<T>) -> Unit = { _, _ -> }

    @Suppress("unused")
    fun data(listSource: ListSource<T>) = apply { listData = listSource }

    // ------------------- LayoutManager setting --------------------

    /**
     * Provide a custom LayoutManger instance. The #sequenial(), #reverse() settings
     * will be ignored.
     */
    @Suppress("unused")
    fun layoutManager(newLayoutManager: RecyclerView.LayoutManager) = apply {
        layoutManagerBuilder.layoutManager(newLayoutManager)
    }

    /**
     * Declare to use vertical LinearLayoutManager, the order of list is determined by
     * #sequential() or #reverse(). The instance provided by
     * layoutManager(newLayoutManager: RecyclerView.LayoutManager) will be overwritten.
     */
    @Suppress("unused")
    fun linearVertical() = apply {
        layoutManagerBuilder.linearVertical()
    }

    /**
     * Declare to use horizontal LinearLayoutManager, the order of list is determined by
     * #sequential() or #reverse(). The instance provided by
     * layoutManager(newLayoutManager: RecyclerView.LayoutManager) will be overwritten.
     */
    @Suppress("unused")
    fun linearHorizontal() = apply {
        layoutManagerBuilder.linearHorizontal()
    }

    /**
     * Declare to use vertical GridLayoutManager, the order of list is determined by
     * #sequential() or #reverse(). The instance provided by
     * layoutManager(newLayoutManager: RecyclerView.LayoutManager) will be overwritten.
     */
    @Suppress("unused")
    fun gridVertical(spanCount: Int) = apply {
        layoutManagerBuilder.gridVertical(spanCount)
    }

    /**
     * Declare to use horizontal GridLayoutManager, the order of list is determined by
     * #sequential() or #reverse(). The instance provided by
     * layoutManager(newLayoutManager: RecyclerView.LayoutManager) will be overwritten.
     */
    @Suppress("unused")
    fun gridHorizontal(spanCount: Int) = apply {
        layoutManagerBuilder.gridHorizontal(spanCount)
    }

    /**
     * Declare to use order item as native order, It is ignored if custom LayoutManager
     * is provided by layoutManager(newLayoutManager: RecyclerView.LayoutManager).
     */
    @Suppress("unused")
    fun sequential() = apply {
        layoutManagerBuilder.sequential()
    }

    /**
     * Declare to use order item as reverse order, It is ignored if custom LayoutManager
     * is provided by layoutManager(newLayoutManager: RecyclerView.LayoutManager).
     */
    @Suppress("unused")
    fun reverse() = apply {
        layoutManagerBuilder.reverse()
    }

    @Suppress("unused")
    fun itemDecolators(block: ItemDecolatorBuilder.() -> Unit) = apply {
        itemDecolatorBuilder = ItemDecolatorBuilder()
        block.invoke(itemDecolatorBuilder)
    }

    fun defaultItemParam(config: RecyclerViewItemParamBuilder<T>.() -> Unit) = apply {
        val builder = RecyclerViewItemParamBuilder(defaultParam)
        config.invoke(builder)
        defaultParam = builder.build()
    }

    @Suppress("unused")
    fun viewHolderBy(
        factory: ViewHolderFactory<T>,
        config: RecyclerViewItemParamBuilder<T>.() -> Unit = {}
    ) = apply {
        if (factory is ParamSink<*>) {
            factoryConfigurators[factory] = config
        }
        viewHolderFactories.add(factory)
    }

    @Suppress("unused")
    fun configure(block: (RecyclerView, CommonAdapter<T>) -> Unit) {
        configBlock = block
    }

    @Suppress("unused")
    fun adapterFactory(factory: () -> CommonAdapter<T>) {
        adapterFactory = factory
    }

//    @Suppress("unused")
//    fun viewHolderByCreator(
//        creator: (ViewGroup, View?, RecyclerViewItemParam<T>) -> CommonViewHolder<T>,
//        config: RecyclerViewItemParamBuilder<T>.() -> Unit = {}
//    ) = apply {
//        val factory = SimpleViewHolderFactory(creator)
//        factoryConfigurators[factory] = config
//        viewHolderFactories.add(factory)
//    }

    private fun <VH : RecyclerView.ViewHolder> getRootAdapter(adapter: RecyclerView.Adapter<*>):
        RecyclerView.Adapter<VH>? {

        return if (adapter is HasDelegate) {
            @Suppress("UNCHECKED_CAST")
            return (adapter as HasDelegate).let {
                val delegate = adapter.getDelegated()
                if (delegate is RecyclerView.Adapter<*>)
                    (delegate as RecyclerView.Adapter<VH>)
                else
                    null
            }
        } else {
            @Suppress("UNCHECKED_CAST")
            (adapter as RecyclerView.Adapter<VH>)
        }
    }

    @Suppress("UNCHECKED_CAST")
    private fun <T> getCommonAdapter(recyclerView: RecyclerView): CommonAdapter<T>? {
        if (recyclerView.adapter == null) return null
        val rootAdapter = getRootAdapter<RecyclerView.ViewHolder>(recyclerView.adapter as RecyclerView.Adapter<*>)
        return if (rootAdapter is CommonAdapter<*>) {
            recyclerView.adapter as CommonAdapter<T>
        } else {
            null
        }
    }

    fun build(recycler: RecyclerView): RecyclerView {

        val castedAdapter: CommonAdapter<T>? = getCommonAdapter(recycler)
        castedAdapter?.listData?.let { listData = it }

        recycler.layoutManager = layoutManagerBuilder.build(recycler.context)

        recycler.clearItemDecolator()
        itemDecolatorBuilder.build(recycler.context).forEach {
            l.debug("addItemDecoration ${it::class.java.simpleName}")
            recycler.addItemDecoration(it)
        }

        val specifiedFactories = viewHolderFactories
        val data = listData

        val newAdapter = adapterFactory.invoke().apply {
            listData = data
            specifiedFactories.forEach {
                val nullableConfig = factoryConfigurators[it]
                nullableConfig?.let { config ->
                    val paramBuilder = RecyclerViewItemParamBuilder(defaultParam)
                    config.invoke(paramBuilder)
                    @Suppress("UNCHECKED_CAST")
                    val sink = it as ParamSink<T>
                    sink.itemParam = paramBuilder.build()
                    registerViewHolderFactory(it)
                }
            }
            configBlock.invoke(recycler, this)
        }
        // TODO add header footer adapter if needed
        recycler.adapter = newAdapter

        return recycler
    }
}

private fun RecyclerView.clearItemDecolator() {
    while (itemDecorationCount > 0) {
        removeItemDecorationAt(0)
    }
}

private fun <T> reconstructBuilderForLayoutManager(
    builder: RecyclerViewBuilder<T>,
    recycler: RecyclerView
) {

    recycler.layoutManager?.let { lm ->
        if (lm::class == LinearLayoutManager::class) {
            val llm = lm as LinearLayoutManager
            if (llm.orientation == RecyclerView.VERTICAL) {
                builder.linearVertical()
            } else {
                builder.linearHorizontal()
            }
            if (llm.reverseLayout) {
                builder.sequential()
            } else {
                builder.reverse()
            }
        } else if (lm::class == GridLayoutManager::class) {
            val glm = lm as GridLayoutManager
            val span = glm.spanCount
            if (glm.orientation == RecyclerView.VERTICAL) {
                builder.gridVertical(span)
            } else {
                builder.gridHorizontal(span)
            }
            if (glm.reverseLayout) {
                builder.sequential()
            } else {
                builder.reverse()
            }
        } else {
            builder.layoutManager(lm)
        }
    }
}

// fun <T> RecyclerView.newBuilder(): RecyclerViewBuilder<T> {
//
//    @Suppress("UNCHECKED_CAST")
//    val castedAdapter: CommonAdapter<T> =
//        if (adapter is CommonAdapter<*>) {
//            adapter as CommonAdapter<T>
//        } else {
//            // the builder cannot server the io.hkhc.recyclerview if the adapter is not CommonAdapter.
//            l.warn("The Adapter is not CommonAdapter, cannot create new builder based on that.")
//            return RecyclerViewBuilder()
//        }
//
//    val oldAdapterParam = castedAdapter.param
//
//    return RecyclerViewBuilder<T>()
//        .data(castedAdapter.listData)
//        .onClick(oldAdapterParam.onClick)
//        .apply {
//
//            reconstructBuilderForLayoutManager(this, this@newBuilder)
//
//            castedAdapter.viewHolderCollection.getFactories().forEach {
//                viewHolderBy(it)
//            }
//
//            itemDecolators {
//                val count = this@newBuilder.itemDecorationCount
//                for (i in 0..count) {
//                    addDecolator(this@newBuilder.getItemDecorationAt(i))
//                }
//            }
//        }
//        .layout(oldAdapterParam.itemLayout)
//        .itemView1(oldAdapterParam.itemView1)
//        .itemView2(oldAdapterParam.itemView2)
//        .mapper1(oldAdapterParam.mapper1)
//        .mapper2(oldAdapterParam.mapper2)
//        .itemCheckable(oldAdapterParam.itemCheckable)
//        .mapperCheckable(oldAdapterParam.mapperCheckable)
// }
