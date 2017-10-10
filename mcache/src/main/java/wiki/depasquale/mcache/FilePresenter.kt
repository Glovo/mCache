package wiki.depasquale.mcache

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
    return Single.just(true)
        .observeOn(Schedulers.io())
        .map { getNow()!! }
        .observeOn(AndroidSchedulers.mainThread())
  }

  fun getLaterWithFollowup(followup: Observable<T>): Observable<T> {
    val subject = PublishSubject.create<T>()
    return subject
        .doOnSubscribe {
          getLater().subscribe({
            subject.onNext(it)
            followup(followup, subject)
          }, { followup(followup, subject) })
        }
  }

  private fun followup(followup: Observable<T>, subject: PublishSubject<T>) {
    followup.subscribe({
      builder.file = it
      getLater().subscribe(subject::onNext, subject::onError)
    }, subject::onError, subject::onComplete)
  }

  fun delete(): Boolean {
    return FileConverter(builder).delete()
  }

  fun deleteLater(): Single<Boolean> {
    return Single.just(true)
        .observeOn(Schedulers.io())
        .map { delete() }
        .observeOn(AndroidSchedulers.mainThread())
  }

}