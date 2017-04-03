package wiki.depasquale.mcache;

/**
 * diareuse on 26.03.2017
 */

class Log {

  static void l(String message) {
    if (message == null) {
      return;
    }
    if (message.contains("DEBUG") && MCache.DEBUG) {
      android.util.Log.d(MCache.TAG, message);
    } else if (BuildConfig.DEBUG) {
      if (message.contains("error")) {
        android.util.Log.e(MCache.TAG, message);
      } else if (message.contains("warning")) {
        android.util.Log.w(MCache.TAG, message);
      } else {
        android.util.Log.i(MCache.TAG, message);
      }
    }
  }

  static void debug(String s) {
    l("DEBUG: " + s);
  }
}
