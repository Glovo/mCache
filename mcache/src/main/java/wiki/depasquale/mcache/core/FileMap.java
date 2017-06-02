package wiki.depasquale.mcache.core;

import android.content.Context;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.util.Base64;
import android.util.Log;
import android.util.Pair;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.annotations.Expose;
import com.google.gson.reflect.TypeToken;
import io.reactivex.Observable;
import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.schedulers.Schedulers;
import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.lang.reflect.Modifier;
import java.lang.reflect.Type;
import java.text.Normalizer;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import wiki.depasquale.mcache.BuildConfig;
import wiki.depasquale.mcache.MCache;

/**
 * Created by diareuse on 01/06/2017. Yeah. Suck it.
 */

public class FileMap<T> {

  private static final String MAP_NAME = "map.fmp";
  private static Gson gson;
  @Expose List<FileParams> files = new ArrayList<>(0);
  private File rootFolder;

  private FileMap(File parent) {
    rootFolder = parent;
    boolean exists = false;
    for (File file : parent.listFiles()) {
      if (MAP_NAME.equals(file.getName())) {
        exists = true;
        break;
      }
    }
    if (!exists) {
      updateMap(null);
    }
  }

  @Nullable
  public static <P> FileMap<P> forFolder(@NonNull Class cls, boolean isCache) {
    String folderName = getFolderName(cls);
    Context context = MCache.get();
    if (context == null) {
      Log.d("RxU", "Context is null");
      return null;
    }
    File dir = isCache ? context.getCacheDir() : context.getFilesDir();
    File[] children = dir.listFiles();
    if (children != null) {
      for (File child : children) {
        if (child.isDirectory() && folderName.equals(child.getName())) {
          Log.d("RxU", "Is directory!");
          return buildFileMapForFolder(child);
        }
      }
    }

    File file = new File(dir, folderName);
    file.mkdirs();
    return buildFileMapForFolder(file);
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
    if (data == null) {
      Log.d("RxU", "Data are null");
      return new FileMap<>(child);
    } else {
      Log.d("RxU", data);
      FileMap<P> map = reconstruct(data, new TypeToken<FileMap<P>>() {}.getType());
      if (map != null) { map.rootFolder = child; } else {
        Log.d("RxU", "Map returned as null");
      }
      return map;
    }
  }

  private static Gson getGson() {
    if (gson == null) {
      gson = new GsonBuilder()
          .excludeFieldsWithModifiers(Modifier.FINAL, Modifier.TRANSIENT, Modifier.STATIC)
          //.serializeNulls()
          .excludeFieldsWithoutExposeAnnotation()
          .create();
    }
    return gson;
  }

  private static <T> T reconstruct(String string, Type cls) {
    if (string == null) {
      Log.d("RxU", "Gson - String was null");
      return null;
    } else {
      try {
        return getGson().fromJson(string, cls);
      } catch (Exception e) {
        e.printStackTrace();
        Log.d("RxU", "Gson - something fucked up");
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

  private static Pair<Boolean, Long> checkLong(String s) {
    boolean isLong = false;
    long longNumber = -1L;
    try {
      longNumber = Long.parseLong(s);
      isLong = true;
    } catch (NumberFormatException ignore) {}
    return new Pair<>(isLong, longNumber);
  }

  public Observable<List<FileParams>> similar(FileParams params) {
    return Observable.just(files)
        .observeOn(Schedulers.computation())
        .flatMapIterable(items -> items)
        .filter(item -> FileParams.compare(item, params) > 0)
        .toList()
        .toObservable()
        .observeOn(AndroidSchedulers.mainThread());
  }

  public Observable<List<FileParams>> matching(FileParams params) {
    return Observable.just(files)
        .observeOn(Schedulers.computation())
        .flatMapIterable(items -> items)
        .filter(item -> FileParams.compare(item, params) == FileParams.MATCHING)
        .toList()
        .toObservable()
        .observeOn(AndroidSchedulers.mainThread());
  }

  public Observable<T> getObject(FileParams fPam) {
    if (rootFolder == null) {
      throw new RuntimeException(
          "Object " + this.toString() + " not initialized with static method?");
    }

    return Observable.just(rootFolder.listFiles())
        .observeOn(Schedulers.io())
        .map(array -> {
          ArrayList<File> files = new ArrayList<>(0);
          Collections.addAll(files, array);
          return files;
        })
        .flatMapIterable(it -> it)
        .filter(it -> {
          Pair<Boolean, Long> values = checkLong(it.getName());
          return values.first && fPam.getId() == values.second;
        })
        .map(FileMap::readFile)
        .map(it -> {
          T finalObject = reconstruct(it, new TypeToken<T>() {}.getType());
          if (finalObject == null) {
            throw new RuntimeException("File specified is null, empty or non existing.");
          }
          return finalObject;
        }).observeOn(AndroidSchedulers.mainThread());
  }

  public void save(T file, FileParams params) {
    Log.d("RxU", "saving... in method");
    Observable.just(files)
        .doOnSubscribe(it -> {
          Log.d("RxU", "saving... subscribed");
        })
        .observeOn(Schedulers.io())
        .flatMapIterable(items -> items)
        .filter(item -> {
          int comparatorResult = FileParams.compare(item, params);
          Log.d("RxU", "comparison: " + comparatorResult);
          return (comparatorResult & FileParams.MATCHING_DES) == FileParams.MATCHING_DES;
        })
        .toList()
        .map(it -> {
          if (it.size() == 0) {
            params.internalForceSetId(getNewId());
            params.setTimeCreated(System.currentTimeMillis());
            params.setTimeChanged(params.getTimeCreated());
            updateMap(params);
            return params;
          } else if (it.size() > 1) {
            throw new RuntimeException("Internal problem with non-unique descriptors.");
          } else {
            return it.get(0);
          }
        })
        .subscribe(it -> saveFile(file, String.valueOf(it.getId())), Throwable::printStackTrace);
  }

  private long getNewId() {
    long id = 0;
    for (FileParams param : files) {
      long tempId = param.getId();
      if (tempId > id) { id = tempId; }
    }
    return id + 1;
  }

  private void updateMap(FileParams params) {
    if (params != null) { files.add(params); }
    saveFile(this, MAP_NAME);
  }

  private void saveFile(T file, String fileName) {
    Log.d("RxU", "saved file: " + fileName);
    if (rootFolder != null) {
      try {
        FileOutputStream fos = new FileOutputStream(new File(rootFolder, fileName));
        fos.write(getGson().toJson(file).getBytes());
        fos.close();
      } catch (IOException e) {
        if (BuildConfig.DEBUG) { e.printStackTrace(); }
      }
    } else { throw new RuntimeException("Root folder is null hence I can't save the file."); }
  }

  private void saveFile(FileMap<T> file, String fileName) {
    Log.d("RxU", "saved file: " + fileName);
    if (rootFolder != null) {
      try {
        FileOutputStream fos = new FileOutputStream(new File(rootFolder, fileName));
        fos.write(getGson().toJson(file).getBytes());
        fos.close();
      } catch (IOException e) {
        if (BuildConfig.DEBUG) { e.printStackTrace(); }
      }
    } else { throw new RuntimeException("Root folder is null hence I can't save the file."); }
  }
}
