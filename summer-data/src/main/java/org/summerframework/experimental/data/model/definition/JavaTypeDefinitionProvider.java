package org.summerframework.experimental.data.model.definition;

import lombok.extern.slf4j.Slf4j;
import org.springframework.core.Ordered;
import org.springframework.core.annotation.Order;
import org.springframework.data.annotation.Id;
import org.springframework.data.util.ClassTypeInformation;
import org.springframework.data.util.TypeInformation;
import org.springframework.util.Assert;

import java.lang.reflect.Field;
import java.lang.reflect.Modifier;
import java.math.BigDecimal;
import java.math.BigInteger;
import java.time.Duration;
import java.time.Instant;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.time.ZonedDateTime;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.Date;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.Set;

/**
 * @author Josef Boukal
 */
@Slf4j
public class JavaTypeDefinitionProvider {

    private static final JavaTypeDefinitionProvider INSTANCE = new JavaTypeDefinitionProvider();

    private final Map<Class<?>, ModelDefinition<?>> modelCache = new HashMap<>(64);

    private final Set<Class<?>> simpleTypes = new HashSet<>(32);

    {
        simpleTypes.add(boolean.class);
        simpleTypes.add(Boolean.class);
        simpleTypes.add(byte.class);
        simpleTypes.add(Byte.class);
        simpleTypes.add(short.class);
        simpleTypes.add(Short.class);
        simpleTypes.add(int.class);
        simpleTypes.add(Integer.class);
        simpleTypes.add(long.class);
        simpleTypes.add(Long.class);
        simpleTypes.add(float.class);
        simpleTypes.add(Float.class);
        simpleTypes.add(double.class);
        simpleTypes.add(Double.class);
        simpleTypes.add(char.class);
        simpleTypes.add(Character.class);
        simpleTypes.add(String.class);
        simpleTypes.add(BigDecimal.class);
        simpleTypes.add(BigInteger.class);
        // Date and Time
        simpleTypes.add(Date.class);
        simpleTypes.add(Duration.class);
        simpleTypes.add(Instant.class);
        simpleTypes.add(LocalDate.class);
        simpleTypes.add(LocalDateTime.class);
        simpleTypes.add(LocalTime.class);
        simpleTypes.add(ZonedDateTime.class);
        // others
        simpleTypes.add(Locale.class);
        simpleTypes.add(Enum.class);
    }

    public static JavaTypeDefinitionProvider getInstance() {
        return INSTANCE;
    }

    public void addSimpleType(Class<?> javaType) {
        simpleTypes.add(javaType);
    }

    /**
     * Returns the Java POJO model definition.
     *
     * @param javaType the model type
     */
    @SuppressWarnings("unchecked")
    public <T, ID> EntityDefinition<T, ID> getEntityDefinition(Class<T> javaType, Class<ID> idJavaType) {
        return getEntityDefinition(null, javaType, idJavaType);
    }

    /**
     * Returns the Java POJO model definition.
     *
     * @param name     the optional name of the model, simple name of the given Java type is used if not specified
     * @param javaType the model type
     */
    @SuppressWarnings("unchecked")
    public <T, ID> EntityDefinition<T, ID> getEntityDefinition(String name, Class<T> javaType, Class<ID> idJavaType) {
        ModelDefinition<T> modelDefinition = getModelDefinition(name, javaType);
        if (modelDefinition instanceof EntityDefinition) {
            return (EntityDefinition<T, ID>) modelDefinition;
        }
        return null;
    }

    /**
     * Returns the Java POJO model definition.
     *
     * @param javaType the model type
     */
    @SuppressWarnings("unchecked")
    public <T> ModelDefinition<T> getModelDefinition(Class<T> javaType) {
        return getModelDefinition(null, javaType);
    }

    /**
     * Returns the Java POJO model definition.
     *
     * @param name     the optional name of the model, simple name of the given Java type is used if not specified
     * @param javaType the model type
     */
    @SuppressWarnings("unchecked")
    public <T> ModelDefinition<T> getModelDefinition(String name, Class<T> javaType) {
        return (ModelDefinition<T>) modelCache.computeIfAbsent(javaType, key -> resolveModelDefinition(name, javaType));
    }

    @SuppressWarnings("unchecked")
    public <T> FieldDefinition<T> getFieldDefinition(String name, TypeInformation<T> typeInformation) {
        if (simpleTypes.contains(typeInformation.getType())) {
            return new SimpleFieldDefinition<>(name, typeInformation);
        }
        if (typeInformation.isCollectionLike()) {
            CollectionDefinition<T> result = new CollectionDefinition<>(name, typeInformation);
            TypeInformation<T> componentType = (TypeInformation<T>) typeInformation.getComponentType();
            // use the "item" name for the component
            result.setComponentDefinition(getFieldDefinition("item", componentType));
            result.seal();
            return result;
        }
        if (typeInformation.isMap()) {
            MapDefinition<Object, T> result = new MapDefinition<>(name, typeInformation);
            TypeInformation keyType = typeInformation.getTypeArguments().get(0);
            TypeInformation<T> valueType = (TypeInformation<T>) typeInformation.getRequiredMapValueType();
            // use the "key" name for the key
            result.setKeyDefinition(getFieldDefinition("key", keyType));
            // use the "value" name for the value
            result.setValueDefinition(getFieldDefinition("value", valueType));
            result.seal();
            return result;
        }
        return getModelDefinition(name, typeInformation.getType());
    }

    private <T> ModelDefinition<T> resolveModelDefinition(String name, Class<T> javaType) {
        Assert.notNull(javaType, "Unable to resolve a model definition, no type is given!");
        ClassTypeInformation<T> typeInformation = ClassTypeInformation.from(javaType);
        ModelDefinition<T> result;
        FieldDefinition<?> idFieldDefinition = resolveIdFieldDefinition(javaType, null);
        if (idFieldDefinition != null) {
            result = new EntityDefinition<>(name, typeInformation, idFieldDefinition);
        } else {
            result = new ModelDefinition<>(name, typeInformation);
        }
        // put the new model to the cache so that we will not resolve it again when recursively defined
        modelCache.put(javaType, result);

        List<Field> fields = resolveAllGenericFields(javaType);
        for (Field field : fields) {
            String fieldName = field.getName();
            TypeInformation<?> fieldType = typeInformation.getProperty(fieldName);
            FieldDefinition<?> fieldDefinition = getFieldDefinition(fieldName, fieldType);
            result.addFieldDefinition(fieldName, fieldDefinition);
        }
        result.seal();
        if (log.isDebugEnabled()) {
            if (result instanceof EntityDefinition) {
                log.debug("Entity definition " + result + " has just been resolved");
            } else {
                log.debug("Model definition " + result + " has just been resolved");
            }
        }
        return result;
    }

    @SuppressWarnings("unchecked")
    private <T> FieldDefinition<T> resolveIdFieldDefinition(Class<?> entityType, Class<T> idType) {
        Field field = resolveIdField(entityType, idType);
        if (field == null) {
            return null;
        }
        TypeInformation<T> typeInformation = ClassTypeInformation.from((Class<T>) field.getType());
        return getFieldDefinition(field.getName(), typeInformation);
    }

    @SuppressWarnings("unchecked")
    private <T> Field resolveIdField(Class<?> entityType, Class<T> idType) {
        Class<?> type = entityType;

        // try to lookup directly using the Spring data @Id annotation
        while (type != Object.class) {
            Field[] fields = type.getDeclaredFields();
            for (Field field : fields) {
                if (Modifier.isStatic(field.getModifiers())) {
                    continue;
                }
                if (EntityDefinition.isIdAnnotatedField(field) && (idType == null || field.getType().equals(idType))) {
                    return field;
                }
            }
            type = type.getSuperclass();
        }

        // try to lookup using the 'id' field name
        type = entityType;
        while (type != Object.class) {
            Field[] fields = type.getDeclaredFields();
            for (Field field : fields) {
                if (Modifier.isStatic(field.getModifiers())) {
                    continue;
                }
                if (EntityDefinition.mayBeIdField(field) && (idType == null || field.getType().equals(idType))) {
                    return field;
                }
            }
            type = type.getSuperclass();
        }
        return null;
    }

    private List<Field> resolveAllGenericFields(Class<?> javaType) {
        List<Class<?>> hierarchy = classHierarchy(javaType);
        List<Field> result = new ArrayList<>(24);
        for (Class<?> type : hierarchy) {
            Field[] fields = type.getDeclaredFields();
            for (Field field : fields) {
                if (Modifier.isStatic(field.getModifiers())) {
                    continue;
                }
                if (field.getAnnotation(Id.class) != null) {
                    continue;
                }
                // TODO use some other ignore annotations?
                result.add(field);
            }
        }
        result.sort(orderedFieldComparator);
        return result;
    }

    private List<Class<?>> classHierarchy(Class<?> javaType) {
        Class<?> type = javaType;
        List<Class<?>> result = new ArrayList<>(6);
        while (type != Object.class) {
            result.add(type);
            type = type.getSuperclass();
        }
        Collections.reverse(result);
        return result;
    }

    private Comparator<Field> orderedFieldComparator = (f1, f2) -> {
        Order a1 = f1.getAnnotation(Order.class);
        Order a2 = f2.getAnnotation(Order.class);
        int o1 = a1 == null ? Ordered.HIGHEST_PRECEDENCE : a1.value();
        int o2 = a2 == null ? Ordered.HIGHEST_PRECEDENCE : a2.value();
        if (o1 == o2) {
            return 0;
        }
        return (o1 < o2) ? -1 : 1;
    };

}
