package wiki.depasquale.mcache.adapters;

import android.support.annotation.NonNull;
import io.reactivex.Observable;
import wiki.depasquale.mcache.core.FileParams;
import wiki.depasquale.mcache.core.IOHandler;

// TODO: 01/06/2017 redo with new standard
public final class CacheIOHandler implements IOHandler {

  public CacheIOHandler() {}


  @Override public <T> Observable<T> get(@NonNull FileParams<T> params) {
    return null;
  }

  @Override public <T> void save(@NonNull T object, @NonNull FileParams<T> params) {

  }

  @Override public void clean() {

  }
}
