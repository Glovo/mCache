package wiki.depasquale.mcache

import android.app.Application
import android.content.Context
import java.lang.ref.WeakReference

object Cache {

  private lateinit var contextReference: WeakReference<Context>

  @JvmStatic
  fun with(context: Application) {
    contextReference = WeakReference(context)
  }

  internal val context: Context
    get() {
      return contextReference.get() ?: throw RuntimeException("Context must not be null.")
    }

  fun <T : Any> obtain(cls: Class<T>): FilePresenterBuilder<T> {
    return FilePresenterBuilder<T>(FilePresenterBuilder.Mode.OBTAIN)
      .ofClass(cls)
  }

  fun <T : Any> give(cls: Class<T>, file: T): FilePresenterBuilder<T> {
    return FilePresenterBuilder<T>(FilePresenterBuilder.Mode.GIVE)
      .ofClass(cls)
      .ofFile(file)
  }
}