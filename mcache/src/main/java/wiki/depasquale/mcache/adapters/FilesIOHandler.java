package wiki.depasquale.mcache.adapters;

import android.support.annotation.NonNull;
import io.reactivex.Observable;
import wiki.depasquale.mcache.core.IOHandler;
import wiki.depasquale.mcache.testing.FileParams;

public final class FilesIOHandler implements IOHandler {

  public FilesIOHandler() {}

  @Override
  @NonNull
  public final <T> Observable<T> get(@NonNull FileParams<T> params) {
    /*FileMap<FileParams<T>> fileMap = FileMap.forClass(params.getFileClass(), false);
    if (fileMap != null) {
      return fileMap.matching(params)
          .flatMapIterable(it -> it)
          .flatMap(it -> FileMap.getObjectObservable(fileMap, it))
          .observeOn(AndroidSchedulers.mainThread());
    }
    PublishSubject<T> empty = PublishSubject.create();
    return empty.doOnSubscribe(disposable -> empty.onError(new Throwable("No file was found.")));*/
    return null;
  }

  @Override
  public final <T> void save(@NonNull T object, @NonNull FileParams<T> params) {
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
