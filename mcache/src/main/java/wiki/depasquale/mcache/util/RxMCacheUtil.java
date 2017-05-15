package wiki.depasquale.mcache.util;

import io.reactivex.Observable;
import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.schedulers.Schedulers;
import io.reactivex.subjects.PublishSubject;
import java.util.List;
import wiki.depasquale.mcache.MCache;
import wiki.depasquale.mcache.core.IOHandler;

/**
 * Created by diareuse on 10/04/2017. Yeah. Suck it.
 */

class RxMCacheUtil {

  static <T> Observable<T> wrap(Observable<T> o, Class<T> cls,
      List<IOHandler> handlers, CharSequence id, boolean force, int readAt,
      boolean returnImmediately) {
    PublishSubject<T> publishSubject = PublishSubject.create();
    return publishSubject.doOnSubscribe(disposable -> Observable.just(cls)
        .observeOn(Schedulers.io())
        .flatMap(concreteClass -> {
          T concreteObject = getObject(concreteClass, id, handlers, readAt);
          if (concreteObject != null && returnImmediately) {
            Observable.just(concreteObject)
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(object -> {
                  publishSubject.onNext(object);
                  if (!force) { publishSubject.onComplete(); }
                });
          }
          if (concreteObject == null || force) {
            return o;
          } else {
            return Observable.just(concreteObject);
          }
        })
        .observeOn(Schedulers.io())
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
            MCache.sCatch ? Throwable::printStackTrace : publishSubject::onError,
            publishSubject::onComplete));
  }

  private static <R> R getObject(Class<R> cls, CharSequence id, List<IOHandler> handlers,
      int readAt) {
    return MCacheBuilder.request(cls)
        .id(id)
        .using(handlers.get(readAt).getClass())
        .with();
  }

  static <T> rx.Observable<T> wrap(rx.Observable<T> o, Class<T> cls,
      List<IOHandler> handlers, CharSequence id, boolean force, int readAt,
      boolean returnImmediately) {
    rx.subjects.PublishSubject<T> publishSubject =
        rx.subjects.PublishSubject.create();
    return publishSubject.doOnSubscribe(() -> rx.Observable.just(cls)
        .observeOn(rx.schedulers.Schedulers.io())
        .flatMap(concreteClass -> {
          T concreteObject = getObject(concreteClass, id, handlers, readAt);
          if (concreteObject != null && returnImmediately) {
            rx.Observable.just(concreteObject)
                .observeOn(rx.android.schedulers.AndroidSchedulers.mainThread())
                .subscribe(object -> {
                  publishSubject.onNext(object);
                  if (!force) { publishSubject.onCompleted(); }
                });
          }
          if (concreteObject == null || force) {
            return o;
          } else {
            return rx.Observable.just(concreteObject);
          }
        })
        .observeOn(rx.schedulers.Schedulers.io())
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
        .subscribe(publishSubject::onNext,
            MCache.sCatch ? Throwable::printStackTrace : publishSubject::onError,
            publishSubject::onCompleted));
  }
}
