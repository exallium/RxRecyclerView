package com.exallium.rxrecyclerview.lib;

import android.support.v7.widget.RecyclerView;
import android.util.LongSparseArray;
import rx.Observable;

/**
 * Simple Adapter implementation backed by LongSparseArray.
 * @param <V>  The type of Model we are adapting
 * @param <VH> The custom view holder
 */
public abstract class RxSimpleAdapter<V, VH extends RecyclerView.ViewHolder> extends RxRecyclerViewAdapter<Long, V, VH> {

    private final LongSparseArray<V> container = new LongSparseArray<>();

    public RxSimpleAdapter(Observable<RxAdapterEvent<Long, V>> observable) {
        super(observable);
    }

    @Override
    protected final int getPositionByKey(Long key) {
        return container.indexOfKey(key);
    }

    @Override
    protected final void setValueOfPosition(int position, V value) {
        container.setValueAt(position, value);
    }

    @Override
    protected final void removeValueAt(int position) {
        container.removeAt(position);
    }

    @Override
    protected final int insertKeyValuePair(Long key, V value) {
        container.put(key, value);
        return container.indexOfKey(key);
    }

    @Override
    public final void onBindViewHolder(VH holder, int position) {
        onBindViewHolder(holder, container.keyAt(position), container.valueAt(position));
    }

    /**
     * Utilize this method to update your ViewHolder objects.  The ViewHolder
     * contains positional information.
     * @param holder The holder to bind
     * @param key    The Key value from container
     * @param value  The Value value from container
     */
    public abstract void onBindViewHolder(VH holder, Long key, V value);

    @Override
    public int getItemCount() {
        return container.size();
    }
}
