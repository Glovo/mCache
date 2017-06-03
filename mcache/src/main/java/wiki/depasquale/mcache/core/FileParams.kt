package wiki.depasquale.mcache.core

import com.google.gson.annotations.Expose

/**
 * diareuse on 03.06.2017
 */

class FileParams {
    @Expose var id: Long = -1L
        internal set
    @Expose var timeCreated: Long = -1L
        internal set
    @Expose var timeChanged: Long = -1L
    @Expose var descriptor: String = ""
}
