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

import android.support.test.espresso.NoMatchingViewException;
import android.support.test.espresso.ViewAssertion;
import android.support.test.espresso.matcher.ViewMatchers;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.View;
import org.hamcrest.Matchers;
import org.hamcrest.StringDescription;

import static android.support.test.espresso.matcher.ViewMatchers.isAssignableFrom;

public class RecyclerViewAssertions {

    private static final String TAG = RecyclerViewAssertions.class.getSimpleName();

    public static ViewAssertion count(int n) {
        return new RecyclerViewCount(n);
    }

    static class RecyclerViewCount implements ViewAssertion {

        private final int n;

        private RecyclerViewCount(int n) {
            this.n = n;
        }

        @Override
        public void check(View view, NoMatchingViewException noViewException) {
            StringDescription description = new StringDescription();
            description.appendText("\'RecyclerView Count");
            if(noViewException != null) {
                description.appendText(String.format("\' check could not be performed because view \'%s\' was not found.\n", new Object[]{noViewException.getViewMatcherDescription()}));
                Log.e(RecyclerViewAssertions.TAG, description.toString());
                throw noViewException;
            } else if (!isAssignableFrom(RecyclerView.class).matches(view)) {
                description.appendText(String.format("\' check could not be performed because view \'%s\' is not a RecyclerView", view));
                Log.e(RecyclerViewAssertions.TAG, description.toString());
            } else if (((RecyclerView) view).getAdapter() == null) {
                description.appendText(String.format("\' check could not be performed because view \'%s\' has no adapter", view));
                Log.e(RecyclerViewAssertions.TAG, description.toString());
            } else {
                description.appendText(String.format("\' doesn\'t match the selected count.", new Object[0]));
                ViewMatchers.assertThat(description.toString(), n, Matchers.equalTo(((RecyclerView) view).getAdapter().getItemCount()));
            }
        }
    }

}
