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

/**
 * An Event Element contains an Event and a View Type for on screen rendering.  It is comparable to other
 * elements for sorting.
 *
 * There are 4 types of Elements.  Data elements, Headers, Footers, and Empty.  Each has a mask, so that
 * you can subclass and create new Elements.
 *
 * @param <K>   The Event Key
 * @param <V>   The Event Value
 */
public class EventElement<K, V> implements Comparable<EventElement<K, V>> {

    // Max supported viewtypes per mask is 1024, including the default
    public static final int MASK_SHIFT = 10;

    public static final int DATA_MASK = 0;      // 00
    public static final int HEADER_MASK = 1;    // 01
    public static final int FOOTER_MASK = 2;    // 10
    public static final int EMPTY_MASK = 3;     // 11

    private final Event<K, V> event;
    private final GroupComparator<K, V> eventGroupComparator;

    public EventElement(Event<K, V> event, GroupComparator<K, V> groupComparator) {
        this.event = event;
        this.eventGroupComparator = groupComparator;
    }

    /**
     * @return The Group Key for the Wrapped Event
     */
    public final String getGroup() {
        return eventGroupComparator.getGroupKey(event);
    }

    /**
     * @return The Datatype for this Element. See Class Documentation
     */
    public int getViewType() {
        return DATA_MASK;
    }

    /**
     * @return The wrapped Event
     */
    public final Event<K, V> getData() {
        return event;
    }

    /**
     * Compare us to another Element
     * @param another The other element
     * @return Comparator result (-1, 0, 1)
     */
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
