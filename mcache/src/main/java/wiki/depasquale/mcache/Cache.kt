package wiki.depasquale.mcache

import android.app.Application
import android.content.Context
import com.google.gson.Gson
import com.orhanobut.logger.AndroidLogAdapter
import com.orhanobut.logger.Logger
import com.orhanobut.logger.PrettyFormatStrategy
import java.lang.ref.WeakReference

/**
 * Automagical object for pleasuring your senses.
 */
object Cache {

    private var contextReference: WeakReference<Context>? = null

    @JvmStatic
    fun withGlobalMode(mode: CacheMode): Cache {
        this.mode = mode
        return this
    }

    @JvmStatic
    fun withGson(gson: Gson): Cache {
        this.gson = gson
        return this
    }

    @JvmStatic
    fun with(context: Application) {
        with(context as Context)
    }

    internal fun with(context: Context) {
        contextReference = WeakReference(context)
        if (BuildConfig.DEBUG) {
            Logger.clearLogAdapters()
            Logger.addLogAdapter(AndroidLogAdapter())
        }
    }

    internal var mode: CacheMode = CacheMode.CACHE
        private set

    internal var gson: Gson = Gson()
        private set

    internal val context: Context
        get() {
            return contextReference?.get() ?: contextRationale()
        }

    @Throws(RuntimeException::class)
    private fun contextRationale(): Context {
        Logger.clearLogAdapters()
        Logger.addLogAdapter(
            AndroidLogAdapter(
                PrettyFormatStrategy.newBuilder()
                    .showThreadInfo(true)
                    .tag("diareuse/mCache")
                    .build()
            )
        )
        Logger.e(
            "You may have forgotten to initialize the library. Please visit my GitHub\n" +
                    "[https://github.com/diareuse/mCache] for latest instructions on how to set it up.\n" +
                    "Exception is thrown immediately after this message. You may have error you code as well as I can.\n" +
                    "Post issues with description as accurate as possible. More info I have more code I can fix :)"
        )
        throw RuntimeException("Context must not be null.")
    }

    /**
     * Initializes [FilePresenterBuilder] so it fetches file(s) defined further on.
     */
    @JvmStatic
    fun <T : Any> obtain(cls: Class<T>): FilePresenterBuilder<T> {
        return FilePresenterBuilder<T>()
            .ofClass(cls)
    }

    /**
     * Initializes [FilePresenterBuilder] so it saves file defined further on.
     */
    @JvmStatic
    fun <T : Any> give(file: T): FilePresenterBuilder<T> {
        return FilePresenterBuilder<T>()
            .ofClass(file.javaClass)
            .ofFile(file)
    }
}