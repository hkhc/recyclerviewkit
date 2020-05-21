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
import io.hkhc.recyclerviewkit.headfoot.HeaderFooterListAdapter
import io.hkhc.recyclerviewkit.internal.CommonAdapter
import io.hkhc.recyclerviewkit.internal.ViewHolderConsumer

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
open class RecyclerViewBuilder<T>() : ListSink<T> {

    override var listData: ListSource<T>? = null

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

    private var adapterFactory: () -> RecyclerView.Adapter<RecyclerView.ViewHolder> = { CommonAdapter<T>() }

    private var headerFactories = mutableListOf<ViewHolderFactory<Any>>()
    private var footerFactories = mutableListOf<ViewHolderFactory<Any>>()

    private var recyclerViewConfigurer:
                (RecyclerView, RecyclerView.Adapter<RecyclerView.ViewHolder>) -> Unit = { _, _ -> }

    @Suppress("unused")
    fun data(listSource: ListSource<T>) = apply {
        listData = listSource
    }

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
    fun configure(block: (RecyclerView, RecyclerView.Adapter<RecyclerView.ViewHolder>) -> Unit) {
        recyclerViewConfigurer = block
    }

    fun header(factory: ViewHolderFactory<Any>) = apply {
        headerFactories.add(factory)
    }

    fun footer(factory: ViewHolderFactory<Any>) = apply {
        footerFactories.add(factory)
    }

    @Suppress("unused")
    fun adapterFactory(factory: () -> RecyclerView.Adapter<RecyclerView.ViewHolder>) {
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

    private fun getRootAdapter(
        adapter: RecyclerView.Adapter<out RecyclerView.ViewHolder>
    ) : RecyclerView.Adapter<out RecyclerView.ViewHolder> {
        return if (adapter is HasDelegate) {
            return (adapter as HasDelegate).getDelegated()
        } else {
            adapter
        }
    }

    private fun getRootAdapter(recyclerView: RecyclerView):
            RecyclerView.Adapter<out RecyclerView.ViewHolder>? {
        if (recyclerView.adapter == null) return null
        return getRootAdapter(recyclerView.adapter as RecyclerView.Adapter<*>)
    }

    private fun setListData(
        recyclerView: RecyclerView,
        newAdapter: RecyclerView.Adapter<out RecyclerView.ViewHolder>,
        newData: ListSource<T>? = null
    ) {

        val rootAdapter = getRootAdapter(newAdapter)

        @Suppress("UNCHECKED_CAST")
        val sink = if (rootAdapter is ListSink<*>) {
            rootAdapter as ListSink<T>
        } else {
            throw IllegalArgumentException("new adapter does not implements ListSink")
        }

        sink.listData = if (newData!=null) {
            l.debug("Use new data")
            newData
        } else {
            l.debug("keep old data")
            var originalData: ListSource<T> = ListSource.EmptyListSource()
            val castedAdapter = getRootAdapter(recyclerView)
            if (castedAdapter is ListSink<*>) {
                @Suppress("UNCHECKED_CAST")
                originalData = castedAdapter.listData as ListSource<T>
            }
            originalData
        }

    }

    private fun registerFactoryToAdapter(
        adapter: RecyclerView.Adapter<out RecyclerView.ViewHolder>,
        factory: ViewHolderFactory<T>
    ) {
        @Suppress("UNCHECKED_CAST")
        val viewHolderConsumer: ViewHolderConsumer<T> = if (adapter is ViewHolderConsumer<*>)
            adapter as ViewHolderConsumer<T>
        else {
            throw IllegalArgumentException("adapterFactory provided a non RecyclerView.Adapter object")
        }

        viewHolderConsumer.registerViewHolderFactory(factory)

    }

    private fun setFactoryInitParam(factory: ViewHolderFactory<T>, paramBuilder: RecyclerViewItemParamBuilder<T>) {

        /*
            Inject the init param if the factory implemen ts ParamSink
         */
        @Suppress("UNCHECKED_CAST")
        if (factory is ParamSink<*>) {
            val sink = factory as ParamSink<T>
            sink.itemParam = paramBuilder.build()
        }

    }

    fun build(recycler: RecyclerView): RecyclerView {

        if (headerFactories.isNotEmpty() || footerFactories.isNotEmpty()) {
            val originalAdapterFactory = adapterFactory
            adapterFactory = {
                HeaderFooterListAdapter(originalAdapterFactory.invoke()).apply {
                    headerFactories.forEach { registerHeader(it) }
                    footerFactories.forEach { registerFooter(it) }
                    hasHeader = headerFactories.isNotEmpty()
                    hasFooter = footerFactories.isNotEmpty()
                }
            }
        }

        recycler.layoutManager = layoutManagerBuilder.build(recycler.context)

        recycler.clearItemDecolator()
        itemDecolatorBuilder.build(recycler.context).forEach {
            l.debug("addItemDecoration ${it::class.java.simpleName}")
            recycler.addItemDecoration(it)
        }

        recycler.adapter = adapterFactory.invoke().apply {

            /*
            Provide the list data if the adapter implements ListSink to accept the list
             */
            setListData(recycler, this, listData)

            viewHolderFactories.forEach { factory ->
                /*
                Further configure each of the factories if a configuration block is provided.
                 */
                factoryConfigurators[factory]?.let { factoryConfigurator ->
                    val paramBuilder = RecyclerViewItemParamBuilder(defaultParam)
                    factoryConfigurator.invoke(paramBuilder)
                    setFactoryInitParam(factory, paramBuilder)
                    registerFactoryToAdapter(getRootAdapter(this), factory)
                }
            }
            recyclerViewConfigurer.invoke(recycler, this)
        }

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
