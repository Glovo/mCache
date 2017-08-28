package wiki.depasquale.mcache.core.internal

import com.google.gson.annotations.Expose
import java.text.ParseException
import java.text.SimpleDateFormat
import java.util.*

/**
 * diareuse on 03.06.2017
 */

class FileParams() {

  @Transient
  val read: Read = Read(this)
  @Transient
  val write: Write = Write(this)
  val core: Core = Core()

  constructor(descriptor: String) : this() {
    core.descriptor = descriptor
  }

  fun setDescriptor(descriptor: String): FileParams {
    core.descriptor = descriptor
    return this
  }

  class Read(params: FileParams) : Common(params) {

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

  class Write(params: FileParams) : Common(params) {
    var listener: (Boolean) -> Unit = {}

    fun setListener(listener: (Boolean) -> Unit): Write {
      this.listener = listener
      return this
    }
  }

  open class Common(val params: FileParams) {
    var fromCreated: Long = -1L
    var fromChanged: Long = -1L
    var toCreated: Long = -1L
    var toChanged: Long = -1L

    var all: Boolean = false

    fun setFromCreated(fromCreated: Long): Common {
      this.fromCreated = fromCreated
      return this
    }

    fun setFromChanged(fromChanged: Long): Common {
      this.fromChanged = fromChanged
      return this
    }

    fun setToCreated(toCreated: Long): Common {
      this.toCreated = toCreated
      return this
    }

    fun setToChanged(toChanged: Long): Common {
      this.toChanged = toChanged
      return this
    }

    fun setAll(all: Boolean): Common {
      this.all = all
      return this
    }

    fun build(): FileParams {
      return params
    }
  }

  class Core {
    @Expose
    var id: Long = -1L
      internal set
    @Expose
    var timeCreated: Long = -1L
      internal set
    @Expose
    var timeChanged: Long = -1L
      internal set
    @Expose
    var descriptor: String = ""
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
