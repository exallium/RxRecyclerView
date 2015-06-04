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
import android.util.Log;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import com.exallium.rxrecyclerview.app.model.ObjectModel;
import com.exallium.rxrecyclerview.lib.element.EventElement;
import com.exallium.rxrecyclerview.lib.event.Event;
import com.exallium.rxrecyclerview.lib.RxRecyclerViewAdapter;
import rx.Observable;

public class Adapter extends RxRecyclerViewAdapter<Long, String, Adapter.ViewHolder> {
    private static final String TAG = Adapter.class.getSimpleName();

    public Adapter(Observable<EventElement<Long, String>> observable) {
        super(observable);
    }

    @Override
    public void onBindViewHolder(ViewHolder holder, EventElement<Long, String> element) {
        final String dataString;
        switch (element.getViewType() >> EventElement.MASK_SHIFT) {
            case EventElement.EMPTY_MASK:
                dataString = "EMPTY";
                break;
            case EventElement.HEADER_MASK:
                dataString = "HEADER";
                break;
            case EventElement.FOOTER_MASK:
                dataString = "FOOTER";
                break;
            default:
                dataString = element.getData().getValue();
                break;
        }
        holder.onBind(element.getData().getKey(), dataString, element.getViewType() >> EventElement.MASK_SHIFT);
    }

    @Override
    public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        return new ViewHolder(new TextView(parent.getContext()));
    }

    @Override
    protected void postProcessElement(EventElement<Long, String> element) {
        Log.d(TAG, String.format("POST PROCESS ELEMENT %d - %s",
                element.getData().getKey(), element.getData().getValue()));
    }

    @Override
    protected void preProcessElement(EventElement<Long, String> element) {
        Log.d(TAG, String.format("PRE PROCESS ELEMENT %d - %s",
                element.getData().getKey(), element.getData().getValue()));
        final int indexOf = getIndexOf(element);
        if (indexOf != -1) {
            switch (element.getData().getType()) {
                case ADD:
                    final EventElement<Long, String> oldElement = getItemAt(indexOf);
                    Log.d(TAG, String.format("REPLACE ELEMENT %d - %s",
                            oldElement.getData().getKey(), oldElement.getData().getValue()));
                    break;
                case REMOVE:
                    Log.d(TAG, String.format("REMOVE ELEMENT %d - %s",
                            element.getData().getKey(), element.getData().getValue()));
                    break;
                default:
            }
        }
    }

    public static class ViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener {

        private Long key;
        private String value;

        public ViewHolder(View itemView) {
            super(itemView);
        }

        public void onBind(Long key, String value, int viewMask) {
            ((TextView)itemView).setText(String.format("%d - %s", key, value));
            this.key = key;
            this.value = value;
            itemView.setOnClickListener(viewMask == EventElement.DATA_MASK ? this : null);
        }

        @Override
        public void onClick(View v) {
            ObjectModel.getInstance().getEventObserver().onNext(new Event<>(Event.TYPE.REMOVE, key, value));
        }
    }
}
