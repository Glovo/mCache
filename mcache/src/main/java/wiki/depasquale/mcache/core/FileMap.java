package wiki.depasquale.mcache.core;

import android.content.Context;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.util.Base64;
import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;
import io.reactivex.Observable;
import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.schedulers.Schedulers;
import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.lang.reflect.Type;
import java.text.Normalizer;
import java.util.ArrayList;
import java.util.List;
import wiki.depasquale.mcache.BuildConfig;
import wiki.depasquale.mcache.MCache;

/**
 * Created by diareuse on 01/06/2017. Yeah. Suck it.
 */

public class FileMap<T> {

  private static final String MAP_NAME = "map.fmp";
  private static Gson gson;
  public List<FileParams<T>> files = new ArrayList<>(0);

  @Nullable
  public static <P> FileMap<P> forFolder(@NonNull Class<P> cls, boolean isCache) {
    String folderName = getFolderName(cls);
    Context context = MCache.get();
    if (context == null) {
      return null;
    }
    File dir = isCache ? context.getCacheDir() : context.getFilesDir();
    File[] children = dir.listFiles();
    if (children != null) {
      for (File child : children) {
        if (child.isDirectory() && folderName.equals(child.getName())) {
          return buildFileMapForFolder(child);
        }
      }
    }

    File file = new File(dir, folderName);
    if (file.exists()) {
      if (file.mkdir()) {
        File map = new File(file, MAP_NAME);
        if (map.exists()) {
          return buildFileMapForFolder(file);
        }
      }
    }

    return null;
  }

  private static <P> String getFolderName(Class<P> cls) {
    String tempName = Base64.encodeToString(cls.getSimpleName().getBytes(), Base64.DEFAULT).trim()
        .replace("=", "");
    tempName = Normalizer.normalize(tempName, Normalizer.Form.NFD);
    return tempName;
  }

  @Nullable
  private static <P> FileMap<P> buildFileMapForFolder(@NonNull File child) {
    String data = null;
    for (File potentialMap : child.listFiles()) {
      if (MAP_NAME.equals(potentialMap.getName())) {
        data = readFile(potentialMap);
        break;
      }
    }
    return reconstruct(data, new TypeToken<FileMap<P>>() {}.getType());
  }

  private static Gson getGson() {
    if (gson == null) {
      gson = new Gson();
    }
    return gson;
  }

  private static <T> T reconstruct(String string, Type cls) {
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

  private static String readFile(File fileToRead) {
    try {
      FileInputStream in = new FileInputStream(fileToRead);
      InputStreamReader inputStreamReader = new InputStreamReader(in);
      BufferedReader bufferedReader = new BufferedReader(inputStreamReader);
      StringBuilder sb = new StringBuilder(0);
      String line;
      while ((line = bufferedReader.readLine()) != null) {
        sb.append(line);
      }
      inputStreamReader.close();
      return sb.toString();
    } catch (IOException e) {
      if (BuildConfig.DEBUG) { e.printStackTrace(); }
    }
    return null;
  }

  public Observable<List<FileParams<T>>> similar(FileParams<T> params) {
    return Observable.just(files)
        .observeOn(Schedulers.computation())
        .flatMapIterable(items -> items)
        .filter(item -> FileParams.compare(item, params) > 0)
        .toList()
        .toObservable()
        .observeOn(AndroidSchedulers.mainThread());
  }

  public Observable<List<FileParams<T>>> matching(FileParams<T> params) {
    return Observable.just(files)
        .observeOn(Schedulers.computation())
        .flatMapIterable(items -> items)
        .filter(item -> FileParams.compare(item, params) == FileParams.MATCHING)
        .toList()
        .toObservable()
        .observeOn(AndroidSchedulers.mainThread());
  }

  public T getObject(FileParams<T> fPam) {
    // TODO: 01/06/2017 fill
    return null;
  }
}
