package org.summerframework.core.context.message.template;

import java.util.Locale;

/**
 * The interface specification for objects responsible for message resolving based on the given codes.
 */
public interface MessageTemplateResolver {

    /**
     * Tries to resolve a message template for the given codes and locale. It must return null when no such template in
     * the given locale exists.
     *
     * @param locale the optional locale, some default should be used when none specified
     * @param codes  the ordered list of codes to try to resolve a message template from
     */
    String resolveTemplate(Locale locale, String[] codes);

}
