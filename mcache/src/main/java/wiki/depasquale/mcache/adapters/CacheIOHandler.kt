package wiki.depasquale.mcache.adapters

import io.reactivex.*
import wiki.depasquale.mcache.core.*
import wiki.depasquale.mcache.core.internal.*

class CacheIOHandler : IOHandler {

  override fun <T> get(type: Class<T>, params: FileParams): Observable<T> =
      FileMap.forClass(type, true).findObjectByParams(type, params)

  override fun <T : Any> save(obj: T, params: FileParams) =
      FileMap.forClass(obj.javaClass, true).saveObjectWithParams(obj, params)

  override fun <T : Any?> remove(type: Class<T>, params: FileParams) =
      FileMap.forClass(type, true).removeObjectWithParams(params)

  override fun clean() =
      FileMap.clean(true)
}
