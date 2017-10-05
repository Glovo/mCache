package wiki.depasquale.mcache

import io.reactivex.Observable
import io.reactivex.schedulers.Schedulers
import io.reactivex.subjects.PublishSubject

class FilePresenter<T>(private val builder: FilePresenterBuilderInterface<T>) {

  fun now(): T? {
    FileConverter(builder)
    return builder.file
  }

  fun afterAWhile(): Observable<T> {
    val subject = PublishSubject.create<T>()
    return subject
      .subscribeOn(Schedulers.io())
      .doOnSubscribe {
        FileConverter(builder)
        builder.file?.let {
          subject.onNext(it)
        }
        subject.onComplete()
      }
  }

}