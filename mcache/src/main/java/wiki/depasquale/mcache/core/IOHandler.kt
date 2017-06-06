package wiki.depasquale.mcache.core

import io.reactivex.*
import wiki.depasquale.mcache.core.internal.*

/**
 * diareuse on 26.03.2017
 */

interface IOHandler {

  operator fun <T> get(type: Class<T>, params: FileParams): Observable<T>

  fun <T : Any> save(obj: T, params: FileParams)

  fun <T> remove(type: Class<T>, params: FileParams)

  fun clean()

}
