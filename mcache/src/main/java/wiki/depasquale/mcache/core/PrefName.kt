package wiki.depasquale.mcache.core

/**
 * Created by diareuse on 13/04/2017. Yeah. Suck it.
 */

@Target(AnnotationTarget.FIELD)
@Retention(AnnotationRetention.RUNTIME)
annotation class PrefName(val value: String = "default")
