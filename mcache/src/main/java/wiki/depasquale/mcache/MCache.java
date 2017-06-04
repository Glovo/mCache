package wiki.depasquale.mcache;

import android.app.Application;
import android.content.Context;
import android.support.annotation.Nullable;
import java.lang.ref.WeakReference;
import java.util.HashMap;
import java.util.Map;
import wiki.depasquale.mcache.adapters.CacheIOHandler;
import wiki.depasquale.mcache.adapters.FilesIOHandler;
import wiki.depasquale.mcache.core.IOHandler;

/**
 * diareuse on 26.03.2017
 */

@SuppressWarnings("unused")
public class MCache {

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
   * Initializes MCache
   * IOHandler - {@link CacheIOHandler}
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
}
