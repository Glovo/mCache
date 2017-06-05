package wiki.depasquale.mcache.kotlin

import android.app.*
import io.reactivex.*
import wiki.depasquale.mcache.*
import wiki.depasquale.mcache.adapters.*
import wiki.depasquale.mcache.core.*

fun Application.mCache() {
  MCache.with(this)
}

fun <T> Class<T>.request(
    handlers: Array<Class<out IOHandler>> = arrayOf(CacheIOHandler::class.java),
    descriptor: String = "default",
    force: Boolean = true,
    pullIfNotNull: Boolean = true,
    readWith: Int = 0,
    listener: (T) -> Unit = {},
    errorConsumer: (Throwable) -> Unit = {}) {

  MCacheBuilder.request(this)
      .using(*handlers)
      .descriptor(descriptor)
      .force(force)
      .pullIfNotNull(pullIfNotNull)
      .readWith(readWith)
      .with(listener, errorConsumer)
}

fun <T : Any> T.saveAsCache(descriptor: String = "default", listener: (Boolean) -> Unit = {}) =
    this.save<T>(handlers = arrayOf(CacheIOHandler::class.java), descriptor = descriptor, listener = listener)

fun <T : Any> T.saveAsFile(descriptor: String = "default", listener: (Boolean) -> Unit = {}) =
    this.save<T>(handlers = arrayOf(FilesIOHandler::class.java), descriptor = descriptor, listener = listener)

private fun <T : Any> T.save(
    handlers: Array<Class<out IOHandler>> = arrayOf(CacheIOHandler::class.java),
    descriptor: String = "default",
    listener: (Boolean) -> Unit = {}) {
  MCacheBuilder.request(this.javaClass)
      .using(*handlers)
      .descriptor(descriptor)
      .save(this, listener)
}

fun <T : Any> T.loadFromCache(): Observable<T> {
  return this.javaClass.fastLoad(Observable.empty())
}

fun <T> Class<T>.fastLoad(actualRequest: Observable<T>): Observable<T> {
  return MCacheBuilder.request(this).with(actualRequest)
}