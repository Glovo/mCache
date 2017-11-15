package wiki.depasquale.mcache

class FilePresenterKotlinBuilder<T : Any> : FilePresenterBuilder<T>() {

  /**
   * @see FilePresenterBuilder.ofClass
   */
  fun ofClass(init: FilePresenterKotlinBuilder<T>.() -> Class<T>) = apply { cls = init() }

  /**
   * @see FilePresenterBuilder.ofFile
   */
  fun ofFile(init: FilePresenterKotlinBuilder<T>.() -> T) = apply { file = init() }

  /**
   * @see FilePresenterBuilder.ofMode
   */
  fun ofMode(init: FilePresenterKotlinBuilder<T>.() -> CacheMode) = apply { mode = init() }

  /**
   * @see FilePresenterBuilder.ofIndex
   */
  fun ofIndex(init: FilePresenterKotlinBuilder<T>.() -> String) = apply { index = init() }
}