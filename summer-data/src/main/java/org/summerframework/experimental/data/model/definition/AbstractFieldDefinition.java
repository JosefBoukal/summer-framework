package org.summerframework.experimental.data.model.definition;

import org.springframework.data.util.TypeInformation;
import org.springframework.util.Assert;
import org.summerframework.core.model.AccessRight;
import org.summerframework.util.StringUtils;

import java.util.Collections;
import java.util.Set;

public abstract class AbstractFieldDefinition<T> implements FieldDefinition<T> {
    protected final String name;
    protected final String type;
    protected final TypeInformation<T> typeInformation;
    protected Set<AccessRight> accessRights = AccessRight.ALL;

    protected boolean sealed;

    public AbstractFieldDefinition(String name, TypeInformation<T> typeInformation) {
        this(name, null, typeInformation);
    }

    public AbstractFieldDefinition(String name, String type, TypeInformation<T> typeInformation) {
        Assert.notNull(typeInformation, "Unable to create a field definition without a Java Type Information!");
        if (type == null) {
            type = typeInformation.getType().getSimpleName();
        }
        if (name == null) {
            name = StringUtils.uncapitalize(type);
        }
        this.name = name;
        this.type = type;
        this.typeInformation = typeInformation;
        printInfo();
    }

    private void printInfo() {
        /*System.out.println("------ " + name + " ------");
        System.out.println(typeInformation);
        System.out.println("Actual Type: " + typeInformation.getActualType());
        List<TypeInformation<?>> typeArguments = typeInformation.getTypeArguments();
        if (typeArguments.size() > 0) {
            System.out.println("Type Arguments: " + typeArguments);
        }
        if (typeInformation.isCollectionLike()) {
            System.out.println("Component type: " + typeInformation.getComponentType());
        }
        if (typeInformation.isMap()) {
            System.out.println("Map Value type: " + typeInformation.getMapValueType());
        }*/
    }

    public void seal() {
        this.accessRights = Collections.unmodifiableSet(this.accessRights);
        this.sealed = true;
    }

    @Override
    public String getName() {
        return name;
    }

    @Override
    public String getType() {
        return type;
    }

    @Override
    public TypeInformation<T> getTypeInformation() {
        return typeInformation;
    }

    @Override
    public Set<AccessRight> getAccessRights() {
        return accessRights;
    }

    @Override
    public String toString() {
        final StringBuilder sb = new StringBuilder(64);
        sb.append(getClass().getSimpleName()).append('{');
        sb.append("name='").append(name).append('\'');
        sb.append(", type='").append(type).append('\'');
        sb.append('}');
        return sb.toString();
    }

}
