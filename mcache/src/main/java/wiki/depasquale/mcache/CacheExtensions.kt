package wiki.depasquale.mcache

fun <T : Any> T.give(): FilePresenterKotlinBuilder<T> {
  return FilePresenterKotlinBuilder<T>()
      .ofClass { this@give.javaClass }
      .ofFile { this@give }
}

fun <T : Any> Class<T>.obtain(): FilePresenterKotlinBuilder<T> {
  return FilePresenterKotlinBuilder<T>()
      .ofClass { this@obtain }
}
