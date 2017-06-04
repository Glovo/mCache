package wiki.depasquale.mcache.kotlin

import io.reactivex.Observable
import wiki.depasquale.mcache.adapters.CacheIOHandler
import wiki.depasquale.mcache.core.IOHandler
import wiki.depasquale.mcache.core.MCacheBuilder

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

fun <T : Any> T.saveAsCache(
    handlers: Array<Class<out IOHandler>> = arrayOf(CacheIOHandler::class.java),
    descriptor: String = "default") {
    MCacheBuilder.request(this.javaClass)
        .using(*handlers)
        .descriptor(descriptor)
        .save(this)
}

fun <T : Any> T.loadFromCache(): Observable<T> {
    return this.javaClass.fastLoad(Observable.empty())
}

fun <T> Class<T>.fastLoad(actualRequest: Observable<T>): Observable<T> {
    return MCacheBuilder.request(this).with(actualRequest)
}