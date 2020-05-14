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

@file:Suppress("TooManyFunctions")

package io.hkhc.recyclerviewkit

import android.content.Context
import android.content.res.Resources
import androidx.annotation.ArrayRes
import androidx.annotation.PluralsRes
import androidx.recyclerview.widget.RecyclerView

sealed class ListSource<T> {

    class EmptyListSource<T> : ListSource<T>() {
        override operator fun get(i: Int): T {
            throw IndexOutOfBoundsException("")
        }
        override val size: Int
            get() = 0
    }

    class ArrayListSource<T>(private val array: Array<T>) : ListSource<T>() {
        override operator fun get(i: Int): T = array[i]
        override val size: Int
            get() = array.size
    }

    class ListListSource<T>(private val list: List<T>) : ListSource<T>() {
        override operator fun get(i: Int): T = list[i]
        override val size: Int
            get() = list.size
    }

    class StringArrayResourceListSource(ctx: Context, @ArrayRes private val arrayId: Int) :
        ListSource<String>() {

        val stringArray: Array<String> = ctx.resources.getStringArray(arrayId)

        override operator fun get(i: Int): String = stringArray[i]
        override val size: Int
            get() = stringArray.size
    }

    class IntArrayResourceListSource(ctx: Context, @ArrayRes private val arrayId: Int) :
        ListSource<Int>() {

        val intArray = ctx.resources.getIntArray(arrayId)

        override operator fun get(i: Int): Int = intArray[i]
        override val size: Int
            get() = intArray.size
    }

    class QuantityListSource(ctx: Context, private val quantityId: Int, private val length: Int) :
        ListSource<String>() {

        val resources: Resources = ctx.resources

        override operator fun get(i: Int): String {
            if (i in 0 until length) {
                return resources.getQuantityString(quantityId, i)
            } else {
                throw IndexOutOfBoundsException()
            }
        }

        override val size: Int
            get() = length
    }

    class QuantityListSource2(ctx: Context, private val quantityId: Int, private val length: Int) :
        ListSource<String>() {

        val resources: Resources = ctx.resources

        override operator fun get(i: Int): String {
            if (i in 0 until length) {
                return resources.getQuantityString(quantityId, i)
            } else {
                throw IndexOutOfBoundsException()
            }
        }

        override val size: Int
            get() = length
    }

    abstract operator fun get(i: Int): T
    abstract val size: Int
}

internal fun <T> RecyclerView.data(source: ListSource<T>) {
    if (adapter is ListSink<*>) {
        @Suppress("UNCHECKED_CAST")
        val realAdapter: ListSink<T> = adapter as ListSink<T>
        realAdapter.listData = source
    }
}

fun <T> RecyclerViewBuilder<T>.array(data: Array<T>) = apply {
    data(ListSource.ArrayListSource(data))
}

fun <T> RecyclerView.array(data: Array<T>) {
    data(ListSource.ArrayListSource(data))
}

fun <T> RecyclerViewBuilder<T>.list(data: List<T>) = apply {
    data(ListSource.ListListSource(data))
}

fun <T> RecyclerView.list(data: List<T>) {
    data(ListSource.ListListSource(data))
}

fun RecyclerViewBuilder<String>.stringResArray(ctx: Context, @ArrayRes arrayId: Int) = apply {
    data(ListSource.StringArrayResourceListSource(ctx, arrayId))
}

fun RecyclerView.stringResArray(ctx: Context, @ArrayRes arrayId: Int) {
    data(ListSource.StringArrayResourceListSource(ctx, arrayId))
}

fun RecyclerViewBuilder<Int>.intResArray(ctx: Context, @ArrayRes arrayId: Int) = apply {
    data(ListSource.IntArrayResourceListSource(ctx, arrayId))
}

fun RecyclerView.intResArray(ctx: Context, @ArrayRes arrayId: Int) {
    data(ListSource.IntArrayResourceListSource(ctx, arrayId))
}

fun RecyclerViewBuilder<String>.quantityListSource(ctx: Context, @PluralsRes quantityId: Int, length: Int) = apply {
    data(ListSource.QuantityListSource(ctx, quantityId, length))
}

fun RecyclerView.quantityListSource(ctx: Context, @PluralsRes quantityId: Int, length: Int) {
    data(ListSource.QuantityListSource(ctx, quantityId, length))
}

fun RecyclerViewBuilder<String>.quantityListSource2(ctx: Context, @PluralsRes quantityId: Int, length: Int) = apply {
    data(ListSource.QuantityListSource2(ctx, quantityId, length))
}

fun RecyclerView.quantityListSource2(ctx: Context, @PluralsRes quantityId: Int, length: Int) {
    data(ListSource.QuantityListSource2(ctx, quantityId, length))
}
