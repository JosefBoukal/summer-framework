package org.summerframework.core.exception;

import org.summerframework.core.context.message.DefaultMessageResolvable;
import org.summerframework.core.context.message.MessageResolvable;

import java.util.Map;

/**
 * The localized exception is an exception that is easy to localize. All exceptions in an application should be
 * localized so it may be propagated up to User Interfaces without any need of wrapping when no more additional info is
 * required.
 */
public class LocalizedException extends RuntimeException implements MessageResolvable {
    private final MessageResolvable resolvable;
    private volatile String message;

    /**
     * Creates a localized exception from the given parameters.
     *
     * @param code            the error code used for resolving messages
     * @param defaultTemplate the default message template when no localized message could be resolved
     */
    public LocalizedException(String code, String defaultTemplate) {
        this(new String[]{code}, defaultTemplate, null, null);
    }

    /**
     * Creates a localized exception from the given parameters.
     *
     * @param code            the error code used for resolving messages
     * @param defaultTemplate the default message template when no localized message could be resolved
     * @param args            the arguments used for localization
     */
    public LocalizedException(String code, String defaultTemplate, Map<String, ?> args) {
        this(new String[]{code}, defaultTemplate, null, args);
    }

    /**
     * Creates a localized exception from the given parameters.
     *
     * @param code            the error code used for resolving messages
     * @param defaultTemplate the default message template when no localized message could be resolved
     * @param cause           the cause of this exception
     * @param args            the arguments used for localization
     */
    public LocalizedException(String code, String defaultTemplate, Exception cause, Map<String, ?> args) {
        this(new String[]{code}, defaultTemplate, cause, args);
    }

    /**
     * Creates a localized exception from the given parameters.
     *
     * @param codes           the list of error codes used for resolving messages
     * @param defaultTemplate the default message template when no localized message could be resolved
     * @param cause           the cause of this exception
     * @param args            the arguments used for localization
     */
    public LocalizedException(String[] codes, String defaultTemplate, Exception cause, Map<String, ?> args) {
        super(null, cause);
        this.resolvable = new DefaultMessageResolvable(codes, args, defaultTemplate);
    }

    /**
     * Creates a localized exception from the given resolvable.
     *
     * @param resolvable mandatory message resolvable
     */
    public LocalizedException(MessageResolvable resolvable) {
        super(null, null);
        this.resolvable = resolvable;
    }

    @Override
    public String[] getCodes() {
        return resolvable.getCodes();
    }

    @Override
    public Map<String, ?> getArguments() {
        return resolvable.getArguments();
    }

    @Override
    public String getDefaultTemplate() {
        return resolvable.getDefaultTemplate();
    }

    @Override
    public String getMessage() {
        // no need to synchronize, we expect that an exception message is accessed in a single thread anyway
        if (message == null) {
            Map<String, ?> args = resolvable.getArguments();
            if (args != null && args.size() > 0) {
                message = resolvable.getDefaultTemplate() + " Args: " + args;
            } else {
                message = resolvable.getDefaultTemplate();
            }
        }
        return message;
    }

    /**
     * Returns the {@link Throwable#getLocalizedMessage() localized exception message}.
     */
    @Override
    public String toString() {
        return getMessage();
    }

}
