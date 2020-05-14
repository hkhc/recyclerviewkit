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
import android.view.ViewGroup
import android.widget.Checkable
import android.widget.TextView
import androidx.annotation.IdRes

@Suppress("UNCHECKED_CAST")
fun <V> getView(v: View, @IdRes viewId: Int, clazz: Class<V>): V? {

    return if (clazz.isAssignableFrom(v::class.java)) {
        v as V
    } else if (v is ViewGroup) {
        v.findViewById<View>(viewId) as V
    } else {
        throw IllegalArgumentException("view does not match the required type")
    }
}

fun <T> setTextViewText(tv: TextView, value: T) {

    when (value) {
        is String -> tv.text = value
        is CharSequence -> tv.text = value
        else -> tv.text = value.toString()
    }
}

fun <T> setCheckable(cb: Checkable, value: T) {

    when (value) {
        is Boolean -> cb.isChecked = value
        is Int -> cb.isChecked = (value != 0)
        is Long -> cb.isChecked = (value != 0)
        is Float -> cb.isChecked = (value != 0)
        is Double -> cb.isChecked = (value != 0)
        is String -> cb.isChecked = value.isNotEmpty()
        is CharSequence -> cb.isChecked = value.isNotEmpty()
        else -> cb.isChecked = false
    }
}
