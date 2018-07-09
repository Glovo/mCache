# mCache

## How do I use it?

### Create it.

via Java:
```java
public class App extends Application {

   @Override
   public void onCreate() {
    super.onCreate();
    Cache
      //.withGlobalMode(CacheMode.FILE)
      .with(this);
  }
}
```
via Kotlin:
```kotlin
class App : Application() {

  override fun onCreate() {
    super.onCreate()
    Cache
      //.withGlobalMode(CacheMode.FILE)
      .with(this)
  }
}
```

### Save it.

via Java:
```java
User user = new User();
Cache.give(user)
  //.ofIndex(username)
  //.ofMode(mode)
  .build()
  .getNow(); // or .getLater // or .getLaterWithFollowup
```
via Kotlin:
```kotlin
User().give()
  //.ofIndex { username }
  //.ofMode { mode }
  .build()
  .getNow() // or .getLater // or .getLaterWithFollowup
```

### Get it.

via Java:
```java
Cache.obtain(User.class)
  //.ofIndex(username)
  //.ofMode(mode)
  .build()
  .getNow(); // or .getLater // or .getLaterWithFollowup
```
via Kotlin:
```kotlin
obtain<User>()
  //.ofIndex { username }
  //.ofMode { mode }
  .build()
  .getNow() // or .getLater // or .getLaterWithFollowup
```

How easy is that?

## Advanced tips

### Wrap observables!

```kotlin
Cache.obtain(User::class.java)
  ...
  .getLaterWithFollowup(observable)
```

### Customize

You can redo whole library yourself since it's now made entirely via interfaces.

## Contributing

Fork me - make changes - reformat using [this code style](https://github.com/diareuse/contributing/blob/master/ImprovedGoogleStyle.xml)

# Download

[![](https://jitpack.io/v/diareuse/mCache.svg)](https://jitpack.io/#diareuse/mCache)

Add this to your build.gradle

```java
   allprojects {
		repositories {
			...
			maven { url 'https://jitpack.io' }
		}
	}
```

```java
dependencies {
  implementation 'com.github.diareuse:mCache:latest.release'
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

# [Changelog](https://github.com/diareuse/mCache/blob/master/CHANGELOG.md)
