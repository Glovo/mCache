package wiki.depasquale.mcache;

import android.app.Application;
import android.content.Context;
import android.support.annotation.Nullable;
import java.lang.ref.WeakReference;
import java.util.HashMap;
import java.util.Map;
import wiki.depasquale.mcache.adapters.FilesIOHandler;
import wiki.depasquale.mcache.core.IOHandler;

/**
 * diareuse on 26.03.2017
 */

@SuppressWarnings("unused")
public class MCache {

  public static final CharSequence DEFAULT_ID = "_default";
  static final String TAG = "mCacheLib";
  public static String sPrefix = ".wiki.depasquale.";
  private static Map<Class<?>, IOHandler> sIOHandlerInstance;
  private static WeakReference<Context> sContext;

  private MCache() {
    throw new RuntimeException("This constructor shall not be used!");
  }

  private MCache(Application application) {
    MCache.sContext = new WeakReference<>(application);
    initMap();
  }

  private static void initMap() {
    if (sIOHandlerInstance == null) {
      sIOHandlerInstance = new HashMap<>(0);
    }
  }

  /**
   * Initializes MCache with default parameters.
   * Debug - off
   * Prefix - .wiki.depasquale.
   * IOHandler - {@link FilesIOHandler}
   */
  public static MCache with(Application context) {
    return new MCache(context);
  }

  @Nullable
  public static Context get() {
    if (sContext != null) {
      return sContext.get();
    } else {
      return null;
    }
  }

  public static IOHandler getIOHandler(Class<? extends IOHandler> cls) {
    initMap();
    if (cls == null) { cls = FilesIOHandler.class; }
    if (sIOHandlerInstance.containsKey(cls)) {
      return sIOHandlerInstance.get(cls);
    } else {
      try {
        IOHandler interfaceType = cls.newInstance();
        sIOHandlerInstance.put(cls, interfaceType);
        return interfaceType;
      } catch (InstantiationException e) {
        throw new RuntimeException(cls.getName() + " cannot be instantiated.");
      } catch (IllegalAccessException e) {
        throw new RuntimeException(cls.getName() + " probably does not contain constructor.");
      }
    }
  }

  @SafeVarargs public static void clean(Class<? extends IOHandler>... cls) {
    if (cls == null || cls.length == 0) {
      getIOHandler(null).clean();
    } else {
      for (Class<? extends IOHandler> handler : cls) {
        getIOHandler(handler).clean();
      }
    }
  }

  /**
   * Prefix have to start with . [dot] and end with . [dot]. This is only effective with {@link
   * FilesIOHandler} otherwise it's up to you.
   *
   * @param prefix for saving files
   * @return current instance
   */
  public final MCache setPrefix(String prefix) {
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
}
