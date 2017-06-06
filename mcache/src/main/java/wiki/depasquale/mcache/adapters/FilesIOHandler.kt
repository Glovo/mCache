package wiki.depasquale.mcache.adapters

import io.reactivex.*
import wiki.depasquale.mcache.core.*
import wiki.depasquale.mcache.core.internal.*

class FilesIOHandler : IOHandler {

  override fun <T> get(type: Class<T>, params: FileParams): Observable<T> =
      FileMap.forClass(type, false).findObjectByParams(type, params)

  override fun <T : Any> save(t: T, params: FileParams) =
      FileMap.forClass(t.javaClass, false).saveObjectWithParams(t, params)

  override fun <T : Any?> remove(type: Class<T>, params: FileParams) =
      FileMap.forClass(type, false).removeObjectWithParams(params)

  override fun clean() =
      FileMap.clean(false)
}
