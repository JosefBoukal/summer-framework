package org.summerframework.experimental.data.model.definition;

import org.springframework.data.util.TypeInformation;

import java.util.Collections;
import java.util.LinkedHashMap;
import java.util.Map;

/**
 * The generic Model Definition class that describes Java POJOs, JavaBeans or simply data models so that a model
 * definition may be extended programmatically. There is no need for a model or entity to have a Java (class)
 * representation either.
 *
 * @author Josef Boukal
 */
public class ModelDefinition<T> extends AbstractFieldDefinition<T> {

    protected Map<String, FieldDefinition<?>> fields = new LinkedHashMap<>(16);

    public ModelDefinition(String name, TypeInformation<T> typeInformation) {
        super(name, null, typeInformation);
    }

    public Map<String, FieldDefinition<?>> getFields() {
        return fields;
    }

    public void seal() {
        super.seal();
        this.fields = Collections.unmodifiableMap(this.fields);
    }

    public void addFieldDefinition(String fieldName, FieldDefinition<?> definition) {
        if (sealed) {
            throw new IllegalStateException("Unable to add the " + definition + " field definition of the '" +
                    fieldName + "' field on the " + this + " sealed definition!");
        }
        FieldDefinition<?> existing = this.fields.put(fieldName, definition);
        if (existing != null) {
            throw new IllegalArgumentException("Failed to add the '" + definition.getName() +
                    "' field definition, it already exists!"
            );
        }
    }
}
