package wiki.depasquale.mcache.adapters;

import android.support.annotation.NonNull;
import io.reactivex.Observable;
import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.subjects.PublishSubject;
import wiki.depasquale.mcache.core.FileMap;
import wiki.depasquale.mcache.core.FileParams;
import wiki.depasquale.mcache.core.IOHandler;

// TODO: 01/06/2017 redo with new standard
public final class CacheIOHandler implements IOHandler {

  public CacheIOHandler() {}


  @Override public <T> Observable<T> get(Class<T> cls, @NonNull FileParams params) {
    FileMap<T> fileMap = FileMap.forFolder(params.getFileClass(), true);
    if (fileMap != null) {
      return fileMap.matching(params)
          .flatMapIterable(it -> it)
          .flatMap(fileMap::getObject)
          .observeOn(AndroidSchedulers.mainThread());
    }
    PublishSubject<T> empty = PublishSubject.create();
    return empty/*.doOnSubscribe(disposable -> empty.onError(new Throwable("No file was found.")))*/;
  }

  @Override public <T> void save(@NonNull T object, @NonNull FileParams params) {
    FileMap<T> fileMap = FileMap.forFolder(params.getFileClass(), false);
    if (fileMap != null) {
      fileMap.save(object, params);
    }
  }

  @Override public void clean() {

  }
}
