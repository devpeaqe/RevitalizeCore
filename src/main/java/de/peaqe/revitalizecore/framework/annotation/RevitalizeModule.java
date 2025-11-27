package de.peaqe.revitalizecore.framework.annotation;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/**
 * *
 *
 * @author peaqe
 * @version 1.0
 * @since 26.11.2025 | 20:36 Uhr
 * *
 */

@Retention(RetentionPolicy.RUNTIME)
@Target(ElementType.TYPE)
public @interface RevitalizeModule {

    String name();
    boolean enabledByDefault() default true;

}
