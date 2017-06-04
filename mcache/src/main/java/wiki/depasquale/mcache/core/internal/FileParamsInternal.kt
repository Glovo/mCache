package wiki.depasquale.mcache.core.internal

/**
 * diareuse on 03.06.2017
 */

class FileParamsInternal<T> {
    var observable: io.reactivex.Observable<T>? = null
        internal set
    var requestedClass: Class<T>? = null
        internal set
    var handlers: List<wiki.depasquale.mcache.core.IOHandler> = listOf(wiki.depasquale.mcache.MCache.getIOHandler(wiki.depasquale.mcache.adapters.CacheIOHandler::class.java))
        internal set
    var fileParams: FileParams = FileParams("default")
        internal set
    var returnImmediately: Boolean = true
        internal set
    var force: Boolean = true
        internal set
    var readWith: Int = 0
        internal set

    companion object {
        fun <T> checkParams(iP: wiki.depasquale.mcache.core.internal.FileParamsInternal<T>) {
            if (iP.observable == null) iP.observable = io.reactivex.Observable.empty<T>()
            if (iP.requestedClass == null) throw RuntimeException("mCache Panic", Throwable("This is unacceptable behavior; Requested class is not present"))
        }

        fun <T> wrap(iP: wiki.depasquale.mcache.core.internal.FileParamsInternal<T>): io.reactivex.Observable<T> {

            wiki.depasquale.mcache.core.internal.FileParamsInternal.Companion.checkParams(iP)

            val publishSubject = io.reactivex.subjects.PublishSubject.create<T>()
            return publishSubject.doOnSubscribe {
                io.reactivex.Observable.just<io.reactivex.Observable<T>>(iP.observable)
                    .observeOn(io.reactivex.schedulers.Schedulers.io())
                    .flatMap {
                        val concreteObject = iP.handlers[iP.readWith].get(iP.requestedClass!!, iP.fileParams)
                        if (concreteObject != null && iP.force) {
                            concreteObject
                                .observeOn(io.reactivex.android.schedulers.AndroidSchedulers.mainThread())
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
                    .observeOn(io.reactivex.schedulers.Schedulers.io())
                    .map {
                        for (handler in iP.handlers) {
                            handler.save(it, iP.fileParams)
                        }
                        return@map it
                    }
                    .observeOn(io.reactivex.android.schedulers.AndroidSchedulers.mainThread())
                    .subscribe({ publishSubject.onNext(it) },
                        { publishSubject.onError(it) },
                        { publishSubject.onComplete() })
            }
        }
    }
}