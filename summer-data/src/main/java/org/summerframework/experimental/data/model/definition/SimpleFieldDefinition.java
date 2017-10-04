package org.summerframework.experimental.data.model.definition;

import lombok.extern.slf4j.Slf4j;
import org.springframework.data.util.ClassTypeInformation;
import org.springframework.data.util.TypeInformation;

import java.lang.reflect.Field;

/**
 * @author Josef Boukal
 */
@Slf4j
public class SimpleFieldDefinition<T> extends AbstractFieldDefinition<T> {

    @SuppressWarnings("unchecked")
    public SimpleFieldDefinition(Field field) {
        this(field.getName(), (TypeInformation<T>) ClassTypeInformation.from(field.getType()));
    }

    public SimpleFieldDefinition(String name, TypeInformation<T> typeInformation) {
        super(name, typeInformation);
        if (log.isTraceEnabled()) {
            log.trace("Simple field of the '" + name + "' name created");
        }
        // seal right now, no other modification expected
        seal();
    }

}
