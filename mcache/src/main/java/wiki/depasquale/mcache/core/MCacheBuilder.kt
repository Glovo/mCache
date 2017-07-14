package wiki.depasquale.mcache.core

import io.reactivex.Observable
import io.reactivex.android.schedulers.AndroidSchedulers
import io.reactivex.schedulers.Schedulers
import wiki.depasquale.mcache.MCache
import wiki.depasquale.mcache.core.internal.FileParams
import wiki.depasquale.mcache.core.internal.FileParamsInternal

/**
 * Created by diareuse on 10/04/2017. Yeah. Suck it.
 */

class MCacheBuilder<T : Any> {

  private val internalParams = FileParamsInternal<T>()

  private constructor() {
    throw RuntimeException("This shall not be used!")
  }

  private constructor(cls: Class<T>) {
    internalParams.requestedClass = cls
  }

  /**
   * **Base function**: Replaces current IOHandlers with given
   *
   * **Detailed function**: Clears current handlers, then it creates instance for each given
   * IOHandler javaClass and adds them to current handlers.
   */
  @SafeVarargs
  fun using(vararg handlers: Class<out IOHandler>): MCacheBuilder<T> {
    internalParams.handlers.clear()
    for (handler in handlers) {
      internalParams.handlers.add(MCache.getIOHandler(handler))
    }
    return this
  }

  /**
   * **Base function**: Sets force
   *
   * **Detailed function**: Force indicates whether given observable should be subscribed to or not.
   * If you set this as >true< it will be subscribed immediately upon creation.
   */
  fun force(force: Boolean): MCacheBuilder<T> {
    internalParams.force = force
    return this
  }

  /**
   * **Base function**: Read by handler with position useful with multiple handlers
   *
   * **Detailed function**: Integer interprets position of handler wanted to be read with.
   * Defaults to >0<.
   */
  fun readWith(position: Int): MCacheBuilder<T> {
    internalParams.readWith = position
    return this
  }

  /**
   * **Base function**: Sets params
   *
   * **Detailed function**: Unconditionally sets params given as parameter. This is more useful
   * than using **descriptor(String)** method which won't allow this very versatility
   */
  fun params(params: FileParams): MCacheBuilder<T> {
    internalParams.fileParams = params
    return this
  }

  /**
   * **Base function**: Sets observable
   *
   * **Detailed function**: Wraps given Observable by params described before and returns
   * Observable with cache.
   */
  fun with(o: Observable<T>?): Observable<T> {
    internalParams.observable = o
    return FileParamsInternal.wrap(internalParams)
  }

  /**
   * **Base function**: Immediately pushes the cache to the listener.
   *
   * **Detailed function**: Creates an Observable with handler and spec to read with. Requests
   * given class with params.
   */
  fun with(listener: (T) -> Unit, errorConsumer: (Throwable) -> Unit = {}) {
    FileParamsInternal.checkParams(internalParams)
    Observable.just(internalParams.handlers[internalParams.readWith])
        .observeOn(Schedulers.io())
        .map { it[internalParams.requestedClass!!, internalParams.fileParams] }
        .observeOn(AndroidSchedulers.mainThread())
        .flatMap { it -> it }
        .subscribe({ listener.invoke(it) }, { errorConsumer.invoke(it) })
  }

  /**
   * **Base function**: Saves object
   *
   * **Detailed function**: Uses all handlers specified to save given object with params specified
   * before.
   */
  @JvmOverloads
  fun save(t: T, listener: Function1<Boolean, Unit> = {}) {
    internalParams.fileParams.write.listener = listener
    for (handler in internalParams.handlers) {
      handler.save(t, internalParams.fileParams)
    }
  }

  /**
   * **Base function**: Removes object with params
   *
   * **Detailed function**: Uses all handlers specified to remove object with params speficied
   * before.
   */
  @JvmOverloads
  fun remove(listener: Function1<Boolean, Unit> = {}) {
    internalParams.fileParams.write.listener = listener
    internalParams.handlers.forEach { it -> it.remove(internalParams.requestedClass!!, internalParams.fileParams) }
  }

  companion object {

    /**
     * **Base function**: Initializes builder with Class given
     *
     * **Detailed function**: Adds class to request base and creates builder.
     */
    @JvmStatic
    fun <U : Any> request(cls: Class<U>): MCacheBuilder<U> {
      return MCacheBuilder(cls)
    }
  }
}
