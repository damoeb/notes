package org.notes.core.endpoints.request;

import java.lang.annotation.Retention;
import java.lang.annotation.Target;

import static java.lang.annotation.ElementType.FIELD;
import static java.lang.annotation.RetentionPolicy.RUNTIME;

/**
 * @author Daniel Scheidle, daniel.scheidle@ucs.at
 */
@Target(FIELD)
@Retention(RUNTIME)
public @interface RequestProperty {

    public String value();

    public boolean mandatory() default false;
}
