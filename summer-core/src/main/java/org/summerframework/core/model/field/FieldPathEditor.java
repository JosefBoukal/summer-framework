package org.summerframework.core.model.field;

import lombok.extern.slf4j.Slf4j;

import java.beans.PropertyEditorSupport;

/**
 * The Spring editor for the {@link FieldPath}.
 */
@Slf4j
public class FieldPathEditor extends PropertyEditorSupport {

    @Override
    public void setAsText(String text) throws IllegalArgumentException {
        setValue(FieldPath.valueOf(text));
    }

}
