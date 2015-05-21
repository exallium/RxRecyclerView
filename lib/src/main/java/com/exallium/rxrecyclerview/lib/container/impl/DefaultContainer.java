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

package com.exallium.rxrecyclerview.lib.container.impl;

import android.util.Log;
import com.exallium.rxrecyclerview.lib.RxAdapterEvent;
import com.exallium.rxrecyclerview.lib.container.Container;

import java.util.ArrayList;
import java.util.Comparator;
import java.util.HashMap;

/**
 * Default Container for the RxRecyclerViewAdapter.  This is used if you
 * do not provide a custom one.  This has been written to try to get max
 * access performance out of it.
 *
 * Overriding classes should make sure to call super.  If you want to implement
 * your own Container, please implement Container instead of overriding this
 * class.
 *
 * Documentation is available in {@link Container}
 *
 * @param <K> The Key type
 * @param <V> The Value type
 */
public class DefaultContainer<K, V> implements Container<K, V> {
    private final HashMap<K, RxAdapterEvent<K, V>> dataMap = new HashMap<>();
    private final ArrayList<K> keyList = new ArrayList<>();

    private final Comparator<RxAdapterEvent<K, V>> eventComparator;

    public DefaultContainer() {
        this(null);
    }
    public DefaultContainer(Comparator<RxAdapterEvent<K, V>> eventComparator) {
        this.eventComparator = eventComparator;
    }

    @Override
    public RxAdapterEvent<K, V> get(int position) {
        return dataMap.get(keyList.get(position));
    }

    @Override
    public int indexOfKey(final K key) {
        return keyList.indexOf(key);
    }

    @Override
    public int size() {
        return keyList.size();
    }

    @Override
    public void remove(RxAdapterEvent<K, V> event) {
        keyList.remove(event.getKey());
        dataMap.remove(event.getKey());
    }

    @Override
    public void put(RxAdapterEvent<K, V> event) {
        int p = indexOfKey(event.getKey());
        if (p >= 0) {
            keyList.remove(event.getKey());
            if (eventComparator != null) {
                binaryAdd(event);
            } else {
                keyList.add(p, event.getKey());
            }
        } else if (eventComparator != null) {
            binaryAdd(event);
        } else {
            keyList.add(event.getKey());
        }
        dataMap.put(event.getKey(), event);
    }

    private void binaryAdd(RxAdapterEvent<K, V> event) {
        if (keyList.isEmpty())
            keyList.add(event.getKey());
        else {
            keyList.add(binarySearch(event, 0, keyList.size()), event.getKey());
        }
    }

    private int binarySearch(RxAdapterEvent<K, V> event, int start, int end) {
        final int segmentLength = end - start;
        final int normalCenter = segmentLength / 2;
        final int trueCenter = normalCenter + start;
        final RxAdapterEvent<K, V> centerEvent = dataMap.get(keyList.get(trueCenter));
        final int compare = eventComparator.compare(event, centerEvent);
        if (compare == 0) {
            return trueCenter;
        } else if (segmentLength == 1) {
            return trueCenter + ((compare > 0) ? 1 : 0);
        } else {
            return binarySearch(event, (compare > 0) ? trueCenter : start, (compare > 0) ? end : trueCenter);
        }
    }
}
