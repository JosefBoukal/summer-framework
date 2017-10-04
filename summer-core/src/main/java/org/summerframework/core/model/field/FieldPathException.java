package org.summerframework.core.model.field;

import org.summerframework.core.exception.LocalizedException;

/**
 * This exception is used to signal problems and issues while using the {@link FieldPath} API.
 */
public class FieldPathException extends LocalizedException {

    public FieldPathException(String code, String defaultTemplate) {
        super(code, defaultTemplate);
    }

}
