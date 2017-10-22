package wiki.depasquale.mcache

import android.util.Base64

internal fun String.unBase64(): String {
  return String(Base64.decode(toByteArray(), Base64.NO_WRAP))
}

internal fun String.base64(): String {
  return String(Base64.encode(toByteArray(), Base64.NO_WRAP))
}