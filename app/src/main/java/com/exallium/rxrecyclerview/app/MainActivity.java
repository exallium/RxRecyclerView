package com.exallium.rxrecyclerview.app;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.MotionEvent;
import android.widget.Button;
import butterknife.ButterKnife;
import butterknife.InjectView;
import com.exallium.rxrecyclerview.lib.RxAdapterEvent;
import rx.Observable;
import rx.android.view.OnClickEvent;
import rx.android.view.ViewObservable;
import rx.functions.Action1;
import rx.functions.Func1;
import rx.functions.Func2;
import rx.subjects.PublishSubject;

import java.util.Random;

public class MainActivity extends Activity {

    @InjectView(R.id.refreshButton)
    Button refreshButton;

    @InjectView(R.id.recyclerView)
    RecyclerView recyclerView;

    @InjectView(R.id.addButton)
    Button addButton;

    @InjectView(R.id.anotherActivityButton)
    Button anotherActivityButton;

    private Observable<RxAdapterEvent<Long, String>> adapterEventObservable;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        ButterKnife.inject(this);

        recyclerView.setLayoutManager(new LinearLayoutManager(this));
        final Random random = new Random(System.currentTimeMillis());

        Observable<OnClickEvent> addClicks = ViewObservable.clicks(addButton);
        Observable<Long> idAggregator = addClicks.map(new Func1<OnClickEvent, Long>() {
            @Override
            public Long call(OnClickEvent onClickEvent) {
                return 1L;
            }
        }).scan(new Func2<Long, Long, Long>() {
            @Override
            public Long call(Long l1, Long l2) {
                return l1 + l2;
            }
        });

        Observable<RxAdapterEvent<Long, String>> createEvents = Observable.zip(addClicks, idAggregator, new Func2<OnClickEvent, Long, RxAdapterEvent<Long, String>>() {
            @Override
            public RxAdapterEvent<Long, String> call(OnClickEvent onClickEvent, Long key) {
                return new RxAdapterEvent<>(RxAdapterEvent.TYPE.INSERT, key, "Item");
            }
        });

        Observable<RxAdapterEvent<Long, String>> updateEvents = ViewObservable.clicks(refreshButton).map(new Func1<OnClickEvent, RxAdapterEvent<Long, String>>() {
            @Override
            public RxAdapterEvent<Long, String> call(OnClickEvent onClickEvent) {
                // send a new event saying position 0 string becomes random number
                return new RxAdapterEvent<>(RxAdapterEvent.TYPE.CHANGE, 1L, Integer.toString(random.nextInt()));
            }
        });

        adapterEventObservable = Observable.merge(createEvents, updateEvents);

        Adapter adapter = new Adapter(adapterEventObservable);
        recyclerView.setAdapter(adapter);

        ViewObservable.clicks(anotherActivityButton).forEach(new Action1<OnClickEvent>() {
            @Override
            public void call(OnClickEvent onClickEvent) {
                Intent i = new Intent();
                i.setClass(MainActivity.this, AnotherActivity.class);
                startActivity(i);
            }
        });
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
    }
}
