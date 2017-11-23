package wiki.depasquale.mcache

open class FilePresenterBuilder<T : Any> : FilePresenterBuilderInterface<T> {

  override var mode: CacheMode = Cache.mode
  override lateinit var cls: Class<T>
  override var file: T? = null
  override var index: CharSequence = ""
    set(value) {
      field = value.toString().normalize().base64()
    }
  override var files: List<T> = listOf()

  /**
   * Sets class to be fetched/saved.
   *
   * **Do not tamper** if called with [Cache.obtain] or [Cache.give].
   */
  fun ofClass(cls: Class<T>): FilePresenterBuilder<T> {
    this.cls = cls
    return this
  }

  /**
   * Sets file to be saved.
   *
   * **Do not tamper** if called with [Cache.obtain] or [Cache.give].
   */
  fun ofFile(file: T): FilePresenterBuilder<T> {
    this.file = file
    return this
  }

  /**
   * Sets mode to be initialized in. This is set to [Cache.mode], which is set by [Cache.withGlobalMode] respectively, by default.
   */
  fun ofMode(mode: CacheMode): FilePresenterBuilder<T> {
    this.mode = mode
    return this
  }

  /**
   * Sets index (as index file) to be fetched/saved.
   */
  fun ofIndex(index: String): FilePresenterBuilder<T> {
    this.index = index
    return this
  }

  /**
   * @return New instance of [FilePresenter] with previously defined params.
   */
  fun build(): FilePresenter<T> = FilePresenter(this)
}