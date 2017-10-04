package org.summerframework.core.model;

import java.util.Collections;
import java.util.EnumSet;
import java.util.Set;

/**
 * @author Josef Boukal
 */
public enum AccessRight {

    /**
     * Used to signal that a field value may be read. For example a password field is usually write only thus when a
     * field is missing this right no value will be provided (or it will be cleared) when accessed to read it.
     */
    Read,

    /**
     * Used to signal that a field value may be modified.
     */
    Write;

    public static final Set<AccessRight> ALL = Collections.unmodifiableSet(EnumSet.allOf(AccessRight.class));

}
