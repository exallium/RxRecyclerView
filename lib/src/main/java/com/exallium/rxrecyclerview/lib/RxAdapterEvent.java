package com.exallium.rxrecyclerview.lib;

import rx.functions.Func1;

/**
 * Event for adapter change.
 * @param <K> The Key for this object (such as an ID number)
 * @param <V> The Value for this object (The object itself)
 */
public class RxAdapterEvent<K, V> {

    public static final class TypeFilter<K, V> implements Func1<RxAdapterEvent<K, V>, Boolean> {

        private final TYPE type;

        public TypeFilter(TYPE type) {
            this.type = type;
        }

        @Override
        public Boolean call(RxAdapterEvent<K, V> rxAdapterEvent) {
            return rxAdapterEvent.getType() == type;
        }
    }


    public enum TYPE {
        INSERT,
        CHANGE,
        REMOVE,
        UNKNOWN
    }

    private final TYPE type;
    private final K key;
    private final V value;

    public RxAdapterEvent(TYPE type, K key, V value) {
        this.type = type;
        this.key = key;
        this.value = value;
    }

    public TYPE getType() {
        return type;
    }

    public K getKey() {
        return key;
    }

    public V getValue() {
        return value;
    }

}