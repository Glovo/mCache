package wiki.depasquale.mcache.util;

import io.reactivex.Observable;
import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.schedulers.Schedulers;
import io.reactivex.subjects.PublishSubject;
import java.util.List;
import wiki.depasquale.mcache.core.IOHandler;

/**
 * Created by diareuse on 10/04/2017. Yeah. Suck it.
 */

class RxMCacheUtil {

  static <T> Observable<T> wrap(Observable<T> o, Class<T> cls,
      List<IOHandler> handlers, CharSequence id, boolean force, int readAt) {
    PublishSubject<T> publishSubject = PublishSubject.create();
    try {
      return publishSubject;
    } finally {
      Observable.just(cls)
          .observeOn(Schedulers.io())
          .flatMap(tClass -> {
            T t = getObject(tClass, id, handlers, readAt);
            if (t == null || force) {
              return o;
            } else {
              return Observable.just(t);
            }
          })
          .map(rawObject -> {
            for (IOHandler handler : handlers) {
              MCacheBuilder.request(rawObject.getClass())
                  .id(id)
                  .using(handler.getClass())
                  .save(rawObject);
            }
            return rawObject;
          })
          .observeOn(AndroidSchedulers.mainThread())
          .subscribe(publishSubject::onNext,
              Throwable::printStackTrace,
              publishSubject::onComplete);
    }
  }

  private static <R> R getObject(Class<R> cls, CharSequence id,
      List<IOHandler> handlers,
      int readAt) {
    return MCacheBuilder.request(cls)
        .id(id)
        .using(handlers.get(readAt).getClass())
        .with();
  }

  static <T> rx.Observable<T> wrap(rx.Observable<T> o, Class<T> cls,
      List<IOHandler> handlers, CharSequence id, boolean force, int readAt) {
    rx.subjects.PublishSubject<T> publishSubject =
        rx.subjects.PublishSubject.create();
    try {
      return publishSubject;
    } finally {
      rx.Observable.just(cls)
          .observeOn(rx.schedulers.Schedulers.io())
          .flatMap(tClass -> {
            T t = getObject(tClass, id, handlers, readAt);
            if (t == null || force) {
              return o;
            } else {
              return rx.Observable.just(t);
            }
          })
          .map(rawObject -> {
            for (IOHandler handler : handlers) {
              MCacheBuilder.request(rawObject.getClass())
                  .id(id)
                  .using(handler.getClass())
                  .save(rawObject);
            }
            return rawObject;
          })
          .observeOn(rx.android.schedulers.AndroidSchedulers.mainThread())
          .onBackpressureBuffer()
          .subscribe(publishSubject::onNext,
              Throwable::printStackTrace,
              publishSubject::onCompleted);
    }
  }
}
