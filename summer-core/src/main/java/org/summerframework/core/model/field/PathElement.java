package org.summerframework.core.model.field;

/**
 * A {@link FieldPath} element.
 */
public interface PathElement<T> {

    /**
     * Returns the value of this element, that is either a field name or an indexed value.
     */
    T value();

    boolean matches(PathElement other);

    void printTo(StringBuilder builder);

    PathElement ANY_FIELD = new PathElement() {
        private final int hashCode = "*".hashCode();

        @Override
        public void printTo(StringBuilder builder) {
            if (builder.length() > 0) {
                builder.append('.');
            }
            builder.append("*");
        }

        @Override
        public Object value() {
            return this;
        }

        @Override
        public boolean matches(PathElement other) {
            // anything matches, we are any field (wildcard) item/element
            return true;
        }

        @Override
        public boolean equals(Object o) {
            return this == o;
        }

        @Override
        public int hashCode() {
            return hashCode;
        }

        @Override
        public String toString() {
            return "*";
        }
    };

}
