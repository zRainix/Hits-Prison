package de.hits.prison.base.command.anno;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

@Retention(RetentionPolicy.RUNTIME)
@Target(ElementType.METHOD)
public @interface BaseCommand {
    String permission() default "";
    String[] aliases() default {};
    boolean op() default false;
}