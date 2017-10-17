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
  override val encodedFiles: List<String>
    get() {
      return FileWrapper(this)
          .unwrapList()
    }

  fun run() {
    if (builder.file == null) {
      decode()
    } else {
      encode()
    }
  }

  fun fetchAll() {
    val gson = Gson()
    builder.files = encodedFiles.map { gson.fromJson(it, builder.cls) }
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