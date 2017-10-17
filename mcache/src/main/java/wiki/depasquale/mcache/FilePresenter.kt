package wiki.depasquale.mcache

import io.reactivex.Flowable
import io.reactivex.Observable
import io.reactivex.Single
import io.reactivex.android.schedulers.AndroidSchedulers
import io.reactivex.schedulers.Schedulers
import io.reactivex.subjects.PublishSubject

class FilePresenter<T>(private val builder: FilePresenterBuilderInterface<T>) {

  fun getNow(): T? {
    FileConverter(builder).run()
    return builder.file
  }

  fun getLater(): Single<T> {
    return Single.just(getNow()!!)
        .subscribeOn(Schedulers.io())
        .observeOn(AndroidSchedulers.mainThread())
  }

  fun getAll(): List<T> {
    FileConverter(builder).fetchAll()
    return builder.files
  }

  fun getAllLater(): Flowable<T> {
    return Flowable.just(getAll())
        .flatMapIterable { it }
        .subscribeOn(Schedulers.io())
        .observeOn(AndroidSchedulers.mainThread())
  }

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

  fun delete(): Boolean {
    return FileConverter(builder).delete()
  }

  fun deleteLater(): Single<Boolean> {
    return Single.just(delete())
        .subscribeOn(Schedulers.io())
        .observeOn(AndroidSchedulers.mainThread())
  }

}