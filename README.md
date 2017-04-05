# mCache

## How do I use it?

Create it.
```java
public class App extends Application {

  @Override public void onCreate() {
    super.onCreate();
    MCache.with(this);
  }
}
```

Save it.
```java
MCache.save(T object, Class<?> cls)
```

Get it.
```java
MCache.get(Class<T> cls);
```

How easy is that?

## Advanced tips

Wrap observables!

```java
MCache.wrap(Observable.just(1,2,3), Integer.class, false /*condition*/, false /*force*/);
```

Extend [`DefaultIOHandler`](https://github.com/diareuse/mCache/blob/master/mcache/src/main/java/wiki/depasquale/mcache/DefaultIOHandler.java)
Use encryption within your app. Cache with different mechanism. Whatever! :)

## Please read this!

Gson is very heavy a should be used only off main thread to prevent lag. By wrapping Observable via `MCache` class you will prevent this issue because it's already wrapped in "network thread". Methods `<T> void save(T object, Class<?> cls)` and `<T> T get(Class<T> cls);` are executed on thread from which are called. So please keep this in mind. :)

## Can I help?

Yes please! More things we come up with the better. Create issues with ideas, pull requests and whatnot! :)

## Dude what's this even for...?

Main features:
* Saves object as file
* If previously saved, reads file as object
* Compatible with RxJava and RxJava2

Main uses:
* Saving frequently queried data
* Faster startups
* Caching REST requests
* You name it... everything that could be saved instead wasting processing time

# Download

[ ![Download](https://api.bintray.com/packages/diareuse/libs/mcache/images/download.svg) ](https://bintray.com/diareuse/libs/mcache/_latestVersion)

```java
dependencies {
  compile 'wiki.depasquale:mcache:latest_version'
}
```
