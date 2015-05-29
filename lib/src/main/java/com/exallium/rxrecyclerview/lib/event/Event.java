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

package com.exallium.rxrecyclerview.lib.event;

/**
 * Immutable Event for adapter change.
 *
 * @param <K> The Key for this object (such as an ID number)
 * @param <V> The Value for this object (The object itself)
 */
public class Event<K, V> {

    public enum TYPE {
        ADD,        // Used when Adding content to an RxRecyclerView
        REMOVE,     // Used when Removing content from an RxRecyclerView
    }

    private final TYPE type;
    private final K key;
    private final V value;

    public Event(TYPE type, K key, V value) {
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