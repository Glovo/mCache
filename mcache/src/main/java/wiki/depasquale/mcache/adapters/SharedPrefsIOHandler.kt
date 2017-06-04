package wiki.depasquale.mcache.adapters

import android.content.Context
import android.content.SharedPreferences
import android.content.SharedPreferences.Editor
import io.reactivex.Observable
import wiki.depasquale.mcache.MCache
import wiki.depasquale.mcache.core.IOHandler
import wiki.depasquale.mcache.core.PrefName
import wiki.depasquale.mcache.core.internal.FileParams
import java.lang.reflect.Field

/**
 * Created by diareuse on 13/04/2017. Yeah. Suck it.
 */

// TODO: 01/06/2017 redo with new standard
class SharedPrefsIOHandler : IOHandler {

    override fun <T> get(type: Class<T>, params: FileParams): Observable<T> {
        return Observable.empty<T>()
    }

    override fun <T : Any> save(t: T, params: FileParams) {
        val mPrefs = prefs
        val mEditor = mPrefs.edit()
        t.javaClass.declaredFields
            .filter { it.isAnnotationPresent(PrefName::class.java) }
            .forEach {
                try {
                    save(mEditor, it, t)
                } catch (e: IllegalAccessException) {
                    e.printStackTrace()
                }
            }
        mEditor.apply()
    }

    override fun clean() {
        prefs.edit().clear().apply()
    }

    companion object {
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
