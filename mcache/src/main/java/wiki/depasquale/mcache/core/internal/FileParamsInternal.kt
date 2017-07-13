package wiki.depasquale.mcache.core.internal

import io.reactivex.Observable
import io.reactivex.android.schedulers.AndroidSchedulers
import io.reactivex.schedulers.Schedulers
import io.reactivex.subjects.PublishSubject
import wiki.depasquale.mcache.MCache
import wiki.depasquale.mcache.adapters.CacheIOHandler
import wiki.depasquale.mcache.core.IOHandler

/**
 * diareuse on 03.06.2017
 */

class FileParamsInternal<T> {
  var observable: Observable<T>? = null
  var requestedClass: Class<T>? = null
  var handlers: MutableList<IOHandler> = mutableListOf(MCache.getIOHandler(CacheIOHandler::class.java))
  var fileParams: FileParams = FileParams("default")
  var force: Boolean = true
  var readWith: Int = 0

  companion object {
    @JvmStatic
    fun <T> checkParams(iP: FileParamsInternal<T>) {
      if (iP.observable == null) iP.observable = Observable.empty<T>()
      if (iP.requestedClass == null) throw RuntimeException("mCache Panic", Throwable("This is unacceptable behavior; Requested class is not present"))
    }

    @JvmStatic
    fun <T : Any> wrap(iP: FileParamsInternal<T>): Observable<T> {
      FileParamsInternal.checkParams(iP)
      val publishSubject = PublishSubject.create<T>()
      return publishSubject.doOnSubscribe {
        Observable.just(iP.observable)
            .observeOn(Schedulers.io())
            .flatMap {
              val concreteObject = iP.handlers[iP.readWith][iP.requestedClass!!, iP.fileParams]
              if (iP.force) {
                concreteObject
                    .observeOn(AndroidSchedulers.mainThread())
                    .subscribe({ publishSubject.onNext(it) }, { publishSubject.onError(it) })
              }
              return@flatMap iP.observable ?: Observable.empty()
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