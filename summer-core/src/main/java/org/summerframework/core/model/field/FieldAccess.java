package org.summerframework.core.model.field;

import org.summerframework.core.model.AccessRight;

import java.lang.annotation.Documented;
import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.Target;

import static java.lang.annotation.RetentionPolicy.RUNTIME;

/**
 * Field access annotation is used to specify access rights on fields. For example, password fields should be write only
 * and they must never be read nor revealed in JSON responses.
 */
@Target(ElementType.FIELD)
@Documented
@Retention(RUNTIME)
public @interface FieldAccess {

    AccessRight[] type();

}
