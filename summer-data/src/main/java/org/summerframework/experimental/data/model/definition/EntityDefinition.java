package org.summerframework.experimental.data.model.definition;

import org.springframework.data.annotation.Id;
import org.springframework.data.util.TypeInformation;
import org.springframework.util.Assert;

import java.lang.reflect.Field;

public class EntityDefinition<T, ID> extends ModelDefinition<T> {

    private final FieldDefinition<ID> idField;

    public EntityDefinition(String entityName, TypeInformation<T> typeInformation, FieldDefinition<ID> idField) {
        super(entityName, typeInformation);
        Assert.notNull(idField, "Unable to create an entity definition, an id field definition is missing!");
        this.idField = idField;
    }

    public FieldDefinition<ID> getIdField() {
        return idField;
    }


    public static boolean isIdAnnotatedField(Field field) {
        Assert.notNull(field, "Unable to check if a field is an identity field, no field is given!");
        return field.getAnnotation(Id.class) != null;
    }

    public static boolean mayBeIdField(Field field) {
        Assert.notNull(field, "Unable to check if a field may be an identity field, no field is given!");
        return "id".equals(field.getName());
    }

}
