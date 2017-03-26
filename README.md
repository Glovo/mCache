# mCache

Main features:
* Saves object as file
* If previously saved, reads file as object
* Compatible with RxJava and RxJava2

Main uses:
* Saving frequently queried data
* Faster startups
* Caching REST requests
* You name it...

## How does it work?

Calling `<T> void save(T object, Class<?> cls)` method will be `T` object saved by corresponding name of it's class. Object is serialized by [Gson](https://github.com/google/gson) and simply saved as plain text in protected `files` folder beside your app. Beware that Realm related files cannot be serialized due to current limitations which will cause Gson to overflow. [see](https://realm.io/docs/java/latest/#gson)

Calling `<T> T get(Class<T> cls);` method will be `T` object retrieved by it's class name. It's also deserialized by Gson.

Please be aware that wrapping your Observable with method `public static <T> Observable<T> wrapRead(Observable<T> o, Class<T> cls, boolean condition, boolean force)` is your Consumer (Subscriber) executed on **non-main thread**. You can use enclosed class `Threader` and it's method `runOnUI(Runnable r)` to update your views and/or adapters.

This library is pretty much the most versatile thing you can get. With a very little effort it can cache large files as well as small and it's entirely up to you whether it's used as-is or with custom IOHandler and encrypted for instance.

## Is it customizable?

Of course it is. You can override [`DefaultIOHandler`](https://github.com/diareuse/mCache/blob/master/mcache/src/main/java/wiki/depasquale/mcache/DefaultIOHandler.java) and then set it via `MCache::setIOHandler(IOHandler ioHandler)`. Please note that it has to be initialized before calling `MCache::with(Application context)` to prevent data corruption in the process. Also if you set handler after initializing library you will be awarded with a warning and your handler won't be applied.

## Please read this!

Gson is very heavy a should be used only off main thread to prevent lag. By wrapping Observable via `MCache` class you will prevent this issue because it's already wrapped in "network thread". Methods `<T> void save(T object, Class<?> cls)` and `<T> T get(Class<T> cls);` are executed on thread from which are called. So please keep this in mind. :)

## Can I help?

Yes please! More things we come up with the better. Create issues with ideas, pull requests and whatnot! :)

# Download

``
coming soon :)
``

