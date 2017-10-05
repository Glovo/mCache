package wiki.depasquale.mcache

class FilePresenterBuilder<T : Any> : FilePresenterBuilderInterface<T> {

  override lateinit var cls: Class<T>
  override var file: T? = null

  fun ofClass(cls: Class<T>): FilePresenterBuilder<T> {
    this.cls = cls
    return this
  }

  fun ofFile(file: T): FilePresenterBuilder<T> {
    this.file = file
    return this
  }

  fun ofChanged(from: Long, to: Long): FilePresenterBuilder<T> {
    return this
  }

  fun ofCreated(from: Long, to: Long): FilePresenterBuilder<T> {
    return this
  }

  fun build(): FilePresenter<T> {
    return FilePresenter(this)
  }
}