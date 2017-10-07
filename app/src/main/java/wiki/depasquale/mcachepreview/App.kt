package wiki.depasquale.mcachepreview

import android.app.Application
import com.orhanobut.logger.AndroidLogAdapter
import com.orhanobut.logger.Logger
import wiki.depasquale.mcache.Cache
import wiki.depasquale.mcache.CacheMode

/**
 * diareuse on 26.03.2017
 */

class App : Application() {

  override fun onCreate() {
    super.onCreate()
    Cache
      .withGlobalMode(CacheMode.FILE)
      .with(this)
    Logger.addLogAdapter(AndroidLogAdapter())
  }
}
