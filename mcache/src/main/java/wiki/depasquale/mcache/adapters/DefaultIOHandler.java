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
import wiki.depasquale.mcache.L;
import wiki.depasquale.mcache.MCache;
import wiki.depasquale.mcache.core.IOHandler;

/**
 * diareuse on 26.03.2017
 */

public final class DefaultIOHandler implements IOHandler {

  private static Gson gson;

  public DefaultIOHandler() {
  }

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
      L.debug("Reconstruction failed, read file was null");
      return null;
    } else {
      try {
        L.debug("Reconstructing...");
        return getGson().fromJson(string, cls);
      } catch (Exception e) {
        e.printStackTrace();
        L.debug("Failed to reconstruct file.");
        return null;
      }
    }
  }

  @Override
  @Nullable
  public final <T> T get(CharSequence identifier, Class<T> cls) {
    L.debug("Requested class:" + cls.getName());
    String filename = String.format("%s%s%s", MCache.sPrefix,
        Base64.encodeToString(cls.getSimpleName().getBytes(), Base64.DEFAULT)
            .trim().replace("=", ""),
        identifier);
    L.debug("Requested filename" + filename);
    try {
      Context context = MCache.get();
      if (context == null) {
        L.l("Error captain! We've lost the context.");
        return null;
      } else {
        L.debug("Context OK");
      }
      L.debug("Opening Input Stream");
      FileInputStream in = context.openFileInput(filename);
      InputStreamReader inputStreamReader = new InputStreamReader(in);
      BufferedReader bufferedReader = new BufferedReader(inputStreamReader);
      StringBuilder sb = new StringBuilder(0);
      String line;
      while ((line = bufferedReader.readLine()) != null) {
        sb.append(line);
      }
      inputStreamReader.close();
      L.debug("Read OK, preparing reconstruction");
      try {
        return reconstruct(sb.toString(), cls);
      } finally {
        L.debug("Reconstruction OK");
      }
    } catch (IOException e) {
      e.printStackTrace();
      L.debug("Failed to read file.");
    }
    L.debug("Returning NULL value for " + cls.getName());
    return null;
  }

  @Override
  public final <T> void save(T object, CharSequence identifier, Class<?> cls) {
    L.debug("Saving object with class of " + cls.getName() + " with identifier " + identifier);
    String filename = String.format("%s%s%s",
        MCache.sPrefix,
        Base64.encodeToString(cls.getSimpleName().getBytes(), Base64.DEFAULT)
            .trim().replace("=", ""),
        identifier);
    L.debug("Saving via filename " + filename);
    try {
      Context context = MCache.get();
      if (context == null) {
        L.l("Error captain! We've lost the context.");
        return;
      } else {
        L.debug("Context OK");
      }
      L.debug("Opening Output Stream");
      FileOutputStream fos = context.openFileOutput(filename, Context.MODE_PRIVATE);
      fos.write(getGson().toJson(object).getBytes());
      fos.close();
      L.debug("Object of " + cls.getName() + " saved OK");
    } catch (IOException e) {
      e.printStackTrace();
      L.debug("Failed to write file.");
    }
  }

  @Override
  public final void clean() {
    Context context = MCache.get();
    if (context == null) {
      L.l("Error captain! We've lost the context.");
      return;
    }
    for (String file : context.fileList()) {
      if (file.startsWith(MCache.sPrefix)) {
        L.debug("Deleting file " + file);
        context.deleteFile(file);
      }
    }
  }
}
