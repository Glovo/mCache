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
MCacheBuilder.request(User.class)
    .params(FileParams)
    .save(object); // or .save(object, Function1<Boolean,Unit>)
```

Get it.
```java
MCacheBuilder.request(User.class)
    .params(FileParams)
    .with(Function1<User,Unit>);
```

How easy is that?

## Advanced tips

### Wrap observables!

```java
MCacheBuilder
    .request(User.class)
    //.using(FilesIOHandler.class) //optional
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

## Can I help?

Yes! Create pull request, issue or both.

# Download

[ ![Download](https://api.bintray.com/packages/diareuse/libs/mcache/images/download.svg) ](https://bintray.com/diareuse/libs/mcache/_latestVersion)
[![Build Status](https://travis-ci.org/diareuse/mCache.svg?branch=master)](https://travis-ci.org/diareuse/mCache)

```java
dependencies {
  compile 'wiki.depasquale:mcache:latest.release'
}
```

# Snapshots

Add this to your build.gradle

```java
repositories {
    maven { url "https://oss.sonatype.org/content/repositories/snapshots" }
}
```

And visit [this link](https://oss.sonatype.org/content/repositories/snapshots/wiki/depasquale/mcache/) for the latest version

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

# [Changelog](https://github.com/diareuse/mCache/blob/master/CHANGELOG.md)