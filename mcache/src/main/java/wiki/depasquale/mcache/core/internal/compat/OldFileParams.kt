package wiki.depasquale.mcache.core.internal.compat

import wiki.depasquale.mcache.core.internal.FileParams

class OldFileParams {
  var id: Long = -1L
    internal set
  var timeCreated: Long = -1L
    internal set
  var timeChanged: Long = -1L
    internal set
  val descriptor: String = ""

  fun toNew(): FileParams {
    val params = FileParams()
    params.core.id = id
    params.core.timeCreated = timeCreated
    params.core.timeChanged = timeChanged
    params.core.descriptor = descriptor
    return params
  }
}