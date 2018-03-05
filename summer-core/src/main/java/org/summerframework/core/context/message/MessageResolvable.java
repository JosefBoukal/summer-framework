package org.summerframework.core.context.message;

import org.springframework.lang.Nullable;

import java.util.Map;

/**
 * The extended interface to the {@link org.springframework.context.MessageSourceResolvable} for messages to support
 * named parameters instead of indexed. Any resolved message (or the {@link #getDefaultTemplate() default template}) is
 * supposed to be interpolated by some template processing engine (such as Pebble) using the {@link #getArguments()
 * arguments}.
 */
public interface MessageResolvable {

    /**
     * Returns an ordered list of error codes used to resolve this message starting from the most specific one.
     */
    String[] getCodes();

    /**
     * Returns the default message template when none is resolved for the {@link #getCodes() given codes}. It may also
     * contain unresolved placeholders that will be interpolated with {@link #getArguments() arguments} by a templating
     * engine.
     */
    String getDefaultTemplate();

    /**
     * Returns named arguments used while interpolating the resolved message. It may return null or empty map if there
     * is no argument.
     */
    @Nullable
    Map<String, ?> getArguments();

}
