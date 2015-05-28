# RxRecyclerViewAdapter Library 2.0

Crazy easy to use RecyclerView Adapter for Reactive Applications

## Interface

* ```RxRecyclerViewAdapter::onCreateViewHolder``` is the same as ```RecyclerView.Adapter```
* ```RxRecyclerViewAdapter::onBindViewHolder``` gives you the Element you are
  binding to
* ```Event<K,V>``` is Immutable and takes an ```Event.TYPE```, Key, and Value.
* ```Event<K,V>``` also contains an UNKNOWN type and is overridable for custom
  processing.

## Creating an Adapter

You need to have an Observable that you've merged all of your event emitters
into.  You then need to either map those into EventElements or utilize
GroupComparator and it's corresponding Operator via Observable::lift to do the
conversion for you.

## Sorting and Grouping your stuff

There is an interface called GroupComparator that lets you sort and group your
Events.  These are passed to an instance of ```ElementGenerationOperator```
which will then add in Header and Footer items, as well as handle Empty items
per your provided Options.  The Adapter uses a TreeSet internally, which allows
for automatic sorting by natural keys (Elements subclass Comparator)

## Examples

Are available in the app module!

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
