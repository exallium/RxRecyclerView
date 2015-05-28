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

import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import com.exallium.rxrecyclerview.app.model.ObjectModel;
import com.exallium.rxrecyclerview.lib.element.Element;
import com.exallium.rxrecyclerview.lib.element.EventElement;
import com.exallium.rxrecyclerview.lib.event.Event;
import com.exallium.rxrecyclerview.lib.RxRecyclerViewAdapter;
import rx.Observable;

import java.util.Comparator;

public class Adapter extends RxRecyclerViewAdapter<Long, String, Adapter.ViewHolder> {

    public Adapter(Observable<EventElement<Long, String>> observable) {
        super(observable);
    }

    @Override
    public void onBindViewHolder(ViewHolder holder, Element<Event<Long, String>> element) {
        holder.onBind(element.getData().getKey(), element.getData().getValue());
    }

    @Override
    public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        return new ViewHolder(new TextView(parent.getContext()));
    }


    public static class ViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener {

        private Long key;
        private String value;

        public ViewHolder(View itemView) {
            super(itemView);
            itemView.setOnClickListener(this);
        }

        public void onBind(Long key, String value) {
            ((TextView)itemView).setText(String.format("%d - %s", key, value));
            this.key = key;
            this.value = value;
        }

        @Override
        public void onClick(View v) {
            ObjectModel.getInstance().getEventObserver().onNext(new Event<>(Event.TYPE.REMOVE, key, value));
        }
    }
}
