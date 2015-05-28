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

package com.exallium.rxrecyclerview.lib;

import android.support.v7.widget.RecyclerView;
import android.util.Log;
import com.exallium.rxrecyclerview.lib.element.Element;
import com.exallium.rxrecyclerview.lib.element.EventElement;
import com.exallium.rxrecyclerview.lib.event.Event;
import rx.Observable;
import rx.Subscriber;
import rx.android.schedulers.AndroidSchedulers;

import java.util.TreeSet;

/**
 * Reactive View Adapter for RecyclerView
 * Currently Supported operations are:
 *  * Modify an existing item or Add a new Item (ADD)
 *  * Remove an item (REMOVE)
 *
 *  When the Controller (Activity or Fragment) is finished, it is good to send an OnComplete signal down through the
 *  Observer chain.  This should be done in your onDestroy method.  Note that this is done automatically by ViewObservable
 *
 * @param <K>   The type of Keys used for the items.
 * @param <V>   The kind of items we are adapting
 * @param <VH>  Custom View Holder
 */
public abstract class RxRecyclerViewAdapter<K, V, VH extends RecyclerView.ViewHolder> extends RecyclerView.Adapter<VH> {

    private static final String TAG = RxRecyclerViewAdapter.class.getSimpleName();

    private final TreeSet<Element<Event<K, V>>> treeSet;

    /**
     * Takes an observable of RxAdapterEvents.  See example in MainActivity in sample app.
     * It then splits this observable into one for each supported operation.  UNKNOWN is of course not supported but
     * can be listened for separately by the Programmer
     * @param observable The Stream of Events to observe and react to
     */
    public RxRecyclerViewAdapter(Observable<EventElement<K, V>> observable) {
        treeSet = new TreeSet<>();
        Observable<EventElement<K, V>> androidThreadObservable = observable.observeOn(AndroidSchedulers.mainThread());
        androidThreadObservable.subscribe(new RxSubscriber());
    }

    private void onError(Class<?> clazz, Throwable e) {
        Log.d(TAG, "An error happened in " + clazz.getSimpleName(), e);
    }

    @Override
    public final void onBindViewHolder(VH holder, int position) {
        EventElement<K,V> element = getItemAt(position);
        onBindViewHolder(holder, element);
    }

    /**
     * Binds a ViewHolder to the given Key/Value pair
     * @param holder    The ViewHolder to bind to
     */
    public abstract void onBindViewHolder(VH holder, Element<Event<K, V>> element);

    @Override
    public int getItemCount() {
        return treeSet.size();
    }

    @Override
    public int getItemViewType(int position) {
        return getItemAt(position).getViewType();
    }

    // TODO: Performance
    protected EventElement<K, V> getItemAt(int position) {
        return (EventElement<K, V>) treeSet.toArray()[position];
    }

    private class RxSubscriber extends Subscriber<EventElement<K, V>> {

        @Override
        public void onCompleted() {
            unsubscribe();
        }

        @Override
        public void onError(Throwable e) {
            RxRecyclerViewAdapter.this.onError(this.getClass(), e);
        }

        @Override
        public void onNext(EventElement<K, V> rxEvent) {
            switch (rxEvent.getData().getType()) {
                case ADD:
                    if (treeSet.add(rxEvent)) {
                        notifyItemInserted(treeSet.headSet(rxEvent).size());
                    } else {
                        notifyItemChanged(treeSet.headSet(rxEvent).size());
                    }
                    break;
                case REMOVE:
                    int index = treeSet.headSet(rxEvent).size();
                    if (treeSet.remove(rxEvent)) {
                        notifyItemRemoved(index);
                    }
                    break;
            }
        }
    }
}
