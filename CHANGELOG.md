### UPC (dev)
* create CacheMode
* add global (default) mode to Cache object
* add ofMode method so one call can override default settings
* add read/write methods as interface
* fill r/w methods with some random code
* create extension functions for base64 and simple operations to make them inline
3ba01b5
* fill the files with some random code with seed: V2hhdCBpcyB5b3VyIHByb2JsZW0/IERvIHlvdSBuZWVkIHRvIGRlY29kZSBldmVyeXRoaW5nIGNvbXB1bHNpdmVseT8=
* rename base class
* add type to classes that will need it
* add some basic structure to the builder
* add file writer
* add some basic files with no fill, because why not
* convert main class to object (static) and remove most of unnecessary methods
* set/get context in more clean fashion
* fix update task so it takes UNSTABLE and RC builds in account
* fix small errors
* update google's support dependencies
* use implementation instead of compile so libs won't bleed through mcache
* add new interfaces to be used in the future
* remove compat files
* change tag to unstable
* update android studio with buildtools

### 31.07.2017 (1.5.9)
* Builds are no longer tracked by build. It's automatically incremented each time project is built.
* Updated libs

### 1.0.3/1.0.4
* All changes are now uploaded to Sonatype and that means the only thing! [SNAPSHOTS!](https://oss.sonatype.org/content/repositories/snapshots/wiki/depasquale/mcache/).
* Crashes occurring due to improper conditions should be no longer present
* Added option to filter classes (objects) by **created and/or changed timestamp**
* Slightly edited FileParams class which should be more rigid in terms of reusability

### 1.0.2
* Stability update; removeAll flag won't throw concurrent exceptions no more
* Other stability fixes, see commits

### 1.0.1
* Added option to retrieve all objects for given class

### 1.0.0
* Totally redone whole caching system. Your files will be **stranded**.
  * Project is redone in Kotlin
  * Library contains changes that allow greater **versatility**
* Params started to be messy and so they are replaced with FileParams class
* Since we can have random number of params identifiers can be no longer embedded to file name so this system is replaced by folder-per-class system with unique map inside each folder which will store params for each file
  * Files are now stored under unique id which cannot be changed
    * Current id is replaced with Descriptor by default
* MCacheBuilder is moved in package so it can access essential parts without exposing interface (you'll need to replace the imports, that's it)
* All initialization params are now removed

### 0.7.4 - 0.7.5
* Remove RxJava (first gen) as it's no longer actively maintained 
  * RxJava2 is kept and remains recommended way to cache

### 0.7.3
* Updates build script
* Adds option to enable/disable passing errors within to final observable
* Fixes issue with final observable not being subscribed to immediately results in lost data

### 0.7.2
* Fixes issue with incompatible libraries to latest stable channel

### 0.7.1
* Fixes issue in 0.7 where files that are not on hand will not be read ever

### 0.7
* (Re)Added method pullIfNotNull(boolean) which purpose is to handle whether cached version is immediately thrown into onNext or not. Only effective in combination with wrapping Observables and force(true).

### 0.6.1
* Added MCache.clean(Class<? extends IOHandler>...) method

### 0.6
* Method precondition(boolean) was removed due to confusion, magic and mystery surrounding it
  * Use combination of id(CharSequence) and force(boolean) to achieve the same effect
* Libs were updated to bleeding edge versions
* build() method was removed
* Threader class was removed because it's no longer needed
* Internals of wrapping observables were completely redone
  * Cached object is returned by default
    * Unless it has force(boolean) flag set
  * No longer pushes 2 onNext(T) objects if is cached version present and force flag is set. If force flag is set it won't return cached version first anymore.

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