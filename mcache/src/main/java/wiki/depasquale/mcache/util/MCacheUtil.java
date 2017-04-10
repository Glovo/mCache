package wiki.depasquale.mcache.util;

import static wiki.depasquale.mcache.MCache.DEFAULT_ID;
import static wiki.depasquale.mcache.MCache.sIOHandlerInstance;

import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import wiki.depasquale.mcache.adapters.DefaultIOHandler;
import wiki.depasquale.mcache.core.IOHandler;

/**
 * Created by diareuse on 10/04/2017. Yeah. Suck it.
 */

public class MCacheUtil {

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
    return get(DEFAULT_ID, cls);
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
    save(object, DEFAULT_ID, cls);
  }

  /**
   * @param id Non null identifier for retrieving multiple instances of the same class
   * @see #get(Class)
   */
  @Nullable
  public static <T> T get(@NonNull CharSequence id, Class<T> cls) {
    return sIOHandlerInstance.get(id, cls);
  }

  /**
   * @param id Non null identifier for saving multiple instances of the same class
   * @see #save(Object, Class)
   */
  public static <T> void save(T object, CharSequence id, Class<?> cls) {
    sIOHandlerInstance.save(object, id, cls);
  }

  /**
   * @see IOHandler#clean()
   */
  public static void clean() {
    sIOHandlerInstance.clean();
  }
}
