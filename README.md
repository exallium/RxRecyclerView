# RxRecyclerViewAdapter

Crazy easy to use RecyclerView Adapter for Reactive Applications

## Interface

* RxRecyclerViewAdapter.onCreateViewHolder is the same as RecyclerView
* RxRecyclerViewAdapter.onBindViewHolder gives you the Key and Value of the item
  you're binding to.
* RxAdapterEvent is Immutable and takes an RxAdapterEvent.TYPE, Key, and Value.
* RxAdapterEvent also contains an UNKNOWN type and is overridable for custom
  processing.

## Creating an Adapter

You need to have an Observable that you've merged all of your event emitters
into.  You can also throw in a custom Comparator or container for sorting and
data processing.  Please see my notes below.

## Sorting your stuff

There is a constructor on RxRecyclerViewAdapter for handing in a ```Comparator<RxAdapterEvent<K,V>>```.
The default container will be handed this and insert items based off a binary
search algorithm.  This was chosen because of it's high performance relative to
the number of objects in the list, which scales O(log n) compared to an
iteration which would be O(n).  Much better scaling =) 

To put this into perspective, if you add an item that needs to be inserted at 
the very end of the list, and have 16 items in your list, your iterator will
need to execute 16 iterations to find where it wants to go.  Contrarily, a
binary sort algorithm would only take 5 iterations.

## Customizing the Container

If you need to do extra processing on your items AFTER they've gotten to the
adapter, you can do so by implementing ```Container``` and passing it in via
the right constructor.  If you want your container to be sortable, you should
do that yourself, perhaps by passing it a ```Comparator``` at construction like I 
have done with the default container.  Note, you likely shouldn't need to do
this, and can probably just do your processing further up the event chain.

I do recommend overriding the DefaultContainer, and add any extra processing on
top of that, as that way you can get good performance, and you get the binary
search and insert for free.  In all cases, I'd rather add the processing into
your Rx Chain before overriding this list.

## Examples

Are available in the app module!

* Comparator example has been added (reverse sort by key)
* Added a Key/Value map to hold items between screen rotations.  Note that the
  ID accumulator will currently reset itself, so if items aren't being added,
that's why.  They are, they just happen to be the exact same as the old ones.

## Licensing

This work is (C) under the MIT License.

## Gradle

This has been released on Bintray

```groovy
repositories {
    maven {
        url  "http://dl.bintray.com/exallium/maven" 
    }
}

dependencies {
    compile 'com.exallium.rxrecyclerview:lib:1.1.0'
}
```
