package org.summerframework.experimental.data.model.definition;

import lombok.extern.slf4j.Slf4j;
import org.springframework.data.util.TypeInformation;

@Slf4j
public class CollectionDefinition<T> extends AbstractFieldDefinition<T> {

    private FieldDefinition<T> componentDefinition;

    public CollectionDefinition(String name, TypeInformation<T> typeInformation) {
        super(name, typeInformation);
        if (log.isTraceEnabled()) {
            log.trace("Collection field of the '" + name + "' name created");
        }
    }

    protected void setComponentDefinition(FieldDefinition<T> componentDefinition) {
        if (sealed) {
            throw new IllegalStateException("Unable to set the " + componentDefinition + " on the " + this + " sealed definition!");
        }
        this.componentDefinition = componentDefinition;
    }

    public FieldDefinition<?> getComponentDefinition() {
        return componentDefinition;
    }

}
