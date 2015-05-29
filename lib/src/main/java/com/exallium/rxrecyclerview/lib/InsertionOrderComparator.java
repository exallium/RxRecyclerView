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

import com.exallium.rxrecyclerview.lib.event.Event;

/**
 * Group Comparator that allows for easy "default" insertion order.
 * I recommend using System.currentTimeMillis as your Key value.
 * This will guarentee insertion order.
 *
 * Headers and footers will not and are not designed to work with this.  This is
 * only for those times when you don't want sorted data to be displayed.
 *
 * @param <V> The Value of data we are inserting
 */
public class InsertionOrderComparator<V> implements GroupComparator<Long, V> {
    @Override
    public final String getGroupKey(Event<Long, V> event) {
        return event.getKey().toString();
    }

    /**
     * Returns an event with -1L as Key and null as value.
     * @param eventType ADD or REMOVE
     * @return an event to throw into an EmptyElement
     */
    @Override
    public Event<Long, V> getEmptyEvent(Event.TYPE eventType) {
        return new Event<>(eventType, -1L, null);
    }

    /**
     * Default compare.  Override for reverse or what have you.
     * @param lhs Event we have
     * @param rhs Event we compare to
     * @return -1, 0, 1 based off comparison.
     */
    @Override
    public int compare(Event<Long, V> lhs, Event<Long, V> rhs) {
        return lhs.getKey().compareTo(rhs.getKey());
    }
}
