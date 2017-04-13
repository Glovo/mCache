package wiki.depasquale.mcache.adapters;

import android.content.Context;
import android.support.annotation.Nullable;
import android.util.Base64;
import com.google.gson.Gson;
import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import wiki.depasquale.mcache.MCache;
import wiki.depasquale.mcache.core.IOHandler;

public class CacheIOHandler implements IOHandler {

  private static Gson gson;

  public CacheIOHandler() {}

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
        e.printStackTrace();
        return null;
      }
    }
  }

  private static boolean deleteDir(File dir) {
    if (dir != null && dir.isDirectory()) {
      File[] children = dir.listFiles();
      for (File child : children) {
        boolean success = deleteDir(child);
        if (!success) {
          return false;
        }
      }
      return dir.delete();
    } else {
      return dir != null && dir.isFile() && dir.delete();
    }
  }

  @Override
  @Nullable
  public final <T> T get(CharSequence identifier, Class<T> cls) {
    String filename = String.format("%s%s%s",
        MCache.sPrefix,
        Base64.encodeToString(cls.getSimpleName().getBytes(), Base64.DEFAULT)
            .trim().replace("=", ""),
        identifier);
    try {
      Context context = MCache.get();
      if (context == null) {
        return null;
      }
      File file = new File(context.getCacheDir(), filename);
      FileReader reader = new FileReader(file.getAbsoluteFile());
      BufferedReader bufferedReader = new BufferedReader(reader);
      StringBuilder sb = new StringBuilder(0);
      String line;
      while ((line = bufferedReader.readLine()) != null) {
        sb.append(line);
      }
      reader.close();
      return reconstruct(sb.toString(), cls);
    } catch (IOException e) {
      e.printStackTrace();
    }
    return null;
  }

  @Override
  public final <T> void save(T object, CharSequence identifier, Class<?> cls) {
    String filename = String.format("%s%s%s",
        MCache.sPrefix,
        Base64.encodeToString(cls.getSimpleName().getBytes(), Base64.DEFAULT)
            .trim().replace("=", ""),
        identifier);
    try {
      Context context = MCache.get();
      if (context == null) {
        return;
      }
      File file = new File(context.getCacheDir(), filename);
      FileWriter fos = new FileWriter(file.getAbsoluteFile());
      fos.write(getGson().toJson(object));
      fos.close();
    } catch (IOException e) {
      e.printStackTrace();
    }
  }

  @Override
  public final void clean() {
    Context context = MCache.get();
    if (context == null) {
      return;
    }
    File dir = context.getCacheDir();
    deleteDir(dir);
  }
}
