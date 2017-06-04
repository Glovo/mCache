package wiki.depasquale.mcache.adapters

import io.reactivex.Observable
import wiki.depasquale.mcache.core.IOHandler
import wiki.depasquale.mcache.core.internal.FileMap
import wiki.depasquale.mcache.core.internal.FileParams

class FilesIOHandler : IOHandler {

    override fun <T> get(type: Class<T>, params: FileParams): Observable<T> {
        val map = FileMap.forClass(type, false)
        return map.findObjectByParams(type, params)
    }

    override fun <T : Any> save(t: T, params: FileParams) {
        FileMap.forClass(t.javaClass, false)
            .saveObjectWithParams(t, params)
    }

    override fun clean() {

    }
}
