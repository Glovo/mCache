package wiki.depasquale.mcache;

import android.app.Application;
import android.content.Context;
import android.support.annotation.Nullable;
import java.lang.ref.WeakReference;

/**
 * diareuse on 26.03.2017
 */

@SuppressWarnings("unused")
public class MCache {

  static final String TAG = "mCacheLib";
  static boolean DEBUG = false;
  static String prefix = ".depasquale.wiki.";
  private static WeakReference<Context> context;
  private static IOHandler ioHandlerInstance;

  public static void setIOHandler(IOHandler ioHandler) {
    if (context != null) {
      Log.l("Warning captain! Setting ioHandler after context is NOT RECOMMENDED"
          + " and will not be saved.");
    } else {
      MCache.ioHandlerInstance = ioHandler;
    }
  }

  public static void setPrefix(String prefix) {
    if (context != null) {
      Log.l("Warning captain! Setting prefix after context is NOT RECOMMENDED!");
    }

    if (prefix != null) {
      if (prefix.startsWith(".") && prefix.endsWith(".") && prefix.length() >= 3) {
        MCache.prefix = prefix;
      } else {
        throw new IllegalArgumentException("Prefix have to start and end with a dot (.) "
            + "with minimum length of 3");
      }
    } else {
      throw new IllegalArgumentException("Prefix must not be null");
    }
  }

  public static void setDebug(boolean debug) {
    DEBUG = debug;
  }

  public static void with(Application context) {
    MCache.context = new WeakReference<>(context);

    if (ioHandlerInstance == null) {
      ioHandlerInstance = new DefaultIOHandler();
    }
  }

  @Nullable
  static Context get() {
    if (context != null) {
      return context.get();
    } else {
      return null;
    }
  }

  @Nullable
  public static <T> T get(Class<T> cls) {
    return ioHandlerInstance.get(cls);
  }

  public static <T> void save(T object, Class<?> cls) {
    ioHandlerInstance.save(object, cls);
  }

  public static <T> io.reactivex.Observable<T> wrapSave(io.reactivex.Observable<T> o) {
    return o.map(t -> {
      save(t, t.getClass());
      return t;
    });
  }

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

  public static <T> rx.Observable<T> wrapSave(rx.Observable<T> o) {
    return o.map(t -> {
      save(t, t.getClass());
      return t;
    });
  }

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
}
