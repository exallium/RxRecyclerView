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

package com.exallium.rxrecyclerview.lib.operators;

import com.exallium.rxrecyclerview.lib.GroupComparator;
import com.exallium.rxrecyclerview.lib.element.*;
import com.exallium.rxrecyclerview.lib.event.Event;
import rx.Observable;
import rx.Subscriber;

import java.util.HashMap;
import java.util.Map;
import java.util.TreeMap;

public class ElementGenerationOperator<K, V> implements Observable.Operator<EventElement<K, V>, Event<K, V>> {

    Map<String, Integer> groupMap = new HashMap<>();
    private final GroupComparator<Event<K, V>> groupComparator;
    private final boolean hasHeader;
    private final boolean hasFooter;

    public ElementGenerationOperator(Builder builder) {
        this.groupComparator = builder.groupComparator;
        this.hasFooter = builder.hasFooter;
        this.hasHeader = builder.hasHeader;
    }

    public static class Builder<K, V> {
        private final GroupComparator<Event<K, V>> groupComparator;
        private boolean hasHeader = false;
        private boolean hasFooter = false;

        public Builder(GroupComparator<Event<K, V>> groupComparator) {
            this.groupComparator = groupComparator;
        }

        public Builder<K, V> hasHeader(boolean hasHeader) {
            this.hasHeader = hasHeader;
            return this;
        }

        public Builder<K, V> hasFooter(boolean hasFooter) {
            this.hasFooter = hasFooter;
            return this;
        }

        public ElementGenerationOperator<K, V> build() {
            return new ElementGenerationOperator<>(this);
        }
    }


    @Override
    public Subscriber<? super Event<K, V>> call(final Subscriber<? super EventElement<K, V>> subscriber) {
        return new Subscriber<Event<K, V>>() {
            @Override
            public void onCompleted() {
                if (!subscriber.isUnsubscribed())
                    subscriber.onCompleted();
            }

            @Override
            public void onError(Throwable e) {
                if (!subscriber.isUnsubscribed())
                    subscriber.onError(e);
            }

            @Override
            public void onNext(Event<K, V> event) {
                if (!subscriber.isUnsubscribed()) {
                    final EventElement<K, V> element = new EventElement<>(event, groupComparator);
                    subscriber.onNext(element);
                    final String groupKey = element.getGroup();
                    final int groupSize = groupMap.containsKey(groupKey) ? groupMap.get(groupKey) : 0;

                    switch (event.getType()) {
                        case ADD:
                            groupMap.put(groupKey, groupSize + 1);
                            if (groupSize == 0) {
                                if (hasHeader) subscriber.onNext(new HeaderElement<>(event, groupComparator));
                                if (hasFooter) subscriber.onNext(new FooterElement<>(event, groupComparator));
                            }
                            break;
                        case REMOVE:
                            groupMap.put(groupKey, Math.max(0, groupSize - 1));
                            if (groupMap.get(groupKey) == 0) {
                                groupMap.remove(groupKey);
                                if (hasHeader) subscriber.onNext(new HeaderElement<>(event, groupComparator));
                                if (hasFooter) subscriber.onNext(new FooterElement<>(event, groupComparator));
                            }
                            break;
                    }


                }
            }
        };
    }
}
