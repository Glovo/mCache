package wiki.depasquale.mcache.util;

import android.support.annotation.NonNull;
import io.reactivex.Observable;
import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.schedulers.Schedulers;
import java.util.ArrayList;
import java.util.List;
import wiki.depasquale.mcache.MCache;
import wiki.depasquale.mcache.adapters.FilesIOHandler;
import wiki.depasquale.mcache.core.FinishedListener;
import wiki.depasquale.mcache.core.IOHandler;

/**
 * Created by diareuse on 10/04/2017. Yeah. Suck it.
 */

public class MCacheBuilder<T> {

  private final Class<T> cls;
  private List<IOHandler> handlers = new ArrayList<>(0);
  private CharSequence identifier = MCache.DEFAULT_ID;
  private boolean force = false;
  private int readPosition = 0;
  private boolean pullIfNotNull = true;

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
   * Sets <b>IOHandler</b> to handle upcoming situation. If not set {@link FilesIOHandler} will be
   * used.
   *
   * @param handlers Classes of IOHandler. Custom or not, it does not care.
   * @return building instance
   */
  @SafeVarargs public final MCacheBuilder<T> using(Class<? extends IOHandler>... handlers) {
    this.handlers.clear();
    for (Class<? extends IOHandler> handler : handlers) {
      this.handlers.add(MCache.getIOHandler(handler));
    }
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
   * Overrides caching process to immediately return cached version so onNext method will be
   * effectively called twice. Default is true.
   *
   * @param pullIfNotNull Boolean representation of condition
   * @return building instance
   */
  public final MCacheBuilder<T> pullIfNotNull(boolean pullIfNotNull) {
    this.pullIfNotNull = pullIfNotNull;
    return this;
  }

  /**
   * Indicates with which handler should it read values. This is extremely useful if you input more
   * than one handler to {@link #using(Class[])} method. First handler has 0 index.
   *
   * @param position valid position
   * @return building instance
   * @throws IllegalArgumentException when position is greater or equal to number of handlers
   */
  public final MCacheBuilder<T> readWith(int position) {
    if (position < handlers.size()) { this.readPosition = position; } else {
      throw new IllegalArgumentException("Position must not be greater than number of handlers");
    }
    return this;
  }

  /**
   * Creates map around given observable with earlier predefined conditions.
   *
   * @param o Observable of matching class
   * @return The same observable
   */
  public final Observable<T> with(Observable<T> o) {
    if (handlers.isEmpty()) { using(FilesIOHandler.class); }
    return RxMCacheUtil.wrap(o, cls, handlers, identifier, force, readPosition, pullIfNotNull);
  }

  /**
   * Creates map around given observable with earlier predefined conditions.
   *
   * @param o Observable of matching class
   * @return The same observable
   */
  public final rx.Observable<T> with(rx.Observable<T> o) {
    if (handlers.isEmpty()) { using(FilesIOHandler.class); }
    return RxMCacheUtil.wrap(o, cls, handlers, identifier, force, readPosition, pullIfNotNull);
  }

  /**
   * Synchronously returns saved object with earlier predefined conditions. First handler in list
   * will be used.
   *
   * @return Corresponding object
   */
  public final T with() {
    if (handlers.isEmpty()) { using(FilesIOHandler.class); }
    return handlers.get(0).get(identifier, cls);
  }

  /**
   * Asynchronously returns saved object with earlier predefined conditions. First handler in list
   * will be used.
   *
   * @param listener Listener with corresponding class
   */
  public final void with(FinishedListener<T> listener) {
    Observable.just(handlers.get(0))
        .observeOn(Schedulers.io())
        .map(handler -> handler.get(identifier, cls))
        .observeOn(AndroidSchedulers.mainThread())
        .subscribe(listener::onFinished);
  }

  /**
   * Saves given object to file with predefined conditions. First handler in list will be used.
   *
   * @param object non null object
   */
  public final void save(@NonNull Object object) {
    if (handlers.isEmpty()) { using(FilesIOHandler.class); }
    handlers.get(0).save(object, identifier, cls);
  }

  /**
   * All handlers specified in {@link #using(Class[])} will be cleansed.
   *
   * @see IOHandler#clean()
   */
  public final void clean() {
    if (handlers.isEmpty()) { using(FilesIOHandler.class); }
    for (IOHandler handler : handlers) { handler.clean(); }
  }
}
