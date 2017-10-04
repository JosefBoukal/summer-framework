package org.summerframework.experimental.data.model.definition;

import lombok.extern.slf4j.Slf4j;
import org.springframework.data.util.TypeInformation;

/**
 * @author Josef Boukal
 */
@Slf4j
public class MapDefinition<K, T> extends AbstractFieldDefinition<T> {

    private FieldDefinition<K> keyDefinition;
    private FieldDefinition<T> valueDefinition;

    public MapDefinition(String name, TypeInformation<T> typeInformation) {
        super(name, typeInformation);
        if (log.isTraceEnabled()) {
            log.trace("Map field of the '" + name + "' name created");
        }
    }

    public void setKeyDefinition(FieldDefinition<K> keyDefinition) {
        if (sealed) {
            throw new IllegalStateException("Unable to set the " + keyDefinition + " key definition on the " + this +
                    " sealed map definition!"
            );
        }
        this.keyDefinition = keyDefinition;
    }

    public FieldDefinition<K> getKeyDefinition() {
        return keyDefinition;
    }

    public void setValueDefinition(FieldDefinition<T> valueDefinition) {
        if (sealed) {
            throw new IllegalStateException("Unable to set the " + valueDefinition + " value definition on the " + this +
                    " sealed map definition!"
            );
        }
        this.valueDefinition = valueDefinition;
    }

    public FieldDefinition<T> getValueDefinition() {
        return valueDefinition;
    }

}
