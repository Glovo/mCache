package wiki.depasquale.mcache.core;

import android.support.annotation.NonNull;
import io.reactivex.Observable;
import wiki.depasquale.mcache.core.internal.FileParams;

/**
 * diareuse on 26.03.2017
 */

public interface IOHandler {

  /**
   * Creates object with class of type and params of FileParams.
   */
  <T> Observable<T> get(@NonNull Class<T> type, @NonNull FileParams params);

  /**
   * Saves object of class cls. <b>Unsafe on main thread.</b>
   */
  <T> void save(@NonNull T object, @NonNull FileParams params);

  /**
   * Cleans the mess.
   */
  void clean();

}
