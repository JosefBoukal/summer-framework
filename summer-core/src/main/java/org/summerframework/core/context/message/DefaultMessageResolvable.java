package org.summerframework.core.context.message;

import lombok.AllArgsConstructor;
import lombok.Getter;

import java.util.Map;

/**
 * The default immutable implementation for the {@link MessageResolvable}.
 */
@AllArgsConstructor
@Getter
public class DefaultMessageResolvable implements MessageResolvable {
    private final String[] codes;
    private final Map<String, ?> arguments;
    private final String defaultTemplate;
}
