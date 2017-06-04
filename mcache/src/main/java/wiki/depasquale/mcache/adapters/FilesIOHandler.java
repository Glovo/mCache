package wiki.depasquale.mcache.adapters;

import android.support.annotation.NonNull;
import io.reactivex.Observable;
import wiki.depasquale.mcache.core.FileMap;
import wiki.depasquale.mcache.core.FileParams;
import wiki.depasquale.mcache.core.IOHandler;

public final class FilesIOHandler implements IOHandler {

  public FilesIOHandler() {}

  @Override
  @NonNull
  public <T> Observable<T> get(@NonNull Class<T> type, @NonNull FileParams params) {
    FileMap map = FileMap.Companion.forClass(type, false);
    return map.findObjectByParams(type, params);
    /*FileMap<FileParams<T>> fileMap = FileMap.forClass(params.getFileClass(), false);
    if (fileMap != null) {
      return fileMap.matching(params)
          .flatMapIterable(it -> it)
          .flatMap(it -> FileMap.getObjectObservable(fileMap, it))
          .observeOn(AndroidSchedulers.mainThread());
    }
    PublishSubject<T> empty = PublishSubject.create();
    return empty.doOnSubscribe(disposable -> empty.onError(new Throwable("No file was found.")));*/
  }

  @Override
  public <T> void save(@NonNull T object, @NonNull FileParams params) {
    FileMap.Companion.forClass(object.getClass(), false)
        .saveObjectWithParams(object, params);
    /*FileMap<FileParams<T>> fileMap = FileMap.forClass(params.getFileClass(), false);
    if (fileMap != null) {
      Log.d("RxU", "saving...");
      FileMap.save(fileMap, object, params);
    } else {
      Log.d("RxU", "Map is null, the hell?");
    }*/
  }

  @Override
  public final void clean() {
    /*Context context = MCache.get();
    if (context == null) {
      return;
    }
    for (String file : context.fileList()) {
      if (file.startsWith(MCache.sPrefix)) {
        context.deleteFile(file);
      }
    }*/
  }
}
