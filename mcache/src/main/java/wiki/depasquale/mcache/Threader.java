package wiki.depasquale.mcache;

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

  public static void runOnUI(Runnable r) {
    if (handler == null) {
      handler = new Handler(Looper.getMainLooper());
    }
    handler.post(r);
  }

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