package wiki.depasquale.mcache

fun <T : Any> T.give(): FilePresenterKotlinBuilder<T> {
    return FilePresenterKotlinBuilder<T>()
        .ofClass { this@give.javaClass }
        .ofFile { this@give }
}

inline fun <reified T : Any> obtain(): FilePresenterKotlinBuilder<T> {
    return FilePresenterKotlinBuilder<T>()
        .ofClass { T::class.java }
}