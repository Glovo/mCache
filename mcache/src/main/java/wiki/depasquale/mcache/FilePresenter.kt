package wiki.depasquale.mcache

import io.reactivex.Observable
import io.reactivex.schedulers.Schedulers
import io.reactivex.subjects.PublishSubject

class FilePresenter<T>(private val builder: FilePresenterBuilderInterface<T>) {

  fun getNow(): T? {
    FileConverter(builder).run()
    return builder.file
  }

  fun getLater(): Observable<T> {
    val subject = PublishSubject.create<T>()
    return subject
      .subscribeOn(Schedulers.io())
      .doOnSubscribe {
        getNow()?.let {
          subject.onNext(it)
        }
        subject.onComplete()
      }
  }

  fun getLaterWithFollowup(followup: Observable<T>): Observable<T> {
    val subject = PublishSubject.create<T>()
    return subject
      .subscribeOn(Schedulers.io())
      .doOnSubscribe {
        getNow()?.let {
          subject.onNext(it)
        }
        followup.subscribe({
          subject.onNext(it)
        }, subject::onError, subject::onComplete)
      }
  }

  fun delete(): Boolean {
    return FileConverter(builder).delete()
  }

  fun deleteLater(): Observable<Boolean> {
    val subject = PublishSubject.create<Boolean>()
    return subject
      .subscribeOn(Schedulers.io())
      .doOnSubscribe {
        subject.onNext(delete())
        subject.onComplete()
      }
  }

}