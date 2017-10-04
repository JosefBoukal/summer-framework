package org.summerframework.experimental.report;

import org.summerframework.core.context.message.MessageResolvable;

import java.util.Map;

/**
 *
 */
public interface ReportSink {

    void setThreshold(Level level);

    void summary(Level level, String[] codes, String defaultTemplate, Map<String, ?> args);

    default void summary(Level level, String code, String defaultTemplate, Map<String, ?> args) {
        summary(level, new String[]{code}, defaultTemplate, args);
    }

    default void summary(Level level, MessageResolvable resolvable) {
        summary(level, resolvable.getCodes(), resolvable.getDefaultTemplate(), resolvable.getArguments());
    }

    void entry(Level level, String[] codes, String defaultTemplate, Map<String, ?> args);

    default void entry(Level level, String code, String defaultTemplate, Map<String, ?> args) {
        entry(level, new String[]{code}, defaultTemplate, args);
    }

    default void entry(Level level, MessageResolvable resolvable) {
        entry(level, resolvable.getCodes(), resolvable.getDefaultTemplate(), resolvable.getArguments());
    }

}
