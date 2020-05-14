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

import android.content.Context
import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.LiveData
import androidx.lifecycle.Observer
import androidx.paging.LivePagedListBuilder
import androidx.paging.PagedList
import androidx.recyclerview.widget.DividerItemDecoration
import androidx.recyclerview.widget.ItemTouchHelper
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import io.hkhc.demo.demo.R
import io.hkhc.recyclerPaging.SwipeController
import io.hkhc.recyclerviewkit.headfoot.HeaderFooterListAdapter
import io.reactivex.disposables.Disposable

class HeaderActivity : AppCompatActivity() {

    lateinit var listLiveData: LiveData<PagedList<Item>>
    var itemAdapter = ItemListAdapter()
//    var listAdapter = LoadingListAdapter(itemAdapter)
    var itemApi = ItemApi()
    lateinit var disposable: Disposable

    companion object {
        const val PAGE_SIZE = 10
    }

    fun initListView(ctx: Context, recyclerView: RecyclerView) {

//        listAdapter.hasFooter = true

        var headingAdapter = HeaderFooterListAdapter(itemAdapter).apply {
            registerHeader(HeaderViewHolderFactory("Header 1"))
            registerHeader(HeaderViewHolderFactory("Header 2"))
            registerFooter(HeaderViewHolderFactory("Footer 1"))
            registerFooter(HeaderViewHolderFactory("Footer 2"))
            hasHeader = true
            hasFooter = true
        }

        recyclerView.apply {
            layoutManager = LinearLayoutManager(ctx)
            listLiveData = LivePagedListBuilder(
                io.hkhc.recyclerviewkit.demo.MyDataSourceFactory(
                    itemApi
                ),
                PAGE_SIZE
            ).build()
            adapter = headingAdapter
            listLiveData.observe(this@HeaderActivity, Observer {
                itemAdapter.submitList(it)
            })
            setHasFixedSize(false)
            val dividerItemDecoration = DividerItemDecoration(
                this@HeaderActivity,
                (layoutManager as LinearLayoutManager).getOrientation()
            )
            addItemDecoration(dividerItemDecoration)
        }
        val itemTouchHelper = ItemTouchHelper(SwipeController())
        itemTouchHelper.attachToRecyclerView(recyclerView)
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_header_test)

        val listView = findViewById<RecyclerView>(R.id.list)

        initListView(this, listView)
    }

    override fun onPause() {
        super.onPause()
        disposable.dispose()
    }

    override fun onResume() {
        super.onResume()
//        disposable = CompositeDisposable().apply {
//            add(itemApi.activityIndicator.subscribe {
//                listAdapter.hasFooter = it
//            })
//        }
    }

    override fun onDestroy() {
        super.onDestroy()
        disposable.dispose()
    }
}
