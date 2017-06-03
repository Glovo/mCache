package wiki.depasquale.mcache.core;

import android.support.annotation.NonNull;
import io.reactivex.Observable;
import wiki.depasquale.mcache.testing.FileParams;

/**
 * diareuse on 26.03.2017
 */

public interface IOHandler {

  /**
   * Creates object with class of param cls. <b>Unsafe on main thread.</b>
   */
  <T> Observable<T> get(@NonNull FileParams<T> params);

  /**
   * Saves object of class cls. <b>Unsafe on main thread.</b>
   */
  <T> void save(@NonNull T object, @NonNull FileParams<T> params);

  /**
   * Cleans mess which was created by {@link #save(Object, CharSequence, Class)} method.
   */
  void clean();

}
