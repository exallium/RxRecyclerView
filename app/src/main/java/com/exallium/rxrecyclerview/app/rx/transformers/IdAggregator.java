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

package com.exallium.rxrecyclerview.app.rx.transformers;

import rx.Observable;
import rx.functions.Func1;
import rx.functions.Func2;

public class IdAggregator<T> implements Observable.Transformer<T, Long> {

    @Override
    public Observable<Long> call(Observable<T> tObservable) {
        return tObservable.map(new Func1<T, Long>() {
            @Override
            public Long call(T t) {
                return 1L;
            }
        }).scan(new Func2<Long, Long, Long>() {
            @Override
            public Long call(Long accumulator, Long t2) {
                return accumulator + t2;
            }
        });
    }
}
