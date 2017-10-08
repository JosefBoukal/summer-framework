package org.summerframework.core.exception.entity;

import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.summerframework.core.exception.LocalizedException;

import java.util.Map;

/**
 * This exception is usually used to signal that an entity to create already exists.
 *
 * @author Josef Boukal
 */
@ResponseStatus(HttpStatus.CONFLICT)
public class EntityAlreadyExistsException extends LocalizedException {

    public EntityAlreadyExistsException(String code, String message, Map<String, ?> args) {
        super(code, message, args);
    }

    @Override
    public Throwable fillInStackTrace() {
        return null;
    }

}