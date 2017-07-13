package wiki.depasquale.mcache.core.internal.compat

import com.google.gson.annotations.Expose
import wiki.depasquale.mcache.core.internal.FileParams

class OldFileParams {
  @Expose var id: Long = -1L
    internal set
  @Expose var timeCreated: Long = -1L
    internal set
  @Expose var timeChanged: Long = -1L
    internal set
  @Expose val descriptor: String = ""

  fun toNew(): FileParams {
    val params = FileParams()
    params.core.id = id
    params.core.timeCreated = timeCreated
    params.core.timeChanged = timeChanged
    params.core.descriptor = descriptor
    return params
  }
}