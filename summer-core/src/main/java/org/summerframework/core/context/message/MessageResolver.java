package org.summerframework.core.context.message;

import java.util.Locale;
import java.util.Map;

/**
 * The message resolver is used to resolve messages based on a given locale and the {@link MessageResolvable}
 * specification. When a locale is not given some default one should be selected.
 */
public interface MessageResolver {

    /**
     * Resolves a message based on the given codes, default template and locale. When no localized message is resolved
     * the default template must be returned.
     *
     * @param locale          the optional locale
     * @param codes           the codes used to resolve a localized message template
     * @param defaultTemplate the default message template when no localized one is resolved
     * @throws RuntimeException If no message template is resolved and no default template is given or there was some
     *                          template engine error while processing values.
     */
    default String getMessage(Locale locale, String[] codes, String defaultTemplate) {
        return getMessage(locale, codes, defaultTemplate, null);
    }

    /**
     * Resolves a message based on the given codes, default template and locale. When no localized message is resolved
     * the default template must be returned.
     *
     * @param locale          the optional locale
     * @param codes           the codes used to resolve a localized message template
     * @param defaultTemplate the default message template when no localized one is resolved
     * @param args            the optional message arguments, may be null or empty
     * @throws RuntimeException If no message template is resolved and no default template is given or there was some
     *                          template engine error while processing values.
     */
    String getMessage(Locale locale, String[] codes, String defaultTemplate, Map<String, ?> args);

    /**
     * Resolves a message based on the given codes, default template and locale. When no localized message is resolved
     * the default template must be returned.
     *
     * @param locale     the optional locale
     * @param resolvable the required message resolvable to use for message resolution
     * @throws RuntimeException If no message template is resolved and no default template is given or there was some
     *                          template engine error while processing values.
     */
    default String getMessage(Locale locale, MessageResolvable resolvable) {
        return getMessage(locale, resolvable.getCodes(), resolvable.getDefaultTemplate(), resolvable.getArguments());
    }

}
