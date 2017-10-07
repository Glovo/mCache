package wiki.depasquale.mcache

import java.io.File

class FileRW(override val wrapper: FileWrapperInterface) : FileRWInterface {

  override fun read(): String {
    val file = findFile()
    if (file.length() <= 0L)
      return ""
    return file.readText()
  }

  @Synchronized
  override fun write(wrappedFile: String) {
    val file = findFile()
    file.writeText(wrappedFile)
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
    val classFile = File(classFolder, findFileNumber(classFolder))
    classFile.createNewFile()

    return classFile
  }

  private fun findFileNumber(classFolder: File): String {
    classFolder.listFiles().forEach {
      if (!it.name.isNumber() || !it.isFile) {
        it.deleteRecursively()
      }
    }
    val maxNumber = classFolder.listFiles().map { it.name.toInt() }.maxBy { it } ?: 0
    return (maxNumber + 1).toString()
  }
}