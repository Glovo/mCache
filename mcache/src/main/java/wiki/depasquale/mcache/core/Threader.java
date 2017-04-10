package wiki.depasquale.mcache.core;

import static android.os.Process.THREAD_PRIORITY_FOREGROUND;

import android.os.Handler;
import android.os.HandlerThread;
import android.os.Looper;

/**
 * diareuse on 26.03.2017
 */

public class Threader {

  private static Handler handler;
  private static Handler netHandler;

  /**
   * Main (UI) thread for setting views and stuff. J8 safe.
   */
  public static void runOnUI(Runnable r) {
    if (handler == null) {
      handler = new Handler(Looper.getMainLooper());
    }
    handler.post(r);
  }

  /**
   * Foreground priority thread which should be safe to use with any of {@link IOHandler} methods.
   * J8 safe.
   *
   * @param r executable runnable, non null preferred ;)
   */
  public static void runOnNet(Runnable r) {
    if (netHandler == null) {
      HandlerThread thread = new HandlerThread("depasquale.wiki.mcache.NETWORKTHREAD",
          THREAD_PRIORITY_FOREGROUND);
      thread.start();
      netHandler = new Handler(thread.getLooper());
    }
    netHandler.post(r);
  }
}