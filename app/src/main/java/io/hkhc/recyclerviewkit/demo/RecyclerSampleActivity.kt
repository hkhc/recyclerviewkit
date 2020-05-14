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

import android.os.Bundle
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.RecyclerView
import io.hkhc.demo.demo.R
import io.hkhc.recyclerviewkit.RecyclerViewBuilder
import io.hkhc.recyclerviewkit.array
import io.hkhc.recyclerviewkit.item.OneLineItem
import io.hkhc.utils.isEven

class RecyclerSampleActivity : AppCompatActivity() {

    data class Data(val title: String, val author: String, val selected: Boolean)

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_recycler_sample)

        val listView = findViewById<RecyclerView>(R.id.list)

        @Suppress("MagicNumber")
        val data = Array(200) {
            Data(
                "Book title $it",
                "author $it",
                it.isEven()
            )
        }

        RecyclerViewBuilder<Data>()
            .viewHolderBy(OneLineItem()) {
                layout(android.R.layout.simple_list_item_1)
                onClick { _, value, _ ->
                    Toast.makeText(this@RecyclerSampleActivity, value.toString(), Toast.LENGTH_SHORT).show()
                }
            }
            .array(data)
            .build(listView)

//        TwoLineRecyclerViewBuilder<Data>(listView)
//            .mapper1(Data::title)
//            .mapper2(Data::author)
//            .listData(data)
//            .onClick { _, value, _ ->
//                Toast.makeText(this, value.toString(), Toast.LENGTH_SHORT).show()
//            }
//            .build()

//        TwoLineSingleChoiceRecyclerViewBuilder<Data>(listView)
//            .mapperCheckable(Data::selected)
//            .onToggle { _, value, _ ->
//                Toast.makeText(this, "Toggle $value}", Toast.LENGTH_SHORT).show()
//            }
//            .mapper1(Data::title)
//            .mapper2(Data::author)
//            .listData(data)
//            .onClick { _, value, _ ->
//                Toast.makeText(this, "Click $value", Toast.LENGTH_SHORT).show()
//            }
//            .build()
    }
}
