package wiki.depasquale.mcache

import android.app.Application
import android.content.Context
import java.lang.ref.WeakReference

object MCache {

  private lateinit var contextReference: WeakReference<Context>

  @JvmStatic
  fun with(context: Application) {
    contextReference = WeakReference(context)
  }

  internal val context: Context
    get() {
      return contextReference.get() ?: throw RuntimeException("Context must not be null.")
    }
}