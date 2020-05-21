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
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.LiveData
import androidx.lifecycle.Observer
import androidx.paging.LivePagedListBuilder
import androidx.paging.PagedList
import androidx.paging.PagedListAdapter
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.RecyclerView
import io.hkhc.demo.demo.R
import io.hkhc.recyclerviewkit.RecyclerViewBuilder
import io.hkhc.recyclerviewkit.getRootAdapter
import io.hkhc.recyclerviewkit.item.OneLineItem
import io.hkhc.recyclerviewkit.paging
import io.reactivex.disposables.Disposable

class HeaderPagedListDemoActivity : AppCompatActivity() {

    lateinit var listLiveData: LiveData<PagedList<Item>>
    var itemAdapter = ItemListAdapter()
//    var listAdapter = LoadingListAdapter(itemAdapter)
    var itemApi = ItemApi()
    lateinit var disposable: Disposable

    companion object {
        const val PAGE_SIZE = 10
    }

    fun initListView(ctx: Context, recyclerView: RecyclerView) {

        listLiveData = LivePagedListBuilder(
            MyDataSourceFactory(itemApi),
            PAGE_SIZE
        ).build()

        listLiveData.observe(this@HeaderPagedListDemoActivity, Observer {
            (recyclerView.getRootAdapter() as PagedListAdapter<Item, RecyclerView.ViewHolder>)
                .submitList(it)
        })

        RecyclerViewBuilder<Item>()
            .linearVertical()
            .viewHolderBy(OneLineItem())
            .header(HeaderViewHolderFactory("Header 1"))
            .header(HeaderViewHolderFactory("Header 2"))
            .footer(HeaderViewHolderFactory("Footer 1"))
            .footer(HeaderViewHolderFactory("Footer 2"))
            .paging(object : DiffUtil.ItemCallback<Item>() {
                override fun areItemsTheSame(oldItem: Item, newItem: Item): Boolean =
                    oldItem.id == newItem.id

                override fun areContentsTheSame(oldItem: Item, newItem: Item): Boolean =
                    oldItem == newItem
            })
            .itemDecolators { withVerticalDivider() }
            .configure { recyclerView, _ -> recyclerView.setHasFixedSize(false) }
            .build(recyclerView)

//    val itemTouchHelper = ItemTouchHelper(SwipeController())
//    itemTouchHelper.attachToRecyclerView(recyclerView)


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
