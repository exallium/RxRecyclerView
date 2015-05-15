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

package com.exallium.rxrecyclerview.lib;

import rx.functions.Func1;

/**
 * Immutable Event for adapter change.
 *
 * If you would like to extend this object, the recommended way is to define a subclass with TYPE UNKNOWN.  You can then
 * filter the incoming observable within your adapter for this subclass.  Since you are required to define your types
 * when you declare your adapter, K and V will be known, so a non-parameterized Subclass is an option here.  If you
 * really need parameterized subclasses otherwise, my recommendation is to do something along the same lines as I did
 * here with Type, OR figure out the proper generics typing for the RX Java Methods to like your subscribers.
 *
 * You can listen to a custom subclass in your adapter constructor with observer.ofType(), or you can create a Filter
 * as I have done in this class an event's subtype against a type passed by constructor. (See RxRecyclerViewAdapter.java for
 * an example, and RxAdapterEvent.TypeFilter for an example)
 *
 * @param <K> The Key for this object (such as an ID number)
 * @param <V> The Value for this object (The object itself)
 */
public class RxAdapterEvent<K, V> {

    /**
     * Filter for RxAdapterEvents by Type.
     */
    public static final class TypeFilter implements Func1<RxAdapterEvent, Boolean> {

        private final TYPE type;

        public TypeFilter(TYPE type) {
            this.type = type;
        }

        @Override
        public Boolean call(RxAdapterEvent rxAdapterEvent) {
            return rxAdapterEvent.getType() == type;
        }
    }


    public enum TYPE {
        ADD,        // Used when Adding content to an RxRecyclerView
        REMOVE,     // Used when Removing content from an RxRecyclerView
        UNKNOWN     // Used for custom events
    }

    private final TYPE type;
    private final K key;
    private final V value;

    public RxAdapterEvent(TYPE type, K key, V value) {
        this.type = type;
        this.key = key;
        this.value = value;
    }

    /**
     * Returns the type of this event.  Finalized as this method is intended to be used
     * outside of the bounds of the type parameters.
     * @return The TYPE of this Event
     */
    public final TYPE getType() {
        return type;
    }

    /**
     * @return The Event's Key
     */
    public K getKey() {
        return key;
    }

    /**
     * @return The Event's Value
     */
    public V getValue() {
        return value;
    }

}