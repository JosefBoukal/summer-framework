package org.summerframework.core.exception.entity;

import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.summerframework.core.exception.LocalizedException;

import java.util.Map;

/**
 * This exception is usually used to signal that an entity to create already exists.
 */
@ResponseStatus(HttpStatus.CONFLICT)
public class EntityAlreadyExistsException extends LocalizedException {

    /**
     * Creates a new exception from the given parameters.
     *
     * @param code            the error code used for resolving messages
     * @param defaultTemplate the default message template when no localized message could be resolved
     * @param args            the arguments used for localization
     */
    public EntityAlreadyExistsException(String code, String defaultTemplate, Map<String, ?> args) {
        super(code, defaultTemplate, args);
    }

    @Override
    public Throwable fillInStackTrace() {
        return null;
    }

}