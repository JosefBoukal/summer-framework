package org.summerframework.core.context.message;

import java.util.Map;

/**
 * The extended interface to the {@link org.springframework.context.MessageSourceResolvable} for messages to support
 * named parameters instead of indexed. Any resolved message or the {@link #getDefaultTemplate() default template} is
 * supposed to be further processed by some text processing engine syntax (such as Pebble).
 */
public interface MessageResolvable {

    /**
     * Returns an ordered list of error codes used resolve this message starting from the most specific one.
     */
    String[] getCodes();

    /**
     * Returns the default message template when none is resolved for the given codes. It should/may contain unresolved
     * values.
     */
    String getDefaultTemplate();

    /**
     * Returns named arguments used to resolve this message. It may return null or empty map if there is no argument to
     * be resolved.
     */
    Map<String, ?> getArguments();

}
