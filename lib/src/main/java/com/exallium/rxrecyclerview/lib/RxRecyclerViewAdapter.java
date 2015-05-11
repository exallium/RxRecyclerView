package com.exallium.rxrecyclerview.lib;

import android.support.v4.util.SimpleArrayMap;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import rx.Observable;
import rx.Subscriber;
import rx.android.schedulers.AndroidSchedulers;
import rx.subjects.PublishSubject;

/**
 * Reactive View Adapter for RecyclerView
 * Currently Supported operations are:
 *  * Add a new item (INSERT)
 *  * Modify an existing item (CHANGE)
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
    private final SimpleArrayMap<K, V> container = new SimpleArrayMap<>();

    /**
     * Takes an observable of RxAdapterEvents.  See example in MainActivity in sample app.
     * It then splits this observable into one for each supported operation.  UNKNOWN is of course not supported but
     * can be listened for separately by the Programmer
     * @param observable The Stream of Events to observe and react to
     */
    public RxRecyclerViewAdapter(Observable<RxAdapterEvent<K, V>> observable) {
        Observable<RxAdapterEvent<K, V>> androidThreadObservable = observable.mergeWith(eventPublisher).subscribeOn(AndroidSchedulers.mainThread());
        androidThreadObservable.filter(new RxAdapterEvent.TypeFilter<K, V>(RxAdapterEvent.TYPE.CHANGE)).subscribe(new RxChangeSubscriber());
        androidThreadObservable.filter(new RxAdapterEvent.TypeFilter<K, V>(RxAdapterEvent.TYPE.INSERT)).subscribe(new RxInsertSubscriber());
        androidThreadObservable.filter(new RxAdapterEvent.TypeFilter<K, V>(RxAdapterEvent.TYPE.REMOVE)).subscribe(new RxRemoveSubscriber());
    }

    private void onError(Class<?> clazz, Throwable e) {
        if (BuildConfig.DEBUG)
            Log.d(TAG, "An error happened in " + clazz.getSimpleName(), e);
    }

    @Override
    public void onBindViewHolder(VH holder, int position) {
        onBindViewHolder(holder, container.keyAt(position), container.valueAt(position));
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

    private class RxChangeSubscriber extends Subscriber<RxAdapterEvent<K, V>> {

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
            container.put(rxChangeEvent.getKey(), rxChangeEvent.getValue());
            notifyItemChanged(position);
        }
    }

    private class RxInsertSubscriber extends Subscriber<RxAdapterEvent<K, V>> {

        @Override
        public void onCompleted() {
            unsubscribe();
        }

        @Override
        public void onError(Throwable e) {
            RxRecyclerViewAdapter.this.onError(this.getClass(), e);
        }

        @Override
        public void onNext(RxAdapterEvent<K, V> rxInsertEvent) {
            container.put(rxInsertEvent.getKey(), rxInsertEvent.getValue());
            int position = container.indexOfKey(rxInsertEvent.getKey());
            notifyItemInserted(position);
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
            container.removeAt(position);
            notifyItemRemoved(position);
        }
    }
}
