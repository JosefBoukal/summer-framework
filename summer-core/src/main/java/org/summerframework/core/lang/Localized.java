package org.summerframework.core.lang;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/**
 * The annotation used on fields or methods to signal that a field is localized (usually in a persistent storage). For
 * example, you can have a 'title' field on your domain object annotated and it may be stored in the Mongo collection
 * like this:
 * <pre>
 *     ...
 *     title: {
 *       "en": "This is text...",
 *       "cs": "Toto je text..."
 *     }
 *     ...
 * </pre>
 * and the filed will be localized in either English or Czech language depending on the currently selected language. It
 * is out of scope of this contract what the "currently selected language" is, but it will usually be the language
 * specified by the {@link org.springframework.context.i18n.LocaleContextHolder}.
 */
@Target({ElementType.FIELD, ElementType.METHOD})
@Retention(RetentionPolicy.RUNTIME)
public @interface Localized {

    /**
     * Returns true if the filed might be null when a value is not available in the currently selected locale. It
     * defaults to false meaning that some arbitrary localized value (usually the first one) should be selected. An
     * empty string must be returned when no value in any locale is available at all.
     */
    boolean nullable() default false;

}
