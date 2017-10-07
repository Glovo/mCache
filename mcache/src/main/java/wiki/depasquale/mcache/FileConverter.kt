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

  fun run() {
    if (builder.file == null) {
      decode()
    } else {
      encode()
    }
  }

  fun delete(): Boolean {
    return FileWrapper(this).delete()
  }

  private fun encode() {
    encodedFile = Gson().toJson(builder.file)
  }

  private fun decode() {
    builder.file = Gson().fromJson(encodedFile, builder.cls)
  }

}