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
import com.exallium.rxrecyclerview.lib.container.Container;
import com.exallium.rxrecyclerview.lib.container.ContainerFactory;
import com.exallium.rxrecyclerview.lib.container.GroupComparator;

import java.util.Comparator;

public class DefaultContainerFactory<K, V, G> implements ContainerFactory<K, V, G> {
    @Override
    public Container<K, V> create(Comparator<RxAdapterEvent<K, V>> comparator) {
        return new DefaultContainer<>(comparator);
    }

    @Override
    public Container<K, V> create(GroupComparator<K, V, G> groupComparator,
                                  Comparator<RxAdapterEvent<K, V>> comparator) {
        if (groupComparator == null)
            return create(comparator);
        return new DefaultGroupedContainer<>(groupComparator, comparator);
    }
}
