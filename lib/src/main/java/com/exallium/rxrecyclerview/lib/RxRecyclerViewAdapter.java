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
import android.util.ArrayMap;
import android.util.Log;
import rx.Observable;
import rx.Subscriber;
import rx.android.schedulers.AndroidSchedulers;
import rx.subjects.PublishSubject;

import java.util.ArrayList;
import java.util.HashMap;

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

    private final PublishSubject<RxAdapterEvent<K, V>> eventPublisher = PublishSubject.create();

    private final Container container = new Container();
    private final class Container {
        private final HashMap<K, RxAdapterEvent<K, V>> dataMap = new HashMap<>();
        private final ArrayList<K> keyList = new ArrayList<>();

        public RxAdapterEvent<K, V> get(int position) {
            return dataMap.get(keyList.get(position));
        }

        public int indexOfKey(final K key) {
            return keyList.indexOf(key);
        }

        public int size() {
            return keyList.size();
        }

        public void remove(RxAdapterEvent<K, V> event) {
            keyList.remove(event.getKey());
            dataMap.remove(event.getKey());
        }

        public void put(RxAdapterEvent<K, V> event) {
            int p = indexOfKey(event.getKey());
            if (p >= 0) {
                keyList.remove(event.getKey());
                keyList.add(p, event.getKey());
            } else {
                keyList.add(event.getKey());
            }
            dataMap.put(event.getKey(), event);
        }
    }

    /**
     * Takes an observable of RxAdapterEvents.  See example in MainActivity in sample app.
     * It then splits this observable into one for each supported operation.  UNKNOWN is of course not supported but
     * can be listened for separately by the Programmer
     * @param observable The Stream of Events to observe and react to
     */
    public RxRecyclerViewAdapter(Observable<RxAdapterEvent<K, V>> observable) {
        Observable<RxAdapterEvent<K, V>> androidThreadObservable = observable.mergeWith(eventPublisher).subscribeOn(AndroidSchedulers.mainThread());
        androidThreadObservable.filter(new RxAdapterEvent.TypeFilter<K, V>(RxAdapterEvent.TYPE.ADD)).subscribe(new RxAddSubscriber());
        androidThreadObservable.filter(new RxAdapterEvent.TypeFilter<K, V>(RxAdapterEvent.TYPE.REMOVE)).subscribe(new RxRemoveSubscriber());
    }

    private void onError(Class<?> clazz, Throwable e) {
        Log.d(TAG, "An error happened in " + clazz.getSimpleName(), e);
    }

    @Override
    public void onBindViewHolder(VH holder, int position) {
        RxAdapterEvent<K,V> rxAdapterEvent = container.get(position);
        onBindViewHolder(holder, rxAdapterEvent.getKey(), rxAdapterEvent.getValue());
    }

    public abstract void onBindViewHolder(VH holder, K key, V value);

    @Override
    public int getItemCount() {
        return container.size();
    }

    /**
     * Allows for an Adapter to act upon it's own behalf.
     * If you want to customized this publisher, override this method and cache
     * your result.
     * @return The internal event publisher.
     */
    protected PublishSubject<RxAdapterEvent<K, V>> getEventPublisher() {
        return eventPublisher;
    }

    private class RxAddSubscriber extends Subscriber<RxAdapterEvent<K, V>> {

        @Override
        public void onCompleted() {
            unsubscribe();
        }

        @Override
        public void onError(Throwable e) {
            RxRecyclerViewAdapter.this.onError(this.getClass(), e);
        }

        @Override
        public void onNext(RxAdapterEvent<K, V> rxChangeEvent) {
            int position = container.indexOfKey(rxChangeEvent.getKey());
            container.put(rxChangeEvent);
            if (position >= 0)
                notifyItemChanged(position);
            else
                notifyItemInserted(container.indexOfKey(rxChangeEvent.getKey()));
        }
    }

    private class RxRemoveSubscriber extends Subscriber<RxAdapterEvent<K, V>> {

        @Override
        public void onCompleted() {
            unsubscribe();
        }

        @Override
        public void onError(Throwable e) {
            RxRecyclerViewAdapter.this.onError(this.getClass(), e);
        }

        @Override
        public void onNext(RxAdapterEvent<K, V> rxRemoveEvent) {
            int position = container.indexOfKey(rxRemoveEvent.getKey());
            if (position >= 0) {
                container.remove(rxRemoveEvent);
                notifyItemRemoved(position);
            }
        }
    }
}
