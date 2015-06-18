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

package com.exallium.rxrecyclerview.app;

import android.support.test.espresso.assertion.ViewAssertions;
import android.support.test.espresso.contrib.RecyclerViewActions;
import android.support.test.rule.ActivityTestRule;
import android.support.test.runner.AndroidJUnit4;
import android.test.suitebuilder.annotation.LargeTest;
import org.junit.Rule;
import org.junit.Test;
import org.junit.runner.RunWith;

import static android.support.test.espresso.Espresso.onView;
import static android.support.test.espresso.action.ViewActions.click;
import static android.support.test.espresso.matcher.ViewMatchers.withId;
import static android.support.test.espresso.matcher.ViewMatchers.withText;
import static org.hamcrest.Matchers.notNullValue;

/**
 * MainActivity Test.
 *
 * MainActivity contains an RxRecyclerView with Headers and Empty enabled.
 */
@RunWith(AndroidJUnit4.class)
@LargeTest
public class MainEspressoTest {

    @Rule
    public ActivityTestRule<MainActivity> mainActivityRule = new ActivityTestRule<>(MainActivity.class);

    private void addClick() {
        onView(withId(R.id.addButton)).perform(click());
    }

    private void itemClick(int position) {
        onView(withId(R.id.recyclerView))
                .perform(RecyclerViewActions.actionOnItemAtPosition(position, click()));
    }

    private void itemCount(int size) {
        onView(withId(R.id.recyclerView)).check(RecyclerViewAssertions.count(size));
    }

    @Test
    public void emptyItemTest() {
        itemCount(1);
    }

    @Test
    public void addItemsTest() {
        for (int i = 0; i < 10; i++)
            addClick();

        // 1. and 10. will be in the same group.
        itemCount(19);
    }

    @Test
    public void removeItemsTest() {
        addClick();
        itemCount(2);
        itemClick(1);
        itemCount(1);
    }

    @Test
    public void changeItemTest() {
        addClick();
        itemCount(2);
        onView(withText("1 - Item")).check(ViewAssertions.matches(notNullValue()));
        onView(withId(R.id.refreshButton)).perform(click());
        itemCount(2);
        onView(withText("1- Item")).check(ViewAssertions.doesNotExist());
    }

}
