package wiki.depasquale.mcache

import android.util.Base64

class FileWrapper(override val converter: FileConverterInterface<*>) : FileWrapperInterface {

  fun wrap(encodedFile: String) {
    FileRW(this)
      .write(String(Base64.encode(encodedFile.toByteArray(), Base64.NO_WRAP)))
  }

  fun unwrap(): String {
    return String(Base64.decode(FileRW(this).read(), Base64.NO_WRAP))
  }
}