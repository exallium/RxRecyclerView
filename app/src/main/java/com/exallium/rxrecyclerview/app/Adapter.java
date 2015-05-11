package com.exallium.rxrecyclerview.app;

import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import com.exallium.rxrecyclerview.lib.RxAdapterEvent;
import com.exallium.rxrecyclerview.lib.RxRecyclerViewAdapter;
import rx.Observable;
import rx.subjects.PublishSubject;

import java.lang.ref.WeakReference;

public class Adapter extends RxRecyclerViewAdapter<Long, String, Adapter.ViewHolder> {

    public Adapter(Observable<RxAdapterEvent<Long, String>> observable) {
        super(observable);
    }

    @Override
    public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        return new ViewHolder(new TextView(parent.getContext()), getEventPublisher());
    }

    @Override
    public void onBindViewHolder(ViewHolder holder, Long key, String value) {
        holder.onBind(key, value);
    }

    public static class ViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener {

        private WeakReference<PublishSubject<RxAdapterEvent<Long, String>>> subject = new WeakReference<>(null);

        private Long key;
        private String value;

        public ViewHolder(View itemView, PublishSubject<RxAdapterEvent<Long, String>> subject) {
            super(itemView);
            this.subject = new WeakReference<>(subject);
            itemView.setOnClickListener(this);
        }

        public void onBind(Long key, String value) {
            ((TextView)itemView).setText(String.format("%d - %s", key, value));
            this.key = key;
            this.value = value;
        }

        @Override
        public void onClick(View v) {
            PublishSubject<RxAdapterEvent<Long, String>> subject = this.subject.get();
            if (subject != null)
                subject.onNext(new RxAdapterEvent<>(RxAdapterEvent.TYPE.REMOVE, key, value));
        }
    }
}
