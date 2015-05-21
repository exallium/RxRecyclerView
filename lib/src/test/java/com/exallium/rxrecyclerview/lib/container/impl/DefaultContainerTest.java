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
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.junit.runners.JUnit4;

import java.util.*;

import static org.junit.Assert.assertEquals;

@RunWith(JUnit4.class)
public class DefaultContainerTest {

    // Comparator to return values in Natural order by Value
    private static final Comparator<RxAdapterEvent<Long, String>> abcComparator = new Comparator<RxAdapterEvent<Long, String>>() {
        @Override
        public int compare(RxAdapterEvent<Long, String> lhs, RxAdapterEvent<Long, String> rhs) {
            return lhs.getValue().toLowerCase().compareTo(rhs.getValue().toLowerCase());
        }
    };

    private Random random = new Random(System.currentTimeMillis());
    private Container<Long, String> defaultContainer;

    @Before
    public void setUp() {
        defaultContainer = new DefaultContainer<>(abcComparator);
    }

    @Test
    public void testNoComparator() {
        Container<Long, String> container = new DefaultContainer<>();

        // Add 10 elements to the list.
        Set<Long> set = new LinkedHashSet<>();
        for (int i = 0; i < 10; i++) {

            // Get the next unique long
            long l;
            do {
                l = random.nextLong();
            } while (!set.add(l));

            container.put(new RxAdapterEvent<>(RxAdapterEvent.TYPE.ADD, l, Long.toString(random.nextLong())));
        }

        // Assert all items successfully entered.
        assertEquals(10, container.size());

        // Assert we have maintained insertion order
        Long[] longs = new Long[10];
        set.toArray(longs);
        for (int i = 0; i < 10; i++) {
            assertEquals(longs[i], container.get(i).getKey());
        }

        // Remove all of the items from the list
        for (Long l : set) {
            container.remove(new RxAdapterEvent<>(RxAdapterEvent.TYPE.REMOVE, l, ""));
        }

        assertEquals(0, container.size());
    }

    @Test
    public void testSequentialComparatorInsert() {
        String[] data = new String[] {"A", "B", "C", "D", "E", "F", "G"};
        for (int i = 0; i < data.length; i++) {
            defaultContainer.put(new RxAdapterEvent<>(RxAdapterEvent.TYPE.ADD, (long) i, data[i]));
        }
        assertEquals(data.length, defaultContainer.size());

        for (int i = 0; i < data.length; i++) {
            assertEquals(defaultContainer.get(i).getValue(), data[i]);
        }
    }

    @Test
    public void testNonSequentialComparatorInsert() {
        String[] data = new String[] {"B", "D", "A", "C", "G", "F", "E"};
        for (int i = 0; i < data.length; i++) {
            defaultContainer.put(new RxAdapterEvent<>(RxAdapterEvent.TYPE.ADD, (long) i, data[i]));
        }
        assertEquals(data.length, defaultContainer.size());

        List<String> sorted = Arrays.asList(data);
        Collections.sort(sorted);
        for (int i = 0; i < data.length; i++) {
            assertEquals(defaultContainer.get(i).getValue(), sorted.get(i));
        }
    }

    @Test
    public void testInsertSameKey() {
        defaultContainer.put(new RxAdapterEvent<>(RxAdapterEvent.TYPE.ADD, 5L, "hi"));
        assertEquals(1, defaultContainer.size());
        assertEquals(defaultContainer.get(0).getValue(), "hi");
        defaultContainer.put(new RxAdapterEvent<>(RxAdapterEvent.TYPE.ADD, 5L, "hello"));
        assertEquals(1, defaultContainer.size());
        assertEquals(defaultContainer.get(0).getValue(), "hello");
    }

}
