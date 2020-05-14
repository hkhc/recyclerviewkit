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

package io.hkhc.recyclerviewkit.internal

import io.hkhc.log.l
import io.hkhc.recyclerviewkit.ViewHolderFactory

class ViewHolderFactoryCollection<T> {

    var viewHolderFactories = mutableListOf<ViewHolderFactory<T>>()

    fun register(factory: ViewHolderFactory<T>) {
        viewHolderFactories.add(factory)
    }

    fun findViewHolderFactory(viewType: Int): ViewHolderFactory<T>? {
        // TODO assert that viewType is not out of range
        if (viewType < 0 || viewType >= viewHolderFactories.size) {
            throw IllegalStateException("Unknown viewType ($viewType)")
        }
        return viewHolderFactories[viewType]
    }

    /**
     * return index of factory. return -1 if no factory is found
     */
    fun matchViewHolderFactory(data: T): Int {
        l.debug("before canHandle size = ${viewHolderFactories.size}")
        viewHolderFactories.forEachIndexed { index, it ->
            if (it.canHandle(data)) return@matchViewHolderFactory index
        }
        return -1
    }
}
