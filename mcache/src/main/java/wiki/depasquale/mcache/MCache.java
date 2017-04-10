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

  public static final CharSequence DEFAULT_ID = "_default";
  static final String TAG = "mCacheLib";
  static boolean sDebug = false;
  static String sPrefix = ".wiki.depasquale.";
  static IOHandler sIOHandlerInstance;
  private static WeakReference<Context> sContext;

  private MCache() {
    throw new RuntimeException("This constructor shall not be used!");
  }

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
   * Sets new IOHandler for managing incoming and outgoing I/O.
   *
   * @param ioHandler Overridden {@link DefaultIOHandler} class.
   * @return current instance
   */
  public final MCache setIOHandler(IOHandler ioHandler) {
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

  /**
   * Sets debugging mode.
   *
   * @param debug boolean representation
   * @return current instance
   */
  public final MCache setDebug(boolean debug) {
    sDebug = debug;
    return this;
  }
}
