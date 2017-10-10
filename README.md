# mCache

## How do I use it?

Create it.
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

Save it.
```kotlin
val user = User()
Cache.give(user)
  //.ofIndex(username)
  //.ofMode(mode)
  .build()
  .getNow() // or .getLater // or .getLaterWithFollowup
```

Get it.
```kotlin
Cache.obtain(User::class.java)
  //.ofIndex(username)
  //.ofMode(mode)
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