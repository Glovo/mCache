package wiki.depasquale.mcache.adapters

import io.reactivex.*
import wiki.depasquale.mcache.core.*
import wiki.depasquale.mcache.core.internal.*

class CacheIOHandler : IOHandler {

  override fun <T> get(type: Class<T>, params: FileParams): Observable<T> {
    val map = FileMap.forClass(type, true)
    return map.findObjectByParams(type, params)
  }

  override fun <T : Any> save(t: T, params: FileParams) {
    FileMap.forClass(t.javaClass, true)
        .saveObjectWithParams(t, params)
  }

  override fun <T : Any?> remove(type: Class<T>, params: FileParams) {
    FileMap.forClass(type, true).removeObjectWithParams(params)
  }
}
