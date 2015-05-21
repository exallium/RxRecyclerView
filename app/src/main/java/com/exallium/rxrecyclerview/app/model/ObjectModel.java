/*
 * The MIT License (MIT)
 *
 * Copyright (c) 2015 Alex Hart
 *
 * Permission is hereby granted, free of charge, to any person obtaining a copy
 * of this software and associated documentation files (the "Software"), to deal
 * in the Software without restriction, including without limitation the rights
 * to use, copy, modify, merge, publish, distribute, sublicense, and/or sell
 * copies of the Software, and to permit persons to whom the Software is
 * furnished to do so, subject to the following conditions:
 *
 * The above copyright notice and this permission notice shall be included in
 * all copies or substantial portions of the Software.
 *
 * THE SOFTWARE IS PROVIDED "AS IS", WITHOUT WARRANTY OF ANY KIND, EXPRESS OR
 * IMPLIED, INCLUDING BUT NOT LIMITED TO THE WARRANTIES OF MERCHANTABILITY,
 * FITNESS FOR A PARTICULAR PURPOSE AND NONINFRINGEMENT. IN NO EVENT SHALL THE
 * AUTHORS OR COPYRIGHT HOLDERS BE LIABLE FOR ANY CLAIM, DAMAGES OR OTHER
 * LIABILITY, WHETHER IN AN ACTION OF CONTRACT, TORT OR OTHERWISE, ARISING FROM,
 * OUT OF OR IN CONNECTION WITH THE SOFTWARE OR THE USE OR OTHER DEALINGS IN
 * THE SOFTWARE.
 */

package com.exallium.rxrecyclerview.app.model;

import com.exallium.rxrecyclerview.lib.RxAdapterEvent;
import rx.Observable;
import rx.Observer;
import rx.Subscriber;
import rx.functions.Action1;
import rx.functions.Func1;
import rx.subjects.PublishSubject;

import java.util.Collections;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

/**
 * Singleton to act as our Model layer.
 */
public final class ObjectModel {

    private static ObjectModel instance;

    public static ObjectModel getInstance() {
        if (instance == null)
            instance = new ObjectModel();
        return instance;
    }

    // My... "database"...
    public Map<Long, String> itemMap = Collections.synchronizedMap(new LinkedHashMap<Long, String>());

    // When I get an item, I want to add it to my itemMap, and then transmit that item down the road.
    private Observable<RxAdapterEvent<Long, String>> getEventCacheObservable() {
        return Observable.from(itemMap.entrySet()).map(new Func1<Map.Entry<Long, String>, RxAdapterEvent<Long, String>>() {
            @Override
            public RxAdapterEvent<Long, String> call(Map.Entry<Long, String> longStringEntry) {
                return new RxAdapterEvent<>(RxAdapterEvent.TYPE.ADD, longStringEntry.getKey(), longStringEntry.getValue());
            }
        });
    }

    private final PublishSubject<RxAdapterEvent<Long, String>> eventPublishSubject = PublishSubject.create();

    public final Observable<RxAdapterEvent<Long, String>> getEventObservable() {
        return getEventCacheObservable().mergeWith(eventPublishSubject.doOnNext(new Action1<RxAdapterEvent<Long, String>>() {
            @Override
            public void call(RxAdapterEvent<Long, String> longStringRxAdapterEvent) {
                switch (longStringRxAdapterEvent.getType()) {
                    case ADD:
                        itemMap.put(longStringRxAdapterEvent.getKey(), longStringRxAdapterEvent.getValue());
                        break;
                    case REMOVE:
                        itemMap.remove(longStringRxAdapterEvent.getKey());
                        break;
                }
            }
        }));
    }

    public final Observer<RxAdapterEvent<Long, String>> getEventObserver() {
        return eventPublishSubject;
    }
}
