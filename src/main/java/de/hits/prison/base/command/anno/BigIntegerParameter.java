package de.hits.prison.base.command.anno;

import de.hits.prison.base.command.helper.NumberLimit;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

@Retention(RetentionPolicy.RUNTIME)
@Target(ElementType.PARAMETER)
public @interface BigIntegerParameter {
    String min() default "0";

    String max() default "0";

    NumberLimit limit();
}
