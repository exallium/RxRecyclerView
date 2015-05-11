package com.exallium.rxrecyclerview.lib;

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
 *  * Remove an item (DELETE)
 *
 *  When the Controller (Activity or Fragment) is finished, it is good to send an OnComplete signal down through the
 *  Observer chain.  This should be done in your onDestroy method.  Note that this is done automatically by ViewObservable
 *
 *  The container is completely left up to the implementor.  You'll want something that is fast, and has easy access to
 *  Values via position or Key.  I suggest LongSparseArray, if you happen to be able to key things based off a Long
 *  (This is the case with a lot of DB managers, like Sugar)
 *
 * @param <K>   The type of Keys used for the items.
 * @param <V>   The kind of items we are adapting
 * @param <VH>  Custom View Holder
 */
public abstract class RxRecyclerViewAdapter<K, V, VH extends RecyclerView.ViewHolder> extends RecyclerView.Adapter<VH> {

    private static final String TAG = RxRecyclerViewAdapter.class.getSimpleName();

    private final PublishSubject<RxAdapterEvent<K, V>> eventPublisher = PublishSubject.create();

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

    /**
     * Allows for an Adapter to act upon it's own behalf.
     * If you want to customized this publisher, override this method and cache
     * your result.
     * @return The internal event publisher.
     */
    protected PublishSubject<RxAdapterEvent<K, V>> getEventPublisher() {
        return eventPublisher;
    }

    /**
     * Get the position in the adapter for the item keyed by Key
     * @param key The Key to look up
     * @return The position in the adapter of Key
     */
    protected abstract int getPositionByKey(K key);

    /**
     * Set the Value of the given position to the passed value
     * @param position The position to replace the item of
     * @param value The value to set the position to
     */
    protected abstract void setValueOfPosition(int position, V value);

    /**
     * Remove the given position from the list
     * @param position The position to remove
     */
    protected abstract void removeValueAt(int position);

    /**
     * Inserts a new item into the adapter.  Assumes this is added to the end of the list.
     * @param key The Key to add
     * @param value The Value to add
     * @return the position of the new value
     */
    protected abstract int insertKeyValuePair(K key, V value);

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
            int position = getPositionByKey(rxChangeEvent.getKey());
            setValueOfPosition(position, rxChangeEvent.getValue());
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
            int position = insertKeyValuePair(rxInsertEvent.getKey(), rxInsertEvent.getValue());
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
            int position = getPositionByKey(rxRemoveEvent.getKey());
            removeValueAt(position);
            notifyItemRemoved(position);
        }
    }
}
