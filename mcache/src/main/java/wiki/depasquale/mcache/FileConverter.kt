package wiki.depasquale.mcache

import com.google.gson.Gson

internal class FileConverter<T>(override val builder: FilePresenterBuilderInterface<T>) : FileConverterInterface<T> {

  override var encodedFile: String
    set(value) {
      FileWrapper(this)
        .wrap(value)
    }
    get() {
      return FileWrapper(this)
        .unwrap()
    }

  init {
    if (builder.file == null) {
      decode()
    } else {
      encode()
    }
  }

  private fun encode() {
    encodedFile = Gson().toJson(builder.file)
  }

  private fun decode() {
    builder.file = Gson().fromJson(encodedFile, builder.cls)
  }

}