package wiki.depasquale.mcache;

/**
 * diareuse on 26.03.2017
 */

@SuppressWarnings("unused")
interface IOHandler {

  /**
   * Creates object with class of param cls. <b>Unsafe on main thread.</b>
   */
  <T> T get(Class<T> cls);

  /**
   * Saves object of class cls. <b>Unsafe on main thread.</b>
   */
  <T> void save(T object, Class<?> cls);

  /**
   * Cleans mess which was created by {@link #save(Object, Class)} method.
   */
  void clean();

}
