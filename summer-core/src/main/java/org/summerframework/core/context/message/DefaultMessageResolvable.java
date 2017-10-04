package org.summerframework.core.context.message;

import java.util.Map;

/**
 * The default implementation for the {@link MessageResolvable}.
 */
public class DefaultMessageResolvable implements MessageResolvable {
    private final String[] codes;
    private final Map<String, ?> arguments;
    private final String defaultTemplate;

    public DefaultMessageResolvable(String[] codes, Map<String, ?> arguments, String defaultTemplate) {
        this.codes = codes;
        this.arguments = arguments;
        this.defaultTemplate = defaultTemplate;
    }

    @Override
    public String[] getCodes() {
        return codes;
    }

    @Override
    public Map<String, ?> getArguments() {
        return arguments;
    }

    @Override
    public String getDefaultTemplate() {
        return defaultTemplate;
    }
}
