package org.summerframework.experimental.report;

import lombok.extern.slf4j.Slf4j;
import org.springframework.context.i18n.LocaleContextHolder;
import org.summerframework.core.context.message.MessageResolver;

import java.util.Locale;
import java.util.Map;

/**
 *
 */
@Slf4j
public class Sfl4jReport implements ReportSink {

    private final MessageResolver messageResolver;
    private Level threshold;

    public Sfl4jReport(MessageResolver messageResolver) {
        this.messageResolver = messageResolver;
    }

    @Override
    public void setThreshold(Level level) {
        this.threshold = level;
    }

    @Override
    public void summary(Level level, String[] codes, String defaultMessage, Map<String, ?> args) {
        log(level, codes, defaultMessage, args);
    }

    @Override
    public void entry(Level level, String[] codes, String defaultMessage, Map<String, ?> args) {
        log(level, codes, defaultMessage, args);
    }

    private void log(Level level, String[] codes, String defaultMessage, Map<String, ?> args) {
        if (threshold != null && level.ordinal() < threshold.ordinal()) {
            return;
        }
        Locale locale = LocaleContextHolder.getLocale();
        String message = messageResolver.getMessage(locale, codes, defaultMessage, args);
        if (level == Level.error) {
            log.error(message);
        } else if (level == Level.info) {
            log.info(message);
        } else if (level == Level.debug) {
            log.debug(message);
        } else if (level == Level.trace) {
            log.trace(message);
        } else if (level == Level.warn) {
            log.warn(message);
        }
    }

}
