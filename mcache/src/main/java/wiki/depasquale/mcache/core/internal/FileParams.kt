package wiki.depasquale.mcache.core.internal

import com.google.gson.annotations.*

/**
 * diareuse on 03.06.2017
 */

data class FileParams(@Expose val descriptor: String) {
  @Expose var id: Long = -1L
    internal set
  @Expose var timeCreated: Long = -1L
    internal set
  @Expose var timeChanged: Long = -1L
  var listener: (Boolean) -> Unit = {}
}
