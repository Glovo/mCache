package wiki.depasquale.mcache.core.internal

import android.app.Application
import io.reactivex.Observable
import wiki.depasquale.mcache.MCache
import wiki.depasquale.mcache.adapters.CacheIOHandler
import wiki.depasquale.mcache.adapters.FilesIOHandler
import wiki.depasquale.mcache.core.IOHandler
import wiki.depasquale.mcache.core.MCacheBuilder

fun Application.mCache() {
  MCache.with(this)
}

fun <T : Any> Class<T>.request(
    handlers: Array<Class<out IOHandler>> = arrayOf(CacheIOHandler::class.java),
    params: FileParams = FileParams("default"),
    force: Boolean = true,
    readWith: Int = 0,
    listener: (T) -> Unit = {},
    errorConsumer: (Throwable) -> Unit = {}) {

  MCacheBuilder.request(this)
      .using(*handlers)
      .params(params)
      .force(force)
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
  MCacheBuilder.request(this.javaClass)
      .using(*handlers)
      .params(params)
      .save(this, params.write.listener)
}

fun <T : Any> T.loadFromCache(): Observable<T> {
  return this.javaClass.fastLoad(Observable.empty())
}

fun <T : Any> Class<T>.fastLoad(actualRequest: Observable<T>): Observable<T> {
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
    all: Boolean = false,
    listener: (Boolean) -> Unit = {}) {
  val params = FileParams(descriptor)
  params.write.all = all
  MCacheBuilder.request(this::class.java)
      .using(*handlers)
      .params(params)
      .remove(listener)
}