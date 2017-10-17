package wiki.depasquale.mcache

import io.reactivex.Flowable
import io.reactivex.Observable
import io.reactivex.Single
import io.reactivex.android.schedulers.AndroidSchedulers
import io.reactivex.schedulers.Schedulers
import io.reactivex.subjects.PublishSubject

class FilePresenter<T>(private val builder: FilePresenterBuilderInterface<T>) {

  /**
   * Initializes [FileConverter] with parameters from [builder]. This function is *automagical*,
   * based on input from builder it loads or saves stuff. If there is no file supplied in [builder]
   * it fetches it from disk and returns it, otherwise it returns whatever is in
   * [FilePresenterBuilderInterface.file] at that specific moment which might be null or even
   * previous value.
   *
   * **This function is *NOT* safe to use on Main Thread.**
   *
   * This warning can be omitted for smaller files, although take in consideration that library
   * uses [Gson][com.google.gson.Gson] as it's main json converter.
   *
   * PS: Gson is bulky, use [getLater] on Main Thread instead.
   */
  fun getNow(): T? {
    FileConverter(builder).run()
    return builder.file
  }

  /**
   * Wraps [getNow] in [Single] which will return either *next* or *throwable*. Single does not
   * "complete", it completes by returning value or error then it dies.
   *
   * Throwable is returned in second (rx) block if file is not found. Make sure you've this clause
   * included.
   *
   * **This function *is* safe to use on Main Thread**
   *
   * It might throw error even if successfully saved, because [FilePresenterBuilderInterface.file]
   * is null at moment of subscribing, which is not allowed by Rx. This is *correct* behavior.
   */
  fun getLater(): Single<T> {
    return Single.just(getNow()!!)
        .subscribeOn(Schedulers.io())
        .observeOn(AndroidSchedulers.mainThread())
  }

  /**
   * Initializes [FileConverter] with parameters from [builder]. Apart from [getNow] returned list
   * might not be ever null, although it might be empty.
   *
   * **This function is *NOT* safe to use on Main Thread**. Use [getAllLater] on Main Thread no
   * matter what.
   */
  fun getAll(): List<T> {
    FileConverter(builder).fetchAll()
    return builder.files
  }

  /**
   * Wraps [getAll] in [Flowable] which will return stream of non-null values ([T]) and after all
   * items are emitted it completes automagically.
   *
   * Use [Flowable.toList] to aggregate emitted items to single list.
   *
   * **This function *is* safe to use on Main Thread**
   */
  fun getAllLater(): Flowable<T> {
    return Flowable.just(getAll())
        .flatMapIterable { it }
        .subscribeOn(Schedulers.io())
        .observeOn(AndroidSchedulers.mainThread())
  }

  /**
   * Does the same as [getLater] except it subscribes to [followup] on next/throwable immediately.
   */
  fun getLaterWithFollowup(followup: Observable<T>): Observable<T> {
    val subject = PublishSubject.create<T>()
    return subject
        .doOnSubscribe {
          getLater().subscribe({
            subject.onNext(it)
            followup(followup, subject)
          }, {
            followup(followup, subject)
          })
        }
  }

  private fun followup(followup: Observable<T>, subject: PublishSubject<T>) {
    followup.subscribe({
      builder.file = it
      getLater().subscribe({
        subject.onNext(it)
        subject.onComplete()
      }, subject::onError)
    }, subject::onError)
  }

  /**
   * Initializes [FileConverter] with parameters from [builder]. Deletes file(s) based on these params.
   *
   * To remove:
   * * Whole cache
   *    * set [Cache.obtain] class parameter to [Cache::class.java][Cache].
   * * All objects within one class
   *    * set [Cache.obtain] class to desired class and [FilePresenterBuilder.ofIndex] to empty string ("")
   * * Single object
   *    * set [Cache.obtain] class to desired class and [FilePresenterBuilder.ofIndex] to desired file index
   */
  fun delete(): Boolean {
    return FileConverter(builder).delete()
  }

  /**
   * Wraps [delete] in [Single]. It should not throw an error, ever.
   *
   * @return Boolean which says whether delete process was successful.
   * False however mustn't necessarily mean that process failed altogether,
   * it might have deleted *some* files.
   */
  fun deleteLater(): Single<Boolean> {
    return Single.just(delete())
        .subscribeOn(Schedulers.io())
        .observeOn(AndroidSchedulers.mainThread())
  }

}