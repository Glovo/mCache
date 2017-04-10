package wiki.depasquale.mcache.core;

/**
 * diareuse on 26.03.2017
 */

public interface IOHandler {

  /**
   * Creates object with class of param cls. <b>Unsafe on main thread.</b>
   */
  <T> T get(CharSequence identifier, Class<T> cls);

  /**
   * Saves object of class cls. <b>Unsafe on main thread.</b>
   */
  <T> void save(T object, CharSequence identifier, Class<?> cls);

  /**
   * Cleans mess which was created by {@link #save(Object, CharSequence, Class)} method.
   */
  void clean();

}
