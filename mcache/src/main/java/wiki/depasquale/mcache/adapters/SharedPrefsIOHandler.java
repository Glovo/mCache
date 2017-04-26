package wiki.depasquale.mcache.adapters;

import android.content.Context;
import android.content.SharedPreferences;
import android.content.SharedPreferences.Editor;
import java.lang.reflect.Field;
import wiki.depasquale.mcache.BuildConfig;
import wiki.depasquale.mcache.MCache;
import wiki.depasquale.mcache.core.IOHandler;
import wiki.depasquale.mcache.core.PrefName;

/**
 * Created by diareuse on 13/04/2017. Yeah. Suck it.
 */

public final class SharedPrefsIOHandler implements IOHandler {

  private static SharedPreferences sPrefs;

  public static SharedPreferences getPrefs() {
    Context context = MCache.get();
    if (context == null) {
      return null;
    }
    if (sPrefs == null) {
      sPrefs = context.getSharedPreferences(
          String.format("%s.mcache", context.getPackageName()), Context.MODE_PRIVATE);
    }
    return sPrefs;
  }

  private static void save(Editor mPrefs, Field f, Object object)
      throws IllegalAccessException {
    Object fieldValue = f.get(object);
    String fieldName = f.getAnnotation(PrefName.class).value();
    if (fieldValue instanceof String) {
      mPrefs.putString(fieldName, (String) fieldValue);
    } else if (fieldValue instanceof Integer) {
      mPrefs.putInt(fieldName, (Integer) fieldValue);
    } else if (fieldValue instanceof Boolean) {
      mPrefs.putBoolean(fieldName, (Boolean) fieldValue);
    } else if (fieldValue instanceof Float) {
      mPrefs.putFloat(fieldName, (Float) fieldValue);
    } else if (fieldValue instanceof Long) {
      mPrefs.putLong(fieldName, (Long) fieldValue);
    } else {
      throw new IllegalStateException("Field of type " + f.getGenericType() +
          " is not supported by Shared Preferences.");
    }
  }

  @Override public final <T> T get(CharSequence identifier, Class<T> cls) {
    return null;
  }

  @Override public final <T> void save(T object, CharSequence identifier, Class<?> cls) {
    Context context = MCache.get();
    if (context == null) {
      return;
    }
    SharedPreferences mPrefs = getPrefs();
    if (mPrefs == null) { return; }
    Editor mEditor = mPrefs.edit();
    for (Field f : cls.getDeclaredFields()) {
      if (f.isAnnotationPresent(PrefName.class)) {
        try {
          save(mEditor, f, object);
        } catch (IllegalAccessException e) {
          if (BuildConfig.DEBUG) { e.printStackTrace(); }
        }
      }
    }
    mEditor.apply();
  }

  @Override public final void clean() {
    SharedPreferences mPrefs = getPrefs();
    if (mPrefs != null) {
      mPrefs.edit().clear().apply();
    }
  }
}
