package wiki.depasquale.mcache;

import static wiki.depasquale.mcache.MCacheUtil.get;
import static wiki.depasquale.mcache.MCacheUtil.save;

import io.reactivex.Observable;

/**
 * Created by diareuse on 10/04/2017. Yeah. Suck it.
 */

public class RxMCacheUtil {

  /**
   * Creates map around {@link io.reactivex.Observable}. This map uses {@link
   * MCacheUtil#save(Object, Class)} inside {@link Threader#runOnNet(Runnable)} handler.
   *
   * @param o whatever observable you want to have cached and read with {@link
   * #wrapRead(io.reactivex.Observable, Class, CharSequence, boolean, boolean)}
   */
  public static <T> io.reactivex.Observable<T> wrapSave(io.reactivex.Observable<T> o,
      CharSequence id) {
    return o.map(t -> {
      Threader.runOnNet(() -> save(t, id, t.getClass()));
      return t;
    });
  }

  /**
   * Creates {@link io.reactivex.subjects.PublishSubject} returns it for you and finally reads
   * caches versions of this observable under specific <b>condition</b> or <b>force</b>. If cls was
   * found saved first {@link org.reactivestreams.Subscriber#onNext(Object)} will be invoked, if any
   * of params <b>condition</b> or <b>force</b> are true, given Observable is subscribed to. Safe to
   * call in main thread. Subscriber will be executed inside non-main thread, so feel free to use
   * {@link Threader#runOnUI(Runnable)} method.
   *
   * @param condition <b>false</b> of this parameter indicates that cached and current item won't
   * match and {@link org.reactivestreams.Subscriber#onNext(Object)} can be invoked twice.
   * @param force this parameter indicates whether this method should subscribe to given Observable
   * {@param o Parameter}.
   */
  public static <T> io.reactivex.Observable<T> wrapRead(io.reactivex.Observable<T> o, Class<T> cls,
      CharSequence id, boolean condition, boolean force) {
    io.reactivex.subjects.PublishSubject<T> publishSubject =
        io.reactivex.subjects.PublishSubject.create();
    try {
      return publishSubject;
    } finally {
      Threader.runOnNet(() -> {
        Log.debug("Wrapped " + cls.getName() + " with condition " + condition
            + " and force " + force);
        T t = get(id, cls);
        if (t != null && !condition) {
          publishSubject.onNext(t);
        }
        if (t == null || condition || force) {
          o.subscribe(publishSubject::onNext);
        }
      });
    }
  }

  /**
   * Efficient combination of {@link #wrapSave(Observable, CharSequence)} and {@link
   * #wrapRead(Observable, Class, CharSequence, boolean, boolean)} Observable result will be saved
   * only if saved object does not exist or condition is true or force is true
   */
  public static <T> io.reactivex.Observable<T> wrap(io.reactivex.Observable<T> o, Class<T> cls,
      CharSequence id, boolean condition, boolean force) {
    io.reactivex.subjects.PublishSubject<T> publishSubject =
        io.reactivex.subjects.PublishSubject.create();
    try {
      return publishSubject;
    } finally {
      Threader.runOnNet(() -> {
        Log.debug("Wrapped " + cls.getName() + " with condition " + condition
            + " and force " + force);
        T t = get(id, cls);
        if (t != null && !condition) {
          publishSubject.onNext(t);
        }
        if (t == null || condition || force) {
          wrapSave(o, id).subscribe(publishSubject::onNext);
        }
      });
    }
  }

  /**
   * RxJava2 version of {@link #wrapSave(io.reactivex.Observable, CharSequence)}
   *
   * @see #wrapSave(io.reactivex.Observable, CharSequence)
   */
  public static <T> rx.Observable<T> wrapSave(rx.Observable<T> o, CharSequence id) {
    return o.map(t -> {
      Threader.runOnNet(() -> save(t, id, t.getClass()));
      return t;
    });
  }

  /**
   * RxJava2 version of {@link #wrapRead(io.reactivex.Observable, Class, CharSequence, boolean,
   * boolean)}
   *
   * @see #wrapRead(io.reactivex.Observable, Class, CharSequence, boolean, boolean)
   */
  public static <T> rx.Observable<T> wrapRead(rx.Observable<T> o, Class<T> cls,
      CharSequence id, boolean condition, boolean force) {
    rx.subjects.PublishSubject<T> publishSubject =
        rx.subjects.PublishSubject.create();
    try {
      return publishSubject;
    } finally {
      Threader.runOnNet(() -> {
        Log.debug("Wrapped " + cls.getName() + " with condition " + condition
            + " and force " + force);
        T t = get(id, cls);
        if (t != null && !condition) {
          publishSubject.onNext(t);
        }
        if (t == null || condition || force) {
          o.subscribe(publishSubject::onNext);
        }
      });
    }
  }

  /**
   * RxJava2 version of {@link #wrap(Observable, Class, CharSequence, boolean, boolean)}
   *
   * @see #wrap(Observable, Class, CharSequence, boolean, boolean)
   */
  public static <T> rx.Observable<T> wrap(rx.Observable<T> o, Class<T> cls,
      CharSequence id, boolean condition, boolean force) {
    rx.subjects.PublishSubject<T> publishSubject =
        rx.subjects.PublishSubject.create();
    try {
      return publishSubject;
    } finally {
      Threader.runOnNet(() -> {
        Log.debug("Wrapped " + cls.getName() + " with condition " + condition
            + " and force " + force);
        T t = get(id, cls);
        if (t != null && !condition) {
          publishSubject.onNext(t);
        }
        if (t == null || condition || force) {
          wrapSave(o, id).subscribe(publishSubject::onNext);
        }
      });
    }
  }
}
