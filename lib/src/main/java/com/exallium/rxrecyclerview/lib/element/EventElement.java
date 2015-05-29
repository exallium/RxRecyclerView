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

package com.exallium.rxrecyclerview.lib.element;

import com.exallium.rxrecyclerview.lib.GroupComparator;
import com.exallium.rxrecyclerview.lib.event.Event;

public class EventElement<K, V> implements Comparable<EventElement<K, V>> {

    // Max supported viewtypes per mask is 1024, including the default
    public static final int MASK_SHIFT = 10;

    public static final int DATA_MASK = 0;      // 00
    public static final int HEADER_MASK = 1;    // 01
    public static final int FOOTER_MASK = 2;    // 10
    public static final int EMPTY_MASK = 3;     // 11

    private final Event<K, V> event;
    private final GroupComparator<Event<K, V>> eventGroupComparator;

    public EventElement(Event<K, V> event, GroupComparator<Event<K, V>> groupComparator) {
        this.event = event;
        this.eventGroupComparator = groupComparator;
    }

    public String getGroup() {
        return eventGroupComparator.getGroupKey(event);
    }

    public int getViewType() {
        return DATA_MASK;
    }

    public Event<K, V> getData() {
        return event;
    }


    public int compareTo(EventElement<K, V> another) {
        int groupComparison = getGroup().compareTo(another.getGroup());
        if (groupComparison != 0) {
            return groupComparison;
        }

        // We are always after header elements and before footer elements
        if (another.getViewType() >> MASK_SHIFT == HEADER_MASK)
            return 1;
        if (another.getViewType() >> MASK_SHIFT == FOOTER_MASK)
            return -1;

        return eventGroupComparator.compare(getData(), another.getData());
    }
}
