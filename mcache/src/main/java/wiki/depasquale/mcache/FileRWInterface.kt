package wiki.depasquale.mcache

interface FileRWInterface {
  val wrapper: FileWrapperInterface
  fun read(): String
  fun write(wrappedFile: String)
  fun delete(): Boolean
}