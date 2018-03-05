package org.summerframework.core.exception.entity;

import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.summerframework.core.exception.LocalizedException;

import java.util.Map;

/**
 * This exception is used to signal that a required entity doesn't exist.
 */
@ResponseStatus(HttpStatus.NOT_FOUND)
public class EntityNotFoundException extends LocalizedException {

    public EntityNotFoundException(String code, String defaultTemplate) {
        super(code, defaultTemplate);
    }

    public EntityNotFoundException(String code, String defaultTemplate, Map<String, ?> args) {
        super(code, defaultTemplate, args);
    }

    public EntityNotFoundException(String[] codes, String defaultTemplate, Map<String, ?> args) {
        super(codes, defaultTemplate, null, args);
    }

    public EntityNotFoundException(String[] codes, String defaultTemplate, Exception cause, Map<String, ?> args) {
        super(codes, defaultTemplate, cause, args);
    }

    @Override
    public Throwable fillInStackTrace() {
        return null;
    }

}
