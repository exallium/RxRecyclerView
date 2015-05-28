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

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.widget.Button;
import butterknife.ButterKnife;
import butterknife.InjectView;
import com.exallium.rxrecyclerview.app.model.ObjectModel;
import com.exallium.rxrecyclerview.app.rx.transformers.IdAggregator;
import com.exallium.rxrecyclerview.lib.GroupComparator;
import com.exallium.rxrecyclerview.lib.event.Event;
import com.exallium.rxrecyclerview.lib.operators.ElementGenerationOperator;
import rx.Observable;
import rx.android.view.OnClickEvent;
import rx.android.view.ViewObservable;
import rx.functions.Action1;
import rx.functions.Func1;
import rx.functions.Func2;

import java.util.Comparator;
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

    private final GroupComparator<Event<Long, String>> adapterComparator = new GroupComparator<Event<Long, String>>() {
        @Override
        public String getGroupKey(Event<Long, String> longStringEvent) {
            return longStringEvent.getKey().toString().substring(0,1);
        }

        @Override
        public int compare(Event<Long, String> lhs, Event<Long, String> rhs) {
            return lhs.getKey().compareTo(rhs.getKey());
        }
    };

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        ButterKnife.inject(this);

        recyclerView.setLayoutManager(new LinearLayoutManager(this));
        final Random random = new Random(System.currentTimeMillis());

        Observable<OnClickEvent> addClicks = ViewObservable.clicks(addButton);
        Observable<Long> idAggregator = addClicks.compose(new IdAggregator<OnClickEvent>());

        Observable<Event<Long, String>> createEvents = Observable.zip(addClicks, idAggregator, new Func2<OnClickEvent, Long, Event<Long, String>>() {
            @Override
            public Event<Long, String> call(OnClickEvent onClickEvent, Long key) {
                return new Event<>(Event.TYPE.ADD, key, "Item");
            }
        });

        Observable<Event<Long, String>> updateEvents = ViewObservable.clicks(refreshButton).map(new Func1<OnClickEvent, Event<Long, String>>() {
            @Override
            public Event<Long, String> call(OnClickEvent onClickEvent) {
                // send a new event saying position 0 string becomes random number
                return new Event<>(Event.TYPE.ADD, 1L, Integer.toString(random.nextInt()));
            }
        });

        Observable.merge(createEvents, updateEvents).subscribe(ObjectModel.getInstance().getEventObserver());

        Adapter adapter = new Adapter(ObjectModel
                .getInstance()
                .getEventObservable()
                .lift(new ElementGenerationOperator.Builder<>(adapterComparator)
                        .hasHeader(true).build()));
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

}
