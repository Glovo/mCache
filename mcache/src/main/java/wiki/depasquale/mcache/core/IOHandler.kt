package wiki.depasquale.mcache.core

import io.reactivex.*
import wiki.depasquale.mcache.core.internal.*

/**
 * diareuse on 26.03.2017
 */

interface IOHandler {

  /**
   * Creates object with class of type and params of FileParams.
   */
  operator fun <T> get(type: Class<T>, params: FileParams): Observable<T>

  /**
   * Saves object of class cls. **Unsafe on main thread.**
   */
  fun <T : Any> save(obj: T, params: FileParams)

  fun <T> remove(type: Class<T>, params: FileParams)

  fun clean()

}
