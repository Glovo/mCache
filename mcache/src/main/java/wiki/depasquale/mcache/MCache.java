package wiki.depasquale.mcache;

import android.app.Application;
import android.content.Context;
import android.support.annotation.Nullable;
import io.reactivex.Observable;
import java.lang.ref.WeakReference;

/**
 * diareuse on 26.03.2017
 */

@SuppressWarnings("unused")
public class MCache {

  static final String TAG = "mCacheLib";
  static boolean sDebug = false;
  static String sPrefix = ".wiki.depasquale.";
  private static WeakReference<Context> sContext;
  private static IOHandler sIOHandlerInstance;

  private MCache() {}

  private MCache(Application application) {
    MCache.sContext = new WeakReference<>(application);

    if (sIOHandlerInstance == null) {
      sIOHandlerInstance = new DefaultIOHandler();
    }
  }

  /**
   * Initializes MCache with default parameters.
   * Debug - off
   * Prefix - .wiki.depasquale.
   * IOHandler - {@link DefaultIOHandler}
   */
  public static MCache with(Application context) {
    return new MCache(context);
  }

  @Nullable
  static Context get() {
    if (sContext != null) {
      return sContext.get();
    } else {
      return null;
    }
  }

  /**
   * Retrieves class and initializes it as object. This will happen synchronously on current thread.
   * I strongly encourage you to <b>NOT</b> call this method in main thread.
   *
   * @param cls class of desired object
   * @param <T> parameter class of desired object
   * @return retrieved object of class
   */
  @Nullable
  public static <T> T get(Class<T> cls) {
    return sIOHandlerInstance.get(cls);
  }

  /**
   * Saves object (with cls name if {@link DefaultIOHandler} is used). As well as {@link
   * #get(Class)} do <b>NOT</b> call this in main thread.
   *
   * @param object object which is not extending any Realm object
   * @param cls class of object, primarily for file name
   * @param <T> parameter class of saving object
   */
  public static <T> void save(T object, Class<?> cls) {
    sIOHandlerInstance.save(object, cls);
  }

  /**
   * @see IOHandler#clean()
   */
  public static void clean() {
    sIOHandlerInstance.clean();
  }

  /**
   * Creates map around {@link io.reactivex.Observable}. This map uses {@link #save(Object, Class)}
   * inside {@link Threader#runOnNet(Runnable)} handler.
   *
   * @param o whatever observable you want to have cached and read with {@link
   * #wrapRead(io.reactivex.Observable, Class, boolean, boolean)}
   */
  public static <T> io.reactivex.Observable<T> wrapSave(io.reactivex.Observable<T> o) {
    return o.map(t -> {
      Threader.runOnNet(() -> save(t, t.getClass()));
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
      boolean condition, boolean force) {
    io.reactivex.subjects.PublishSubject<T> publishSubject =
        io.reactivex.subjects.PublishSubject.create();
    try {
      return publishSubject;
    } finally {
      Threader.runOnNet(() -> {
        Log.debug("Wrapped " + cls.getName() + " with condition " + condition
            + " and force " + force);
        T t = get(cls);
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
   * Efficient combination of {@link #wrapSave(Observable)} and {@link #wrapRead(Observable, Class,
   * boolean, boolean)} Observable result will be saved only if saved object does not exist or
   * condition is true or force is true
   */
  public static <T> io.reactivex.Observable<T> wrap(io.reactivex.Observable<T> o, Class<T> cls,
      boolean condition, boolean force) {
    io.reactivex.subjects.PublishSubject<T> publishSubject =
        io.reactivex.subjects.PublishSubject.create();
    try {
      return publishSubject;
    } finally {
      Threader.runOnNet(() -> {
        Log.debug("Wrapped " + cls.getName() + " with condition " + condition
            + " and force " + force);
        T t = get(cls);
        if (t != null && !condition) {
          publishSubject.onNext(t);
        }
        if (t == null || condition || force) {
          MCache.wrapSave(o).subscribe(publishSubject::onNext);
        }
      });
    }
  }

  /**
   * RxJava2 version of {@link #wrapSave(io.reactivex.Observable)}
   *
   * @see #wrapSave(io.reactivex.Observable)
   */
  public static <T> rx.Observable<T> wrapSave(rx.Observable<T> o) {
    return o.map(t -> {
      Threader.runOnNet(() -> save(t, t.getClass()));
      return t;
    });
  }

  /**
   * RxJava2 version of {@link #wrapRead(io.reactivex.Observable, Class, boolean, boolean)}
   *
   * @see #wrapRead(io.reactivex.Observable, Class, boolean, boolean)
   */
  public static <T> rx.Observable<T> wrapRead(rx.Observable<T> o, Class<T> cls,
      boolean condition, boolean force) {
    rx.subjects.PublishSubject<T> publishSubject =
        rx.subjects.PublishSubject.create();
    try {
      return publishSubject;
    } finally {
      Threader.runOnNet(() -> {
        Log.debug("Wrapped " + cls.getName() + " with condition " + condition
            + " and force " + force);
        T t = get(cls);
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
   * RxJava2 version of {@link #wrap(Observable, Class, boolean, boolean)}
   *
   * @see #wrap(Observable, Class, boolean, boolean)
   */
  public static <T> rx.Observable<T> wrap(rx.Observable<T> o, Class<T> cls,
      boolean condition, boolean force) {
    rx.subjects.PublishSubject<T> publishSubject =
        rx.subjects.PublishSubject.create();
    try {
      return publishSubject;
    } finally {
      Threader.runOnNet(() -> {
        Log.debug("Wrapped " + cls.getName() + " with condition " + condition
            + " and force " + force);
        T t = get(cls);
        if (t != null && !condition) {
          publishSubject.onNext(t);
        }
        if (t == null || condition || force) {
          MCache.wrapSave(o).subscribe(publishSubject::onNext);
        }
      });
    }
  }

  /**
   * Sets new IOHandler for managing incoming and outgoing I/O.
   *
   * @param ioHandler Overridden {@link DefaultIOHandler} class.
   * @return current instance
   */
  public MCache setIOHandler(IOHandler ioHandler) {
    MCache.sIOHandlerInstance = ioHandler;
    return this;
  }

  /**
   * Prefix have to start with . [dot] and end with . [dot]. This is only effective with {@link
   * DefaultIOHandler} otherwise it's up to you.
   *
   * @param prefix for saving files
   * @return current instance
   */
  public MCache setPrefix(String prefix) {
    if (sContext != null) {
      Log.l("Warning captain! Setting sPrefix after sContext is NOT RECOMMENDED!");
    }

    if (prefix != null) {
      if (prefix.startsWith(".") && prefix.endsWith(".") && prefix.length() >= 3) {
        MCache.sPrefix = prefix;
      } else {
        throw new IllegalArgumentException("Prefix have to start and end with a dot (.) "
            + "with minimum length of 3");
      }
    } else {
      throw new IllegalArgumentException("Prefix must not be null");
    }
    return this;
  }

  /**
   * Sets debugging mode.
   *
   * @param debug boolean representation
   * @return current instance
   */
  public MCache setDebug(boolean debug) {
    sDebug = debug;
    return this;
  }
}
