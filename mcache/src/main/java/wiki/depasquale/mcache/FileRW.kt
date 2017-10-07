package wiki.depasquale.mcache

import java.io.File

class FileRW(override val wrapper: FileWrapperInterface) : FileRWInterface {

  override fun read(): String {
    val file = findFile()
    if (file.length() <= 0L)
      return ""
    return file.readText()
  }

  override fun write(wrappedFile: String) {
    synchronized(lock) {
      val file = findFile()
      file.writeText(wrappedFile)
    }
  }

  private fun findFile(): File {
    val builder = wrapper.converter.builder
    val folder = when (builder.mode) {
      CacheMode.CACHE -> Cache.context.cacheDir
      CacheMode.FILE  -> Cache.context.filesDir
    }
    val name = builder.cls.simpleName

    val homeFolder = File(folder, "mCache")
    homeFolder.mkdirs()
    val classFolder = File(homeFolder, name.base64())
    classFolder.mkdirs()
    val classFile = File(classFolder, findFileName(classFolder))
    classFile.createNewFile()

    return classFile
  }

  private fun findFileName(classFolder: File): String {
    removeUnwantedFiles(classFolder)

    val index = wrapper.converter.builder.index
    if (index.isNotEmpty())
      return index.replace(Regex("[^\\p{L}\\p{Z}]"), "").base64()

    val maxNumber = classFolder.listFiles().mapNotNull { it.name.toIntOrNull() }.maxBy { it } ?: 0
    return (maxNumber + 1).toString()
  }

  private fun removeUnwantedFiles(classFolder: File) {
    classFolder.listFiles().forEach {
      if (/*!it.name.isNumber() || */!it.isFile) {
        it.deleteRecursively()
      }
    }
  }

  companion object {
    @JvmField
    val lock: Any = Any()
  }
}