package wiki.depasquale.mcache.core;

import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import io.reactivex.Observable;
import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.functions.Consumer;
import io.reactivex.schedulers.Schedulers;
import kotlin.Unit;
import kotlin.jvm.functions.Function1;
import org.jetbrains.annotations.NotNull;
import wiki.depasquale.mcache.MCache;
import wiki.depasquale.mcache.adapters.FilesIOHandler;
import wiki.depasquale.mcache.core.internal.FileParams;
import wiki.depasquale.mcache.core.internal.FileParamsInternal;

/**
 * Created by diareuse on 10/04/2017. Yeah. Suck it.
 */

public class MCacheBuilder<T> {

  private FileParamsInternal<T> internalParams = new FileParamsInternal<>();

  @SuppressWarnings("unused")
  private MCacheBuilder() {
    throw new RuntimeException("This shall not be used!");
  }

  private MCacheBuilder(Class<T> cls) {
    internalParams.setRequestedClass(cls);
  }

  /**
   * Creates new <b>MCacheBuilder</b> with affinity to class given as parameter.
   *
   * @param cls Class of the object which needs to be saved/loaded.
   * @return new <b>MCacheBuilder</b>
   */
  @NotNull
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
  @SafeVarargs
  @NotNull
  public final MCacheBuilder<T> using(Class<? extends IOHandler>... handlers) {
    internalParams.getHandlers().clear();
    for (Class<? extends IOHandler> handler : handlers) {
      internalParams.getHandlers().add(MCache.getIOHandler(handler));
    }
    return this;
  }

  /**
   * Sets descriptor for saving/loading given class.
   *
   * @param descriptor Preferably following this pattern "_somePostFix" or ".somePostFix". This is
   * just a suggestion.
   * @return building instance
   */
  @NotNull
  public final MCacheBuilder<T> descriptor(String descriptor) {
    internalParams.setFileParams(new FileParams(descriptor));
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
  @NotNull
  public final MCacheBuilder<T> force(boolean force) {
    internalParams.setForce(force);
    return this;
  }

  /**
   * Overrides caching process to immediately return cached version so onNext method will be
   * effectively called twice. Default is true.
   *
   * @param pullIfNotNull Boolean representation of condition
   * @return building instance
   */
  @NotNull
  public final MCacheBuilder<T> pullIfNotNull(boolean pullIfNotNull) {
    internalParams.setReturnImmediately(pullIfNotNull);
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
  @NotNull
  public final MCacheBuilder<T> readWith(int position) {
    internalParams.setReadWith(position);
    return this;
  }

  @NotNull
  public final MCacheBuilder<T> params(FileParams params) {
    if (params != null) { internalParams.setFileParams(params); }
    return this;
  }

  /**
   * Creates map around given observable with earlier predefined conditions.
   *
   * @param o Observable of matching class
   * @return The same observable
   */
  @NotNull
  public final Observable<T> with(@Nullable Observable<T> o) {
    internalParams.setObservable(o);
    return FileParamsInternal.wrap(internalParams);
  }

  /**
   * Asynchronously returns saved object with earlier predefined conditions. First handler in list
   * will be used.
   *
   * @param listener Listener with corresponding class
   */
  public final void with(FinishedListener<T> listener, Consumer<Throwable> errorConsumer) {
    FileParamsInternal.checkParams(internalParams);
    Observable.just(internalParams.getHandlers().get(internalParams.getReadWith()))
        .observeOn(Schedulers.io())
        .map(handler -> handler
            .get(internalParams.getRequestedClass(), internalParams.getFileParams()))
        .observeOn(AndroidSchedulers.mainThread())
        .flatMap(it -> it)
        .subscribe(listener::onFinished, errorConsumer);
  }

  /**
   * Saves given object to file with predefined conditions. First handler in list will be used.
   *
   * @param object non null object
   */
  public final void save(@NonNull T object) {
    for (IOHandler handler : internalParams.getHandlers()) {
      handler.save(object, internalParams.getFileParams());
    }
  }

  /**
   * All handlers specified in {@link #using(Class[])} will be cleansed.
   *
   * @see IOHandler#remove(Class, FileParams)
   */
  public final void remove() {
    internalParams.getHandlers().forEach(it ->
        it.remove(internalParams.getRequestedClass(), internalParams.getFileParams()));
  }

  @NotNull
  public MCacheBuilder<T> listener(Function1<Boolean, Unit> listener) {
    internalParams.getFileParams().setListener(listener);
    return this;
  }

  @NotNull
  public MCacheBuilder<T> removeAll(boolean removeAll) {
    internalParams.getFileParams().setRemoveAll(removeAll);
    return this;
  }
}
