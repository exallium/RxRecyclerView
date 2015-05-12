# RxRecyclerViewAdapter

Crazy easy to use RecyclerView Adapter for Reactive Applications

## Interface

* RxRecyclerViewAdapter.onCreateViewHolder is the same as RecyclerView
* RxRecyclerViewAdapter.onBindViewHolder gives you the Key and Value of the item
  you're binding to.
* RxAdapterEvent is Immutable and takes an RxAdapterEvent.TYPE, Key, and Value.
* RxAdapterEvent also contains an UNKNOWN type and is overridable for custom
  processing.
* RxRecyclerViewAdapter provides a PublishSubject via getEventPublisher for easy
  Event publishing from within the Adapter (Item clicks)

## Creating an Adapter

You need to have an Observable that you've merged all of your event emitters
into.  You can also utilize getEventPublisher.onNext, but this isn't really
recommended.  In the sample app, I show merging ViewObservables together to
create and update items!

## Examples

Are available in the app module!

## Licensing

This work is (C) under the MIT License.
