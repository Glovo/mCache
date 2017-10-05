package wiki.depasquale.mcache

class FileRW(override val wrapper: FileWrapperInterface) : FileRWInterface {

  fun read(): String {
    //todo
    return ""
  }

  @Synchronized
  fun write(wrappedFile: String) {
    //todo
  }
}