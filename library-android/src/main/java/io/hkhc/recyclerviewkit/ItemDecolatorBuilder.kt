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

import android.content.Context
import androidx.recyclerview.widget.DividerItemDecoration
import androidx.recyclerview.widget.DividerItemDecoration.HORIZONTAL
import androidx.recyclerview.widget.DividerItemDecoration.VERTICAL
import androidx.recyclerview.widget.RecyclerView
import io.hkhc.log.l

class ItemDecolatorBuilder {

    private var itemDecorators = mutableListOf<RecyclerView.ItemDecoration>()
    private var horizontalDivider = false
    private var verticalDivider = false

    /**
     * Specify that no divider is needed
     */
    @Suppress("unused")
    fun withHorizontalDivider() = apply {
        horizontalDivider = true
    }
    @Suppress("unused")
    fun withVerticalDivider() = apply {
        verticalDivider = true
    }

    @Suppress("unused")
    fun noHorizontalDivider() = apply {
        horizontalDivider = false
    }
    @Suppress("unused")
    fun noVerticalDivider() = apply {
        verticalDivider = false
    }

    @Suppress("unused")
    fun noDivider() = apply {
        horizontalDivider = false
        verticalDivider = false
    }

    @Suppress("unused")
    fun addDecolator(decolator: RecyclerView.ItemDecoration) {
        itemDecorators.add(decolator)
    }

    /**
     * Specify that standard ItemDecolator is used
     */
//    fun withDivider() = apply {
//        withDivider = true
//    }

    /**
     * Specify a custom item decolator.
     * Currently only one item decolator is supported
     */
//    fun withDivider(decorator: RecyclerView.ItemDecoration) = apply {
//        itemDecorator = decorator
//        withDivider = true
//    }

    /**
     * Specify that no ItemDecolator is used
     */
//    fun noDivider() = apply {
//        withDivider = false
//    }

    /**
     * Specify the orientation of the item decolator
     * RecyclerView.VERTICAL
     * RecyclerView.HORIZONTAL
     */
//    fun orientation(orientation: Int) = apply {
//        this.orientation = orientation
//    }

    /**
     * Create and return a standard Item Decolator, or return the custom item decolator
     */
    fun build(context: Context): List<RecyclerView.ItemDecoration> {

        val finalItemDecorations = mutableListOf<RecyclerView.ItemDecoration>()

        if (horizontalDivider) {
            finalItemDecorations.add(DividerItemDecoration(context, HORIZONTAL))
        }

        if (verticalDivider) {
            finalItemDecorations.add(DividerItemDecoration(context, VERTICAL))
        }

        finalItemDecorations.addAll(itemDecorators)

        l.debug("itemDecoration count ${finalItemDecorations.size}")

        return finalItemDecorations
    }
}
