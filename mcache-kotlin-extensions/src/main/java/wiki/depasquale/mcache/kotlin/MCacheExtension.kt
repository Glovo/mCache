package wiki.depasquale.mcache.kotlin

import android.app.*
import io.reactivex.*
import wiki.depasquale.mcache.*
import wiki.depasquale.mcache.adapters.*
import wiki.depasquale.mcache.core.*
import wiki.depasquale.mcache.core.internal.*

fun Application.mCache() {
  MCache.with(this)
}

fun <T> Class<T>.request(
    handlers: Array<Class<out IOHandler>> = arrayOf(CacheIOHandler::class.java),
    params: FileParams = FileParams("default"),
    force: Boolean = true,
    pullIfNotNull: Boolean = true,
    readWith: Int = 0,
    listener: (T) -> Unit = {},
    errorConsumer: (Throwable) -> Unit = {}) {

  MCacheBuilder.request(this)
      .using(*handlers)
      .params(params)
      .force(force)
      .pullIfNotNull(pullIfNotNull)
      .readWith(readWith)
      .with(listener, errorConsumer)
}

fun <T : Any> T.saveAsCache(params: FileParams = FileParams("default")) =
    this.save<T>(handlers = arrayOf(CacheIOHandler::class.java), params = params)

fun <T : Any> T.saveAsFile(params: FileParams = FileParams("default")) =
    this.save<T>(handlers = arrayOf(FilesIOHandler::class.java), params = params)

fun <T : Any> T.save(
    handlers: Array<Class<out IOHandler>> = arrayOf(CacheIOHandler::class.java),
    params: FileParams = FileParams("default")) {
  params
  MCacheBuilder.request(this.javaClass)
      .using(*handlers)
      .params(params)
      .save(this)
}

fun <T : Any> T.loadFromCache(): Observable<T> {
  return this.javaClass.fastLoad(Observable.empty())
}

fun <T> Class<T>.fastLoad(actualRequest: Observable<T>): Observable<T> {
  return MCacheBuilder.request(this).with(actualRequest)
}

fun <T : Any> T.removeAsCache(descriptor: String = "default", listener: (Boolean) -> Unit = {}) {
  this.remove<T>(handlers = arrayOf(CacheIOHandler::class.java), descriptor = descriptor, listener = listener)
}

fun <T : Any> T.removeAsFile(descriptor: String = "default", listener: (Boolean) -> Unit = {}) {
  this.remove<T>(handlers = arrayOf(FilesIOHandler::class.java), descriptor = descriptor, listener = listener)
}

private fun <T : Any> T.remove(
    handlers: Array<Class<out IOHandler>> = arrayOf(CacheIOHandler::class.java),
    descriptor: String = "default",
    removeAll: Boolean = false,
    listener: (Boolean) -> Unit = {}) {
  MCacheBuilder.request(this::class.java)
      .using(*handlers)
      .descriptor(descriptor)
      .listener(listener)
      .removeAll(removeAll)
      .remove()
}