package org.summerframework.experimental.data.model.definition;

import org.springframework.data.util.TypeInformation;
import org.summerframework.core.model.AccessRight;

import java.util.Set;

/**
 * @author Josef Boukal
 */
public interface FieldDefinition<T> {

    String getName();

    String getType();

    Set<AccessRight> getAccessRights();

    TypeInformation<T> getTypeInformation();

    void seal();
}
