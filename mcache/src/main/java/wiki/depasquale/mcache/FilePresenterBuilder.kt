package wiki.depasquale.mcache

import java.text.Normalizer

class FilePresenterBuilder<T : Any> : FilePresenterBuilderInterface<T> {

  override var mode: CacheMode = Cache.mode
  override lateinit var cls: Class<T>
  override var file: T? = null
  override var index: CharSequence = ""

  fun ofClass(cls: Class<T>): FilePresenterBuilder<T> {
    this.cls = cls
    return this
  }

  fun ofFile(file: T): FilePresenterBuilder<T> {
    this.file = file
    return this
  }

  fun ofMode(mode: CacheMode): FilePresenterBuilder<T> {
    this.mode = mode
    return this
  }

  fun ofIndex(index: String): FilePresenterBuilder<T> {
    this.index = Normalizer.normalize(index.toLowerCase(), Normalizer.Form.NFD).base64()
    return this
  }

  fun build(): FilePresenter<T> {
    return FilePresenter(this)
  }
}