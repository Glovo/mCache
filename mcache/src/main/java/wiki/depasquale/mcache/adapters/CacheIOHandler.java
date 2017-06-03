package wiki.depasquale.mcache.adapters;

import android.support.annotation.NonNull;
import io.reactivex.Observable;
import wiki.depasquale.mcache.core.FileMap;
import wiki.depasquale.mcache.core.FileParams;
import wiki.depasquale.mcache.core.IOHandler;

// TODO: 01/06/2017 redo with new standard
public final class CacheIOHandler implements IOHandler {

  public CacheIOHandler() {}


  @Override
  @NonNull
  public <T> Observable<T> get(@NonNull Class<T> type, @NonNull FileParams params) {
    FileMap.Companion.forClass((Class<Object>) type, false);
    /*FileMap<FileParams<T>> fileMap = FileMap.forClass(params.getFileClass(), false);
    if (fileMap != null) {
      return fileMap.matching(params)
          .flatMapIterable(it -> it)
          .flatMap(it -> FileMap.getObjectObservable(fileMap, it))
          .observeOn(AndroidSchedulers.mainThread());
    }
    PublishSubject<T> empty = PublishSubject.create();
    return empty.doOnSubscribe(disposable -> empty.onError(new Throwable("No file was found.")));*/
    return Observable.empty();
  }

  @Override
  public <T> void save(@NonNull T object, @NonNull FileParams params) {
    /*FileMap<FileParams<T>> fileMap = FileMap.forClass(params.getFileClass(), false);
    if (fileMap != null) {
      Log.d("RxU", "saving...");
      FileMap.save(fileMap, object, params);
    } else {
      Log.d("RxU", "Map is null, the hell?");
    }*/
  }

  @Override
  public void clean() {

  }
}
