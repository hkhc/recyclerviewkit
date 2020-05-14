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

import android.view.View
import androidx.annotation.IdRes
import androidx.annotation.LayoutRes

@Suppress("TooManyFunctions")
class RecyclerViewItemParamBuilder<T>(defaultItemParam: RecyclerViewItemParam<T>? = null) {

    private var newParam = defaultItemParam?.copy() ?: RecyclerViewItemParam()

    private var canHandle: (T) -> Boolean = { true }
    private var layoutResId: Int = 0

    fun onClick(lambda: (View, T, Int) -> Unit) = apply {
        newParam.onClick = lambda
    }

    fun onClickWithData(lambda: (T) -> Unit) = apply {
        newParam.onClick = { _, t, _ -> lambda.invoke(t) }
    }

    fun onClickWithPosition(lambda: (Int) -> Unit) = apply {
        newParam.onClick = { _, _, p -> lambda.invoke(p) }
    }

    fun map(@LayoutRes layoutResId: Int) = apply {
        this.layoutResId = layoutResId
    }

    fun canHandle(canHandle: (T) -> Boolean) = apply {
        this.canHandle = canHandle
    }

    fun layout(@LayoutRes itemLayout: Int) = apply { newParam.itemLayout = itemLayout }

    @Suppress("unused")
    fun itemView1(@IdRes itemView: Int) = apply { newParam.itemView1 = itemView }

    @Suppress("unused")
    fun itemView2(@IdRes itemView: Int) = apply { newParam.itemView2 = itemView }

    @Suppress("unused")
    fun mapper1(lambda: (T) -> (CharSequence)) = apply { newParam.mapper1 = lambda }

    @Suppress("unused")
    fun mapper2(lambda: (T) -> (CharSequence)) = apply { newParam.mapper2 = lambda }

    @Suppress("unused")
    fun itemCheckable(@IdRes itemView: Int) =
        apply { newParam.itemCheckable = itemView }

    @Suppress("unused")
    fun onToggle(lambda: (View, Boolean, Int) -> Unit) =
        apply { newParam.onToggle = lambda }

    @Suppress("unused")
    fun mapperCheckable(lambda: (T) -> (Boolean)) =
        apply { newParam.mapperCheckable = lambda }

    fun build(): RecyclerViewItemParam<T> {
        return newParam
    }
}
