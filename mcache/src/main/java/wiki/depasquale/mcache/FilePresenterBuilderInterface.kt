package wiki.depasquale.mcache

interface FilePresenterBuilderInterface<T> {

  var file: T?
  var cls: Class<T>
  var mode: CacheMode
}