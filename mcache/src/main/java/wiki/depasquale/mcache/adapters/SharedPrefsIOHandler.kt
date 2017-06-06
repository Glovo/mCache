package wiki.depasquale.mcache.adapters

import android.content.*
import android.content.SharedPreferences.*
import io.reactivex.*
import wiki.depasquale.mcache.*
import wiki.depasquale.mcache.core.*
import wiki.depasquale.mcache.core.internal.*
import java.lang.reflect.*

/**
 * Created by diareuse on 13/04/2017. Yeah. Suck it.
 */

class SharedPrefsIOHandler : IOHandler {

  override fun <T> get(type: Class<T>, params: FileParams): Observable<T> = Observable.empty<T>()

  override fun <T : Any> save(obj: T, params: FileParams) {
    val mPrefs = prefs
    val mEditor = mPrefs.edit()
    obj.javaClass.declaredFields
        .filter { it.isAnnotationPresent(PrefName::class.java) }
        .forEach {
          try {
            save(mEditor, it, obj)
          } catch (e: IllegalAccessException) {
            e.printStackTrace()
          }
        }
    mEditor.apply()
    params.listener(true)
  }

  override fun <T : Any?> remove(type: Class<T>, params: FileParams) = clean()

  override fun clean() = prefs.edit().clear().apply()

  companion object {
    @JvmStatic
    val prefs: SharedPreferences by lazy {
      val context = MCache.get() ?: throw RuntimeException("Prefs Panic", Throwable("Context is not valid anymore."))
      return@lazy context.getSharedPreferences(
          String.format("%s.mcache", context.packageName), Context.MODE_PRIVATE)
    }

    @Throws(IllegalAccessException::class)
    private fun save(mPrefs: Editor, f: Field, any: Any) {
      val fieldValue = f.get(any)
      val fieldName = f.getAnnotation(PrefName::class.java).value
      if (fieldValue is String) {
        mPrefs.putString(fieldName, fieldValue)
      } else if (fieldValue is Int) {
        mPrefs.putInt(fieldName, fieldValue)
      } else if (fieldValue is Boolean) {
        mPrefs.putBoolean(fieldName, fieldValue)
      } else if (fieldValue is Float) {
        mPrefs.putFloat(fieldName, fieldValue)
      } else if (fieldValue is Long) {
        mPrefs.putLong(fieldName, fieldValue)
      } else {
        throw IllegalStateException("Field of type " + f.genericType +
            " is not supported by Shared Preferences.")
      }
    }
  }
}
