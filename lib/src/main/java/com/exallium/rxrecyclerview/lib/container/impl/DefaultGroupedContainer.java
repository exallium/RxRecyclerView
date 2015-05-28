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

import com.exallium.rxrecyclerview.lib.RxAdapterEvent;
import com.exallium.rxrecyclerview.lib.RxRecyclerViewAdapter;
import com.exallium.rxrecyclerview.lib.container.Container;
import com.exallium.rxrecyclerview.lib.container.ContainerFactory;
import com.exallium.rxrecyclerview.lib.container.GroupComparator;

import java.util.ArrayList;
import java.util.Comparator;
import java.util.HashMap;
import java.util.List;

/**
 * Grouped Container
 * TODO: Handle how to add and remove group headers themselves.  If an item is removed and causes
 * TODO: group deletion, we should remove the group from the list as well.
 * TODO: This should be handled on the RxRecyclerViewAdapter side of things.
 *
 * @param <K> RxAdapterEvent Key
 * @param <V> RxAdapterEvent Value
 * @param <G> Group Key
 */
public class DefaultGroupedContainer<K, V, G> implements Container<K, V> {

    private final ArrayList<G> groupKeys = new ArrayList<>();
    private final GroupComparator<K, V, G> groupComparator;
    private final Comparator<RxAdapterEvent<K, V>> eventComparator;
    private final ContainerFactory<K, V, G> containerFactory;

    private final HashMap<G, Container<K, V>> groupMap = new HashMap<>();

    public DefaultGroupedContainer(GroupComparator<K, V, G> groupComparator) {
        this(groupComparator, null, new DefaultContainerFactory<K, V, G>());
    }

    public DefaultGroupedContainer(GroupComparator<K, V, G> groupComparator,
                                   Comparator<RxAdapterEvent<K, V>> eventComparator) {
        this(groupComparator, eventComparator, new DefaultContainerFactory<K, V, G>());
    }

    public DefaultGroupedContainer(GroupComparator<K, V, G> groupComparator,
                                   Comparator<RxAdapterEvent<K, V>> eventComparator,
                                   ContainerFactory<K, V, G> containerFactory) {
        this.groupComparator = groupComparator;
        this.eventComparator = eventComparator;
        this.containerFactory = containerFactory;
    }

    @Override
    public RxAdapterEvent<K, V> get(int position) {
        // Compute position into group offset and container offset
        return null;
    }

    @Override
    public void put(RxAdapterEvent<K, V> event) {
        G groupKey = groupComparator.getGroupKey(event);
        Container<K, V> container = groupMap.get(groupKey);
        if (container == null) {
            container = containerFactory.create(eventComparator);
            groupMap.put(groupKey, container);
        }
        container.put(event);
    }

    @Override
    public void remove(RxAdapterEvent<K, V> event) {
        G groupKey = groupComparator.getGroupKey(event);
        Container<K, V> container = groupMap.get(groupKey);
        container.remove(event);
        if (container.size() == 0) {
            groupMap.remove(groupKey);
            groupKeys.remove(groupKey);
        }
    }

    // TODO: Index calculation
    @Override
    public int indexOfKey(K key) {
        return 0;
    }

    @Override
    public int size() {
        int sum = 0;
        for (Container<K, V> group: groupMap.values()) {
            sum += group.size();
        }
        return sum;
    }

    // TODO binary insert into groupKeys
    private void insertGroupKey(G groupKey) {

    }

}
