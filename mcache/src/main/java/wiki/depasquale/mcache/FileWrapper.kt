package wiki.depasquale.mcache

class FileWrapper(override val converter: FileConverterInterface<*>) : FileWrapperInterface {

  fun wrap(encodedFile: String) {
    FileRW(this).write(encodedFile.base64())
  }

  fun unwrap(): String {
    return FileRW(this).read().unBase64()
  }

  fun delete(): Boolean {
    return FileRW(this).delete()
  }

  fun unwrapList(): List<String> {
    return FileRW(this).all().map { it.unBase64() }
  }
}