package wiki.depasquale.mcache.util;

import io.reactivex.Observable;
import wiki.depasquale.mcache.MCache;
import wiki.depasquale.mcache.adapters.DefaultIOHandler;
import wiki.depasquale.mcache.core.FinishedListener;
import wiki.depasquale.mcache.core.IOHandler;
import wiki.depasquale.mcache.core.Threader;

/**
 * Created by diareuse on 10/04/2017. Yeah. Suck it.
 */

public class MCacheBuilder<T> {

  private final Class<T> cls;
  private IOHandler handler;
  private CharSequence identifier = MCache.DEFAULT_ID;
  private boolean precondition = false;
  private boolean force = false;

  @SuppressWarnings("unused") private MCacheBuilder() {
    throw new RuntimeException("This shall not be used!");
  }

  public MCacheBuilder(Class<T> cls) {
    this.cls = cls;
  }

  /**
   * Creates new <b>MCacheBuilder</b> with affinity to class given as parameter.
   *
   * @param cls Class of the object which needs to be saved/loaded.
   * @return new <b>MCacheBuilder</b>
   */
  public static <U> MCacheBuilder<U> request(Class<U> cls) {
    return new MCacheBuilder<>(cls);
  }

  /**
   * Sets <b>IOHandler</b> to handle upcoming situation. If not set {@link DefaultIOHandler} will be
   * used.
   *
   * @param handler Class of IOHandler. Custom or not it does not care.
   * @return building instance
   */
  public final MCacheBuilder<T> using(Class<? extends IOHandler> handler) {
    this.handler = MCache.getIOHandler(handler);
    return this;
  }

  /**
   * Sets identifier for saving/loading given class. It will be appended to file name. Simple yet
   * effective.
   *
   * @param identifier Preferably following this pattern "_somePostFix" or ".somePostFix". This is
   * just a suggestion.
   * @return building instance
   */
  public final MCacheBuilder<T> id(CharSequence identifier) {
    this.identifier = identifier;
    return this;
  }

  /**
   * Sets precondition for reading saved file. If it's true saved file will not be read, otherwise
   * it will. <b>If you are not using RxJava with this library you can freely skip this, it won't
   * have any effect.</b> Default is false.
   *
   * @param precondition Boolean representation of precondition
   * @return building instance
   */
  public final MCacheBuilder<T> precondition(boolean precondition) {
    this.precondition = precondition;
    return this;
  }

  /**
   * Sets whether it should forcefully update the data within later given Observable. If false given
   * Observable won't be subscribed to unless there's no saved data. <b>If you are not using RxJava
   * with this library you can freely skip this, it won't have any effect.</b> Default is false.
   *
   * @param force Boolean representation of precondition
   * @return building instance
   */
  public final MCacheBuilder<T> force(boolean force) {
    this.force = force;
    return this;
  }

  /**
   * Creates map around given observable with earlier predefined conditions.
   *
   * @param o Observable of matching class
   * @return The same observable
   */
  public final Observable<T> with(Observable<T> o) {
    if (handler == null) { using(DefaultIOHandler.class); }
    return RxMCacheUtil.wrap(o, cls, handler, identifier, precondition, force);
  }

  /**
   * Creates map around given observable with earlier predefined conditions.
   *
   * @param o Observable of matching class
   * @return The same observable
   */
  public final rx.Observable<T> with(rx.Observable<T> o) {
    if (handler == null) { using(DefaultIOHandler.class); }
    return RxMCacheUtil.wrap(o, cls, handler, identifier, precondition, force);
  }

  /**
   * Synchronously returns saved object with earlier predefined conditions.
   *
   * @return Corresponding object
   */
  public final T with() {
    if (handler == null) { using(DefaultIOHandler.class); }
    return handler.get(identifier, cls);
  }

  /**
   * Asynchronously returns saved object with earlier predefined conditions.
   *
   * @param listener Listener with corresponding class
   */
  public final void with(FinishedListener<T> listener) {
    Threader.runOnNet(() -> listener.onFinished(handler.get(identifier, cls)));
  }
}
