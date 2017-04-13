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

### Wrap observables!

```java
MCacheBuilder
    .request(User.class)
    //.using(FilesIOHandler.class) //optional
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

### Use multiple handlers

```java
MCacheBuilder
    ...
    .using(CacheIOHandler.class, FilesIOHandler.class)
    .readWith(1) //read with FilesIOHandler
    ...
    .with(Observable<?>);
```

### Use unique SharedPrefs handler
* Annotate only generic types that are supported by SharedPreferences interface. ```(int, long, float, boolean, String)```

Apply annotation.

```java
class User {

  //jackson or Gson annotations ... or not
  @PrefName("username") //Saves value of String login in SharedPrefs under "username" key
  public String login;
  ...
}
```

Just add one more handler.

```java
MCacheBuilder
    ...
    .using(FilesIOHandler.class, SharedPrefsIOHandler.class)
    ...
    .with(Observable<User>);
```

Now retrieve your values like so:

```java
SharedPrefsIOHandler.getPrefs().getString("username", ":(");
```

And that's it. Easy right?

### Customize

Implement [`IOHandler`](https://github.com/diareuse/mCache/blob/master/mcache/src/main/java/wiki/depasquale/mcache/core/IOHandler.java)
Use encryption within your app. Cache with different mechanism. Whatever! :)

## Please read this!

Gson is very heavy a should be used only off main thread to prevent lag. By wrapping Observable via `MCache` class you will prevent this issue because it's already wrapped in "network thread". Methods `<T> void save(T object, Class<?> cls)` and `<T> T get(Class<T> cls);` are executed on thread from which are called. So please keep this in mind. :)

## Can I help?

Yes! Create pull request, issue or both.

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

# Changelog

### 0.5
* Refractored DefaultIOHandler to FilesIOHandler
* Added CacheIOHandler
  * Unlike FilesIOHandler saves files to cache folder which makes saved files vulnerable to getting deleted by system.
  * However all the files are easily deletable, so user can do it too.
  * In future versions this may become the default handler.
* Added @interface PrefName
* Added SharedPrefsIOHandler
  * Saves fields annotated by PrefName in Shared Preferences folder
  * Easily retrieved by it's getPrefs() method - even though it can return null value, don't be afraid of it, it probably won't happen as long as your app doesn't lose context
* Removed logging/debugging option as it's no longer needed (can be added back upon request, however it will more likely crash than contain an error)

### 0.4
* Final API, first stable release
* Minor refractors
* Minor logic changes

### pre-0.4
* Highly unstable
* Experimental features
* No standard API