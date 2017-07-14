package wiki.depasquale.mcache.core.internal

import android.util.Log
import com.google.gson.annotations.Expose
import java.text.ParseException
import java.text.SimpleDateFormat
import java.util.*

/**
 * diareuse on 03.06.2017
 */

class FileParams() {

  val read: Read by lazy { Read() }
  val write: Write by lazy { Write() }
  @Expose val core: Core = Core()

  constructor(descriptor: String) : this() {
    core.descriptor = descriptor
  }

  class Read : Common() {

    private fun hasSetChanged(): Boolean = fromChanged != -1L && toChanged != -1L

    private fun hasSetCreated(): Boolean = fromCreated != -1L && toCreated != -1L

    fun hasSetBoundaries(): Boolean = hasSetChanged() || hasSetCreated()

    fun hasValidBoundaries(it: FileParams): Boolean {
      var changed: Boolean
      var created: Boolean
      if (hasSetChanged()) {
        changed = fromChanged == FileParams.Time.INFINITE ||
            it.core.timeChanged >= fromChanged
        changed = changed and (toChanged == FileParams.Time.INFINITE ||
            it.core.timeChanged <= toChanged)
      } else {
        changed = true
      }
      if (hasSetCreated()) {
        created = fromCreated == FileParams.Time.INFINITE ||
            it.core.timeCreated >= fromCreated
        created = created and (toCreated == FileParams.Time.INFINITE ||
            it.core.timeCreated <= toCreated)
      } else {
        created = true
      }
      //This is valid and does not need to be checked anymore because hasSetBoundaries()
      //has been invoked before therefore this cannot be result of the two elses (max one)
      return changed && created
    }
  }

  class Write : Common() {
    var listener: (Boolean) -> Unit = { Log.e("mCache", "Listener invoked but not set!") }
  }

  open class Common {
    var fromCreated: Long = -1L
    var fromChanged: Long = -1L
    var toCreated: Long = -1L
    var toChanged: Long = -1L

    var all: Boolean = false
  }

  class Core {
    @Expose var id: Long = -1L
      internal set
    @Expose var timeCreated: Long = -1L
      internal set
    @Expose var timeChanged: Long = -1L
      internal set
    @Expose var descriptor: String = ""
  }

  class Time {
    companion object {
      @JvmField
      val INFINITE = 0L
    }
  }

  companion object {
    /**
     * Converts time to millis based on pattern.
     * @throws ParseException if pattern doesn't match time
     */
    @JvmOverloads
    @JvmStatic
    fun timeFromTimestamp(pattern: String, time: String, locale: Locale = Locale.getDefault()): Long {
      return SimpleDateFormat(pattern, locale).parse(time).time
    }
  }

}
