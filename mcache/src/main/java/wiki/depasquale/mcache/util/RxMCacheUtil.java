package wiki.depasquale.mcache.util;

import io.reactivex.Observable;
import io.reactivex.subjects.PublishSubject;
import wiki.depasquale.mcache.L;
import wiki.depasquale.mcache.core.IOHandler;
import wiki.depasquale.mcache.core.Threader;

/**
 * Created by diareuse on 10/04/2017. Yeah. Suck it.
 */

class RxMCacheUtil {

  public static <T> Observable<T> wrap(Observable<T> o, Class<T> cls,
      IOHandler handler, CharSequence id, boolean condition, boolean force) {
    PublishSubject<T> publishSubject = PublishSubject.create();
    try {
      return publishSubject;
    } finally {
      Threader.runOnNet(() -> {
        L.debug("Wrapped " + cls.getName() + " with condition " + condition + " and force " +
            force);
        T t = MCacheUtil.get(id, cls, handler);
        if (t != null && !condition) {
          publishSubject.onNext(t);
        }
        if (t == null || condition || force) {
          o.map(u -> {
            Threader.runOnNet(() -> MCacheUtil.save(u, u.getClass(), id, handler));
            return u;
          }).subscribe(publishSubject::onNext);
        }
      });
    }
  }

  public static <T> rx.Observable<T> wrap(rx.Observable<T> o, Class<T> cls, IOHandler handler,
      CharSequence id, boolean condition, boolean force) {
    rx.subjects.PublishSubject<T> publishSubject =
        rx.subjects.PublishSubject.create();
    try {
      return publishSubject;
    } finally {
      Threader.runOnNet(() -> {
        L.debug("Wrapped " + cls.getName() + " with condition " + condition + " and force " +
            force);
        T t = MCacheUtil.get(id, cls, handler);
        if (t != null && !condition) {
          publishSubject.onNext(t);
        }
        if (t == null || condition || force) {
          o.map(u -> {
            Threader.runOnNet(() -> MCacheUtil.save(u, t.getClass(), id, handler));
            return u;
          }).subscribe(publishSubject::onNext);
        }
      });
    }
  }
}
