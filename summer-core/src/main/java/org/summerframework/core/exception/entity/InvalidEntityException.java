package org.summerframework.core.exception.entity;

import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.summerframework.core.exception.LocalizedException;

import java.util.Map;

/**
 * This exception is used to signal that an entity (usually given in a POST or PATCH request) is invalid, e.g. it is
 * semantically incorrect.
 *
 * @author Josef Boukal
 */
@ResponseStatus(HttpStatus.UNPROCESSABLE_ENTITY)
public class InvalidEntityException extends LocalizedException {

    public InvalidEntityException(String code, String message) {
        super(code, message);
    }

    public InvalidEntityException(String code, String message, Map<String, ?> args) {
        super(code, message, null, args);
    }

    public InvalidEntityException(String code, String message, Exception e, Map<String, ?> args) {
        super(code, message, e, args);
    }

    public InvalidEntityException(String[] codes, String message, Exception cause, Map<String, ?> args) {
        super(codes, message, cause, args);
    }
}