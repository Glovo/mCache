package wiki.depasquale.mcachepreview;

import android.app.Application;
import wiki.depasquale.mcache.MCache;

/**
 * diareuse on 26.03.2017
 */

public class App extends Application {

  @Override
  public final void onCreate() {
    super.onCreate();
    MCache.with(this).setCatchByDefault(false);
  }
}
