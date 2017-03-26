package wiki.depasquale.mcache;

/**
 * diareuse on 26.03.2017
 */

@SuppressWarnings("unused")
interface IOHandler {

  <T> T get(Class<T> cls);

  <T> void save(T object, Class<?> cls);

  void clean();

}
