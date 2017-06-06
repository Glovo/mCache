package wiki.depasquale.mcache

import android.app.*
import android.content.*
import wiki.depasquale.mcache.adapters.*
import wiki.depasquale.mcache.core.*
import java.lang.ref.*
import java.util.*

/**
 * diareuse on 26.03.2017
 */

class MCache private constructor() {

  init {
    throw RuntimeException("This constructor shall not be used!")
  }

  companion object {

    private val sIOHandlerInstance = HashMap<Class<*>, IOHandler>(0)
    private var sContext: WeakReference<Context>? = null

    /**
     * Initializes MCache
     * IOHandler - [CacheIOHandler]
     */
    @JvmStatic
    fun with(context: Application) {
      MCache.sContext = WeakReference<Context>(context)
    }

    @JvmStatic
    fun get(): Context? {
      sContext?.let {
        return it.get()
      }
      return null
    }

    @JvmStatic
    fun getIOHandler(cls: Class<out IOHandler>?): IOHandler {
      var classToRead = cls
      if (classToRead == null) {
        classToRead = CacheIOHandler::class.java
      }
      if (sIOHandlerInstance.containsKey(classToRead)) {
        return sIOHandlerInstance[classToRead]!!
      } else {
        try {
          val interfaceType = classToRead.newInstance()
          sIOHandlerInstance.put(classToRead, interfaceType)
          return interfaceType
        } catch (e: InstantiationException) {
          throw RuntimeException(classToRead.name + " cannot be instantiated.")
        } catch (e: IllegalAccessException) {
          throw RuntimeException(classToRead.name + " probably does not contain constructor.")
        }

      }
    }

    @JvmStatic
    @SafeVarargs fun clean(vararg cls: Class<out IOHandler>) {
      for (handler in cls) {
        getIOHandler(handler).clean()
      }
    }
  }
}
