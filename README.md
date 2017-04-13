# mCache

## How do I use it?

Create it.
```java
public class App extends Application {

  @Override public void onCreate() {
    super.onCreate();
    MCache.with(this)
      .build();
  }
}
```

Save it.
```java
MCacheBuilder.request(User.class)
    .id(id) //optional
    .save(object));
```

Get it.
```java
MCacheBuilder.request(User.class)
    .id(id) //optional
    .with(); //or asynchronously .with(FinishedListener<? extends User> listener)
```

How easy is that?

## Advanced tips

Wrap observables!

```java
MCacheBuilder
    .request(User.class)
    //.using(DefaultIOHandler.class) //optional
    //.id(MCache.DEFAULT_ID) //optional
    .precondition(!username.equals(userUsername)) //optional (default false)
    .force(username.equals(userUsername)) //optional (default false)
    .with(getRetrofit()
        .user(username)
        .subscribeOn(Schedulers.newThread())
        .observeOn(AndroidSchedulers.mainThread()))
    .map(user -> {
        userUsername = username;
        return user;
    });
```

Implement [`IOHandler`](https://github.com/diareuse/mCache/blob/master/mcache/src/main/java/wiki/depasquale/mcache/core/IOHandler.java)
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

# Licence

Copyright 2017 Viktor De Pasquale

Licensed under the Apache License, Version 2.0 (the "License");
you may not use this file except in compliance with the License.
You may obtain a copy of the License at

http://www.apache.org/licenses/LICENSE-2.0

Unless required by applicable law or agreed to in writing, software
distributed under the License is distributed on an "AS IS" BASIS,
WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
See the License for the specific language governing permissions and
limitations under the License.

Created by Viktor De Pasquale in cooperation with [`Cortex spol. s.r.o.`](https://www.cortex.cz/)