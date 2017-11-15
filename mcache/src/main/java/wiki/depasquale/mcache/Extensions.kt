package wiki.depasquale.mcache

import android.util.Base64
import io.reactivex.Maybe

internal fun String.unBase64(): String {
  return String(Base64.decode(toByteArray(), Base64.NO_WRAP))
}

internal fun String.base64(): String {
  return String(Base64.encode(toByteArray(), Base64.NO_WRAP))
}

/**
 * Uses [consumer] to handle *onNext* (with non-null value) and *onError* *onComplete* (with null
 * value). Use null checks to evaluate whether you've acquired your object.
 */
fun <T> Maybe<T>.subscribeToAll(consumer: (T?) -> Unit) {
  subscribe({ consumer(it) }, { consumer(null) }, { consumer(null) })
}