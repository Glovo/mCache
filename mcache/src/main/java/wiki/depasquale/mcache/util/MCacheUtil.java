package wiki.depasquale.mcache.util;

import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import wiki.depasquale.mcache.adapters.DefaultIOHandler;
import wiki.depasquale.mcache.core.IOHandler;
import wiki.depasquale.mcache.core.Threader;

/**
 * Created by diareuse on 10/04/2017. Yeah. Suck it.
 */

class MCacheUtil {

  /**
   * Retrieves class and initializes it as object. This will happen synchronously on current thread.
   * I strongly encourage you to <b>NOT</b> call this method in main thread.
   *
   * @param cls class of desired object
   * @param <T> parameter class of desired object
   * @return retrieved object of class
   */
  @Nullable
  static <T> T get(@NonNull CharSequence id, Class<T> cls, IOHandler handler) {
    return handler.get(id, cls);
  }

  /**
   * Saves object (with cls name if {@link DefaultIOHandler} is used). As well as {@link
   * #get(CharSequence, Class, IOHandler)} do <b>NOT</b> call this in main thread.
   *
   * @param object object which is not extending any Realm object
   * @param cls class of object, primarily for file name
   * @param id Non null identifier for saving multiple instances of the same class
   * @param handler desired IOHandler for this operation
   * @param <T> parameter class of saving object
   */

  static <T> void save(T object, Class<?> cls, CharSequence id, IOHandler handler) {
    Threader.runOnNet(() -> {
      handler.save(object, id, cls);
    });
  }

  /**
   * @see IOHandler#clean()
   */
  static void clean(IOHandler handler) {
    handler.clean();
  }
}
