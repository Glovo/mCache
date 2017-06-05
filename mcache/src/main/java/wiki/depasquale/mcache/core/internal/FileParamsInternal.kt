package wiki.depasquale.mcache.core.internal

import io.reactivex.*
import io.reactivex.android.schedulers.*
import io.reactivex.schedulers.*
import io.reactivex.subjects.*
import wiki.depasquale.mcache.*
import wiki.depasquale.mcache.adapters.*
import wiki.depasquale.mcache.core.*

/**
 * diareuse on 03.06.2017
 */

class FileParamsInternal<T> {
  var observable: io.reactivex.Observable<T>? = null
  var requestedClass: Class<T>? = null
  var handlers: MutableList<IOHandler> = mutableListOf(MCache.getIOHandler(CacheIOHandler::class.java))
  var fileParams: FileParams = FileParams("default")
  var returnImmediately: Boolean = true
  var force: Boolean = true
  var readWith: Int = 0

  companion object {
    @JvmStatic
    fun <T> checkParams(iP: FileParamsInternal<T>) {
      if (iP.observable == null) iP.observable = Observable.empty<T>()
      if (iP.requestedClass == null) throw RuntimeException("mCache Panic", Throwable("This is unacceptable behavior; Requested class is not present"))
    }

    @JvmStatic
    fun <T> wrap(iP: FileParamsInternal<T>): Observable<T> {

      FileParamsInternal.checkParams(iP)

      val publishSubject = PublishSubject.create<T>()
      return publishSubject.doOnSubscribe {
        Observable.just(iP.observable)
            .observeOn(Schedulers.io())
            .flatMap {
              val concreteObject = iP.handlers[iP.readWith].get(iP.requestedClass!!, iP.fileParams)
              if (concreteObject != null && iP.force) {
                concreteObject
                    .observeOn(AndroidSchedulers.mainThread())
                    .subscribe({
                      publishSubject.onNext(it)
                      if (!iP.returnImmediately) {
                        publishSubject.onComplete()
                      }
                    })
              }
              if (concreteObject == null || iP.returnImmediately) {
                return@flatMap iP.observable
              } else {
                return@flatMap concreteObject
              }
            }
            .map {
              for (handler in iP.handlers) {
                handler.save(it, iP.fileParams)
              }
              return@map it
            }
            .observeOn(AndroidSchedulers.mainThread())
            .subscribe({ publishSubject.onNext(it) },
                { publishSubject.onError(it) },
                { publishSubject.onComplete() })
      }
    }
  }
}