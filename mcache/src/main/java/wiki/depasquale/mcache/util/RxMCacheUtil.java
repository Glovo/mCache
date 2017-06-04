package wiki.depasquale.mcache.util;

import android.support.annotation.Nullable;
import android.util.Log;
import io.reactivex.Observable;
import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.disposables.Disposable;
import io.reactivex.schedulers.Schedulers;
import io.reactivex.subjects.PublishSubject;
import java.util.List;
import wiki.depasquale.mcache.core.FileParams;
import wiki.depasquale.mcache.core.IOHandler;

/**
 * Created by diareuse on 10/04/2017. Yeah. Suck it.
 */

class RxMCacheUtil {

  /*static <T> Observable<T> wrap(Observable<T> o, Class<T> cls,
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
  }*/

  public static <T> Observable<T> wrap(@Nullable Observable<T> o, List<IOHandler> handlers,
      FileParams params, Class<T> cls) {
    if (o == null) { o = Observable.empty(); }
    Observable<T> finalO = o.map(it -> {
      for (IOHandler handler : handlers) {
        Log.d("RxU", "saved");
        handler.save(it, params);
      }
      return it;
    }).observeOn(AndroidSchedulers.mainThread());
    PublishSubject<T> publishSubject = PublishSubject.create();
    final Disposable[] requestDisposable = {null};
    return publishSubject.doOnSubscribe(disposable -> {
      Observable.just(handlers)
          .doOnSubscribe(it -> {
            if (requestDisposable[0] == null) {
              Log.d("RxU", "subscribed to o");
              requestDisposable[0] = finalO.subscribe(
                  publishSubject::onNext,
                  publishSubject::onError,
                  publishSubject::onComplete
              );
            }
          })
          .observeOn(Schedulers.io())
          .flatMapIterable(it -> it)
          .flatMap(it -> it.get(cls, params))
          .onErrorResumeNext(throwable -> {
            throwable.printStackTrace();
            if (requestDisposable[0] == null) {
              Log.d("RxU", "returned o");
              return finalO;
            }
            Log.d("RxU", "returned empty observable");
            return Observable.empty();
          })
          .observeOn(AndroidSchedulers.mainThread())
          .subscribe(
              publishSubject::onNext,
              publishSubject::onError,
              publishSubject::onComplete
          );

    });
  }
}
