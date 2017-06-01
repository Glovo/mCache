package wiki.depasquale.mcache.adapters;

import android.content.Context;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import com.google.gson.Gson;
import io.reactivex.Observable;
import io.reactivex.subjects.PublishSubject;
import wiki.depasquale.mcache.BuildConfig;
import wiki.depasquale.mcache.MCache;
import wiki.depasquale.mcache.core.FileMap;
import wiki.depasquale.mcache.core.FileParams;
import wiki.depasquale.mcache.core.IOHandler;

public final class FilesIOHandler implements IOHandler {

  private static Gson gson;

  public FilesIOHandler() {}

  private static Gson getGson() {
    if (gson == null) {
      gson = new Gson();
    }
    return gson;
  }

  @SuppressWarnings("ReturnOfNull")
  @Nullable
  private static <T> T reconstruct(String string, Class<T> cls) {
    if (string == null) {
      return null;
    } else {
      try {
        return getGson().fromJson(string, cls);
      } catch (Exception e) {
        if (BuildConfig.DEBUG) { e.printStackTrace(); }
        return null;
      }
    }
  }

  @Override
  @Nullable
  public final <T> Observable<T> get(@NonNull FileParams<T> params) {
    Class<T> cls = params.getFileClass();
    FileMap<T> fileMap = FileMap.forFolder(cls, false);
    if (fileMap != null) {
      return fileMap.matching(params)
          .flatMapIterable(it -> it)
          .map(fileMap::getObject);
    }
    PublishSubject<T> empty = PublishSubject.create();
    return empty.doOnSubscribe(disposable -> empty.onError(new Throwable("No file was found.")));
  }

  @Override
  public final <T> void save(@NonNull T object, @NonNull FileParams<T> params) {
    /*String filename = String.format("%s%s%s",
        MCache.sPrefix,
        Base64.encodeToString(cls.getSimpleName().getBytes(), Base64.DEFAULT)
            .trim().replace("=", ""),
        identifier);
    try {
      Context context = MCache.get();
      if (context == null) {
        return;
      }
      FileOutputStream fos = context.openFileOutput(filename, Context.MODE_PRIVATE);
      fos.write(getGson().toJson(object).getBytes());
      fos.close();
    } catch (IOException e) {
      if (BuildConfig.DEBUG) { e.printStackTrace(); }
    }*/
  }

  @Override
  public final void clean() {
    Context context = MCache.get();
    if (context == null) {
      return;
    }
    for (String file : context.fileList()) {
      if (file.startsWith(MCache.sPrefix)) {
        context.deleteFile(file);
      }
    }
  }
}
