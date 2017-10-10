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
            followup.subscribe(subject::onNext, subject::onError, subject::onComplete)
          }, {
            followup.subscribe(subject::onNext, subject::onError, subject::onComplete)
          })
        }
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