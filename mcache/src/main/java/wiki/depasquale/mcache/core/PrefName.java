package wiki.depasquale.mcache.core;

import static java.lang.annotation.ElementType.FIELD;
import static java.lang.annotation.RetentionPolicy.RUNTIME;

import java.lang.annotation.Retention;
import java.lang.annotation.Target;

/**
 * Created by diareuse on 13/04/2017. Yeah. Suck it.
 */

@Target(FIELD)
@Retention(RUNTIME)
public @interface PrefName {

  String value() default "default";
}
