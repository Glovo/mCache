package wiki.depasquale.mcache.core

import io.reactivex.*
import io.reactivex.android.schedulers.*
import io.reactivex.schedulers.*
import wiki.depasquale.mcache.*
import wiki.depasquale.mcache.adapters.*
import wiki.depasquale.mcache.core.internal.*

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
   * Sets **IOHandler** to handle upcoming situation. If not set [FilesIOHandler] will be
   * used.

   * @param handlers Classes of IOHandler. Custom or not, it does not care.
   * *
   * @return building instance
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
   * Sets descriptor for saving/loading given class.

   * @param descriptor Preferably following this pattern "_somePostFix" or ".somePostFix". This is
   * * just a suggestion.
   * *
   * @return building instance
   */
  fun descriptor(descriptor: String): MCacheBuilder<T> {
    internalParams.fileParams = FileParams(descriptor)
    return this
  }

  /**
   * Sets whether it should forcefully update the data within later given Observable. If false given
   * Observable won't be subscribed to unless there's no saved data. **If you are not using RxJava
   * with this library you can freely skip this, it won't have any effect.** Default is false.

   * @param force Boolean representation of precondition
   * *
   * @return building instance
   */
  fun force(force: Boolean): MCacheBuilder<T> {
    internalParams.force = force
    return this
  }

  /**
   * Overrides caching process to immediately return cached version so onNext method will be
   * effectively called twice. Default is true.

   * @param pullIfNotNull Boolean representation of condition
   * *
   * @return building instance
   */
  fun pullIfNotNull(pullIfNotNull: Boolean): MCacheBuilder<T> {
    internalParams.returnImmediately = pullIfNotNull
    return this
  }

  /**
   * Indicates with which handler should it read values. This is extremely useful if you input more
   * than one handler to [.using] method. First handler has 0 index.

   * @param position valid position
   * *
   * @return building instance
   * *
   * @throws IllegalArgumentException when position is greater or equal to number of handlers
   */
  fun readWith(position: Int): MCacheBuilder<T> {
    internalParams.readWith = position
    return this
  }

  fun params(params: FileParams?): MCacheBuilder<T> {
    if (params != null) {
      internalParams.fileParams = params
    }
    return this
  }

  /**
   * Creates map around given observable with earlier predefined conditions.

   * @param o Observable of matching class
   * *
   * @return The same observable
   */
  fun with(o: Observable<T>?): Observable<T> {
    internalParams.observable = o
    return FileParamsInternal.wrap(internalParams)
  }

  /**
   * Asynchronously returns saved object with earlier predefined conditions. First handler in list
   * will be used.

   * @param listener Listener with corresponding class
   */
  fun with(listener: (T) -> Unit, errorConsumer: (Throwable) -> Unit) {
    FileParamsInternal.checkParams(internalParams)
    Observable.just(internalParams.handlers[internalParams.readWith])
        .observeOn(Schedulers.io())
        .map { handler ->
          handler
              .get(internalParams.requestedClass!!, internalParams.fileParams)
        }
        .observeOn(AndroidSchedulers.mainThread())
        .flatMap { it -> it }
        .subscribe({ listener.invoke(it) }, { errorConsumer.invoke(it) })
  }

  /**
   * Saves given object to file with predefined conditions. First handler in list will be used.

   * @param object non null object
   */
  fun save(t: T) {
    for (handler in internalParams.handlers) {
      handler.save(t, internalParams.fileParams)
    }
  }

  /**
   * All handlers specified in [.using] will be cleansed.

   * @see IOHandler.remove
   */
  fun remove() {
    internalParams.handlers.forEach { it -> it.remove(internalParams.requestedClass!!, internalParams.fileParams) }
  }

  fun listener(listener: Function1<Boolean, Unit>): MCacheBuilder<T> {
    internalParams.fileParams.listener = listener
    return this
  }

  fun removeAll(removeAll: Boolean): MCacheBuilder<T> {
    internalParams.fileParams.removeAll = removeAll
    return this
  }

  companion object {

    /**
     * Creates new **MCacheBuilder** with affinity to class given as parameter.

     * @param cls Class of the object which needs to be saved/loaded.
     * *
     * @return new **MCacheBuilder**
     */
    @JvmStatic
    fun <U : Any> request(cls: Class<U>): MCacheBuilder<U> {
      return MCacheBuilder(cls)
    }
  }
}
