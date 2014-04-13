package org.notes.core.interceptors;

import org.notes.core.domain.Operation;

import java.lang.annotation.Retention;
import java.lang.annotation.Target;

import static java.lang.annotation.ElementType.METHOD;
import static java.lang.annotation.RetentionPolicy.RUNTIME;

@Target(METHOD)
@Retention(RUNTIME)
public @interface Bouncer {

    public String value() default "";

    Operation op() default Operation.IGNORE;
}
