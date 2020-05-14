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
import androidx.recyclerview.widget.GridLayoutManager
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView

class LayoutManagerBuilder {

    /**
     * Type of io.hkhc.recyclerview layout. It is used to determine the actual LayoutManager class
     */
    private enum class LayoutType(val orientation: Int) {
        LINEAR_VERTICAL(RecyclerView.VERTICAL),
        LINEAR_HORIZONTAL(RecyclerView.HORIZONTAL),
        GRID_VERTICAL(RecyclerView.VERTICAL),
        GRID_HORIZONTAL(RecyclerView.HORIZONTAL),
        CUSTOM(0)
    }

    /**
     * Type of LayoutManager to be created.
     */
    private var layoutType = LayoutType.LINEAR_VERTICAL

    private var layoutManager: RecyclerView.LayoutManager? = null

    /**
     * Order of item as needed when creating LayoutManager instance
     */
    private var reverseOrder = false

    /**
     * Column/Row Span for GridLayoutManager
     */
    private var gridSpenCount = 2

    /**
     * Provide a custom LayoutManger instance. The #sequenial(), #reverse() settings
     * will be ignored.
     */
    fun layoutManager(newLayoutManager: RecyclerView.LayoutManager) = apply {
        layoutType = LayoutType.CUSTOM
        layoutManager = newLayoutManager
    }

    /**
     * Declare to use vertical LinearLayoutManager, the order of list is determined by
     * #sequential() or #reverse(). The instance provided by
     * layoutManager(newLayoutManager: RecyclerView.LayoutManager) will be overwritten.
     */
    fun linearVertical() = apply {
        layoutType = LayoutType.LINEAR_VERTICAL
    }

    /**
     * Declare to use horizontal LinearLayoutManager, the order of list is determined by
     * #sequential() or #reverse(). The instance provided by
     * layoutManager(newLayoutManager: RecyclerView.LayoutManager) will be overwritten.
     */
    fun linearHorizontal() = apply {
        layoutType = LayoutType.LINEAR_HORIZONTAL
    }

    /**
     * Declare to use vertical GridLayoutManager, the order of list is determined by
     * #sequential() or #reverse(). The instance provided by
     * layoutManager(newLayoutManager: RecyclerView.LayoutManager) will be overwritten.
     */
    fun gridVertical(spanCount: Int) = apply {
        layoutType = LayoutType.GRID_VERTICAL
        gridSpenCount = spanCount
    }

    /**
     * Declare to use horizontal GridLayoutManager, the order of list is determined by
     * #sequential() or #reverse(). The instance provided by
     * layoutManager(newLayoutManager: RecyclerView.LayoutManager) will be overwritten.
     */
    fun gridHorizontal(spanCount: Int) = apply {
        layoutType = LayoutType.GRID_HORIZONTAL
        gridSpenCount = spanCount
    }

    /**
     * Declare to use order item as native order, It is ignored if custom LayoutManager
     * is provided by layoutManager(newLayoutManager: RecyclerView.LayoutManager).
     */
    fun sequential() = apply {
        reverseOrder = false
    }

    /**
     * Declare to use order item as reverse order, It is ignored if custom LayoutManager
     * is provided by layoutManager(newLayoutManager: RecyclerView.LayoutManager).
     */
    fun reverse() = apply {
        reverseOrder = true
    }

    /**
     * get the specified orientation of layout. It could be RecyclerView.VERRICAL or
     * RecyclerView.HORIZONTAL
     */
    val orientation: Int
        get() = layoutType.orientation

    /**
     * Start building the LayoutManager instance
     */
    fun build(context: Context): RecyclerView.LayoutManager {

        return when (layoutType) {
            LayoutType.LINEAR_HORIZONTAL, LayoutType.LINEAR_VERTICAL -> LinearLayoutManager(
                context,
                layoutType.orientation,
                reverseOrder
            )
            LayoutType.GRID_VERTICAL, LayoutType.GRID_HORIZONTAL -> GridLayoutManager(
                context,
                gridSpenCount,
                layoutType.orientation,
                reverseOrder
            )
            LayoutType.CUSTOM -> layoutManager!!
        }
    }
}
