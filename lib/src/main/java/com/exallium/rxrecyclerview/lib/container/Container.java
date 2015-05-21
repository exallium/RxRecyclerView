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

package com.exallium.rxrecyclerview.lib.container;

import com.exallium.rxrecyclerview.lib.RxAdapterEvent;

/**
 * Container for an RxRecyclerViewAdapter.  This contains several helper
 * methods to allow for easy implementation of custom containers that adhere
 * to what the adapter wants to see.
 *
 * We take both the Key and the Value here as we want to store a list of keys,
 * and then have a map refering keys to values.  The idea is that keys are set
 * in stone, but values change.
 *
 * See {@link com.exallium.rxrecyclerview.lib.container.impl.DefaultContainer} for more implementation details.
 *
 * @param <K> The Key type
 * @param <V> The Value type
 */
public interface Container<K, V> {

    /**
     * Get the RxAdapter Event at the given position
     * @param position The index of the event to get.
     * @return The RxAdapterEvent, or null.
     */
    RxAdapterEvent<K, V> get(int position);

    /**
     * Inserts a new RxAdapterEvent into the container.
     * Any sorting should be handled here.
     * @param event The event to add to the list.
     */
    void put(RxAdapterEvent<K, V> event);

    /**
     * Removes the given event from the container.
     * @param event The event to remove.
     */
    void remove(RxAdapterEvent<K, V> event);

    /**
     * Returns the positional element for a given Key.
     * @param key The key to check for
     * @return positional index of key, or -1
     */
    int indexOfKey(final K key);

    /**
     * @return the size of the underlying list.
     */
    int size();
}
