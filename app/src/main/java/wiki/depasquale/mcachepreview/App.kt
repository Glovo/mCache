package wiki.depasquale.mcachepreview

import android.app.Application
import wiki.depasquale.mcache.MCache

/**
 * diareuse on 26.03.2017
 */

class App : Application() {

  override fun onCreate() {
    super.onCreate()
    MCache.with(this)
  }
}
