package wiki.depasquale.mcache.util;

import io.reactivex.Observable;
import io.reactivex.subjects.PublishSubject;
import java.util.List;
import wiki.depasquale.mcache.core.IOHandler;
import wiki.depasquale.mcache.core.Threader;

/**
 * Created by diareuse on 10/04/2017. Yeah. Suck it.
 */

class RxMCacheUtil {

  static <T> Observable<T> wrap(Observable<T> o, Class<T> cls,
      List<IOHandler> handlers, CharSequence id, boolean condition, boolean force, int readAt) {
    PublishSubject<T> publishSubject = PublishSubject.create();
    try {
      return publishSubject;
    } finally {
      Threader.runOnNet(() -> {
        T t = MCacheBuilder.request(cls)
            .id(id)
            .using(handlers.get(readAt).getClass())
            .with();
        if (t != null && !condition) {
          publishSubject.onNext(t);
        }
        if (t == null || condition || force) {
          o.map(u -> {
            Threader.runOnNet(() -> {
              for (IOHandler handler : handlers) {
                MCacheBuilder.request(u.getClass())
                    .id(id)
                    .using(handler.getClass())
                    .save(u);
              }
            });
            return u;
          }).subscribe(publishSubject::onNext);
        }
      });
    }
  }

  static <T> rx.Observable<T> wrap(rx.Observable<T> o, Class<T> cls,
      List<IOHandler> handlers, CharSequence id, boolean condition, boolean force, int readAt) {
    rx.subjects.PublishSubject<T> publishSubject =
        rx.subjects.PublishSubject.create();
    try {
      return publishSubject;
    } finally {
      Threader.runOnNet(() -> {
        T t = MCacheBuilder.request(cls)
            .id(id)
            .using(handlers.get(readAt).getClass())
            .with();
        if (t != null && !condition) {
          publishSubject.onNext(t);
        }
        if (t == null || condition || force) {
          o.map(u -> {
            Threader.runOnNet(() -> {
              for (IOHandler handler : handlers) {
                MCacheBuilder.request(u.getClass())
                    .id(id)
                    .using(handler.getClass())
                    .save(u);
              }
            });
            return u;
          }).subscribe(publishSubject::onNext);
        }
      });
    }
  }
}
