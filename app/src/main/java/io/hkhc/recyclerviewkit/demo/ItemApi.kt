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

import io.reactivex.Observable
import io.reactivex.android.schedulers.AndroidSchedulers
import io.reactivex.schedulers.Schedulers
import io.reactivex.subjects.BehaviorSubject
import io.reactivex.subjects.Subject
import java.util.concurrent.TimeUnit

class ItemApi {

    companion object {
        const val PAGE_SIZE = 30
        const val RESULT_SIZE = 5
        const val RESPONSE_DELAY_MS = 500L // ms
    }

    val activityIndicator: Subject<Boolean> = BehaviorSubject.create()

    private fun getPage(page: Int): List<Item> {

        val start = PAGE_SIZE * (page - 1)
        var end = PAGE_SIZE * page
        if (end> RESULT_SIZE) end =
            RESULT_SIZE

        return (start until end).map { Item(it, "itme ${it + 1}") }
    }

    fun getList(page: Int): Observable<List<Item>> {

        return Observable.just(getPage(page))
            .subscribeOn(Schedulers.io())
            .observeOn(AndroidSchedulers.mainThread())
            .delay(RESPONSE_DELAY_MS, TimeUnit.MILLISECONDS)
            .doOnSubscribe { activityIndicator.onNext(true) }
            .doOnComplete { activityIndicator.onNext(false) }
    }
}
