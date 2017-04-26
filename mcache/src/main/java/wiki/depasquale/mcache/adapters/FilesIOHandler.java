package wiki.depasquale.mcache.adapters;

import android.content.Context;
import android.support.annotation.Nullable;
import android.util.Base64;
import com.google.gson.Gson;
import java.io.BufferedReader;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import wiki.depasquale.mcache.BuildConfig;
import wiki.depasquale.mcache.MCache;
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
  public final <T> T get(CharSequence identifier, Class<T> cls) {
    String filename = String.format("%s%s%s", MCache.sPrefix,
        Base64.encodeToString(cls.getSimpleName().getBytes(), Base64.DEFAULT)
            .trim().replace("=", ""),
        identifier);
    try {
      Context context = MCache.get();
      if (context == null) {
        return null;
      }
      FileInputStream in = context.openFileInput(filename);
      InputStreamReader inputStreamReader = new InputStreamReader(in);
      BufferedReader bufferedReader = new BufferedReader(inputStreamReader);
      StringBuilder sb = new StringBuilder(0);
      String line;
      while ((line = bufferedReader.readLine()) != null) {
        sb.append(line);
      }
      inputStreamReader.close();
      return reconstruct(sb.toString(), cls);
    } catch (IOException e) {
      if (BuildConfig.DEBUG) { e.printStackTrace(); }
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
      FileOutputStream fos = context.openFileOutput(filename, Context.MODE_PRIVATE);
      fos.write(getGson().toJson(object).getBytes());
      fos.close();
    } catch (IOException e) {
      if (BuildConfig.DEBUG) { e.printStackTrace(); }
    }
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
