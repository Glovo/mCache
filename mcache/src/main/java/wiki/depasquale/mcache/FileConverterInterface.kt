package wiki.depasquale.mcache

interface FileConverterInterface<T> {
  val builder: FilePresenterBuilderInterface<T>
  var encodedFile: String
  val encodedFiles: List<String>
}