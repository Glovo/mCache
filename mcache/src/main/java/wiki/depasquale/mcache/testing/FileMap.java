package wiki.depasquale.mcache.testing;

import android.content.Context;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.util.Base64;
import android.util.Log;
import android.util.Pair;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
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

// This is extremely stupid and unreliable solution and it's discontinued.

public class FileMap<T extends FileParams<?>> {

  private static final String MAP_NAME = "map.fmp";
  private final File folder;
  private List<T> files = new ArrayList<>(0);
  private Type type;

  private FileMap(File parent, Type type) {
    this.folder = parent;
    this.type = type;
    boolean exists = false;
    for (File file : parent.listFiles()) {
      if (MAP_NAME.equals(file.getName())) {
        exists = true;
        FileMap<T> map = reconstruct(readFile(file), type);
        if (map != null) {
          this.files = map.files;
        } else {
          exists = false;
        }
        break;
      }
    }
    if (!exists) {
      updateMap(null);
    }
  }

  @Nullable
  public static <P> FileMap<FileParams<P>> forClass(@NonNull Class<P> cls, boolean isCache) {
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
  private static <P> FileMap<FileParams<P>> buildFileMapForFolder(@NonNull File child) {
    return new FileMap<>(child, new TypeToken<P>() {}.getType());
  }

  private static Gson getGson(Type type) {
    return new GsonBuilder()
        .excludeFieldsWithModifiers(Modifier.FINAL, Modifier.TRANSIENT, Modifier.STATIC)
        /*.registerTypeAdapter(type, new TypeAdapter<>() {
          @Override
          public void write(JsonWriter out, Object value) throws IOException {

          }

          @Override
          public Object read(JsonReader in) throws IOException {
            return null;
          }
        })*/
        .create();
    /*if (gson == null) {
      gson = new GsonBuilder()
          .excludeFieldsWithModifiers(Modifier.FINAL, Modifier.TRANSIENT, Modifier.STATIC)
          .registerTypeAdapter(type,null)
          .create();
    }
    return gson;*/
  }

  @Nullable
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

  public static <O> Observable<O> getObjectObservable(FileMap<FileParams<O>> map,
      FileParams<O> params) {
    if (map.folder == null) {
      throw new RuntimeException(
          "Object " + map.toString() + " not initialized with static method?");
    }

    return Observable.just(map.folder.listFiles())
        .observeOn(Schedulers.io())
        .map(array -> {
          ArrayList<File> files = new ArrayList<>(0);
          Collections.addAll(files, array);
          return files;
        })
        .flatMapIterable(it -> it)
        .filter(it -> {
          Pair<Boolean, Long> values = checkLong(it.getName());
          return values.first && params.getId() == values.second;
        })
        .map(FileMap::readFile)
        .map(it -> {
          O finalObject = map.reconstruct(it, map.type);
          if (finalObject == null) {
            throw new RuntimeException("File specified is null, empty or non existing.");
          }
          Log.d("RxU final", new Gson().toJson(finalObject));
          return finalObject;
        }).observeOn(AndroidSchedulers.mainThread());
  }

  public static <O> void save(FileMap<FileParams<O>> map, O file, FileParams params) {
    Log.d("RxU", "saving... in method");
    Observable.just(map.files)
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
            params.internalForceSetId(map.getNewId());
            params.setTimeCreated(System.currentTimeMillis());
            params.setTimeChanged(params.getTimeCreated());
            map.updateMap(params);
            return params;
          } else if (it.size() > 1) {
            throw new RuntimeException("Internal problem with non-unique descriptors.");
          } else {
            return it.get(0);
          }
        })
        .subscribe(it -> saveFile(map, file, String.valueOf(it.getId())),
            Throwable::printStackTrace);
  }

  private static <O> void saveFile(FileMap<FileParams<O>> map, O file, String fileName) {
    Log.d("RxU", "saved file: " + fileName);
    if (map.folder != null) {
      try {
        FileOutputStream fos = new FileOutputStream(new File(map.folder, fileName));
        fos.write(new Gson().toJson(file).getBytes());
        fos.close();
      } catch (IOException e) {
        if (BuildConfig.DEBUG) { e.printStackTrace(); }
      }
    } else { throw new RuntimeException("Root folder is null hence I can't save the file."); }
  }

  private <T> T reconstruct(String string, @NonNull Type cls) {
    if (string == null) {
      Log.d("RxU", "Gson - String was null");
      return null;
    } else {
      try {
        return getGson(type).fromJson(string, cls);
      } catch (Exception e) {
        e.printStackTrace();
        Log.d("RxU", "Gson - something fucked up");
        return null;
      }
    }
  }

  public Observable<List<T>> similar(FileParams params) {
    return Observable.just(files)
        .observeOn(Schedulers.computation())
        .flatMapIterable(items -> items)
        .filter(item -> FileParams.compare(item, params) > 0)
        .toList()
        .toObservable()
        .observeOn(AndroidSchedulers.mainThread());
  }

  public Observable<List<T>> matching(FileParams params) {
    return Observable.just(files)
        .observeOn(Schedulers.computation())
        .flatMapIterable(items -> items)
        .filter(item -> (FileParams.compare(item, params) & FileParams.MATCHING_DES)
            == FileParams.MATCHING_DES)
        .toList()
        .toObservable()
        .observeOn(AndroidSchedulers.mainThread());
  }

  private long getNewId() {
    long id = 0;
    for (FileParams param : files) {
      long tempId = param.getId();
      if (tempId > id) { id = tempId; }
    }
    return id + 1;
  }

  private void updateMap(T params) {
    if (params != null) { files.add(params); }
    saveFile(this, MAP_NAME);
  }

  private void saveFile(FileMap<T> file, String fileName) {
    Log.d("RxU", "saved file: " + fileName);
    if (folder != null) {
      try {
        FileOutputStream fos = new FileOutputStream(new File(folder, fileName));
        fos.write(getGson(type).toJson(file).getBytes());
        fos.close();
      } catch (IOException e) {
        if (BuildConfig.DEBUG) { e.printStackTrace(); }
      }
    } else { throw new RuntimeException("Root folder is null hence I can't save the file."); }
  }
}
