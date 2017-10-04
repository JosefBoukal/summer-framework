package org.summerframework.core.model.field;

import lombok.extern.slf4j.Slf4j;
import org.springframework.util.Assert;
import org.summerframework.util.StringUtils;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.Iterator;
import java.util.List;

/**
 * The immutable object that denotes a path to some field or object in a Map, POJO, JSON, etc. Path consists of a list
 * of elements where an element may be either of String or Integer type for fields or index arrays respectively. The
 * astrix ('*') wildcard may be used to denote any field or index at a given level. Any path with a wildcard denotes
 * indirect path for which the {@link #isDirect()} method returns false. There must be at least one filed given for any
 * path as the root path is represented by <code>null</code>.
 */
@Slf4j
public class FieldPath implements Iterable<PathElement<?>> {
    private final PathElement[] elements;
    private final boolean direct;
    private final int hashCode;

    /**
     * Creates a path to a field from the given field elements. For example given "boo", "foo" the "boo.foo" path is
     * created. There must be at least one element given.
     *
     * @param elements optional list of elements, any given elements must not be null
     * @throws IllegalArgumentException If there is no element given or any of them is invalid (empty or null).
     */
    public FieldPath(String... elements) {
        if (elements.length == 0) {
            throw new IllegalArgumentException("At least one element is required to create a field path!");
        }
        PathElement[] items = new PathElement[elements.length];
        for (int i = 0; i < elements.length; i++) {
            String element = elements[i];
            items[i] = "*".equals(element) ? PathElement.ANY_FIELD : new FieldElement(element);
        }
        this.elements = items;
        this.hashCode = Arrays.hashCode(elements);
        this.direct = resolveDirect();
    }

    /**
     * Creates a field with the given index to an array or collection.
     *
     * @param index the array index must be more than -1
     */
    public FieldPath(int index) {
        this.elements = new PathElement[]{new IndexedElement(index)};
        this.hashCode = Arrays.hashCode(elements);
        this.direct = true;
    }

    public FieldPath(PathElement<?> root) {
        if (root == null) {
            throw new IllegalArgumentException("A root path element is required to create a new field path!");
        }
        this.elements = new PathElement[]{root};
        this.hashCode = Arrays.hashCode(elements);
        this.direct = root != PathElement.ANY_FIELD;
    }

    public FieldPath(PathElement<?>[] elements) {
        if (elements.length == 0) {
            throw new IllegalArgumentException("At least one element is required to create a field path!");
        }
        this.elements = elements;
        this.hashCode = Arrays.hashCode(elements);
        this.direct = resolveDirect();
    }

    /**
     * Creates and returns a single element for the given field name.
     *
     * @param name the required name or wildcard for {@link PathElement#ANY_FIELD}
     */
    public static PathElement element(String name) {
        return "*".equals(name) ? PathElement.ANY_FIELD : new FieldElement(name);
    }

    /**
     * Creates and returns a single element for the given field index.
     *
     * @param index the index to a collection or array field
     */
    public static PathElement element(int index) {
        return new IndexedElement(index);
    }

    /**
     * Returns an array of the elements this path consists of.
     */
    public PathElement<?>[] elements() {
        PathElement<?>[] result = new PathElement[elements.length];
        System.arraycopy(elements, 0, result, 0, result.length);
        return result;
    }

    /**
     * Returns the number of the elements this path consists of.
     */
    public int length() {
        return elements.length;
    }

    /**
     * Returns the String representation of the last element.
     */
    public String field() {
        // there is always at least a single item
        Object value = elements[elements.length - 1].value();
        return value == null ? null : value.toString();
    }

    /**
     * Returns the path iterable of this field path, that is the path without the last element.
     */
    public Iterable<PathElement<?>> path() {
        // there is always at least a single item
        int length = elements.length - 1;
        if (length == 0) {
            // root, return empty iterator
            return Collections.emptyList();
        }
        PathElement<?>[] path = new PathElement[length];
        System.arraycopy(elements, 0, path, 0, length);
        return Arrays.asList(path);
    }

    /**
     * Returns the parent of this path or <code>null</code> if this is a single element path.
     */
    public FieldPath parent() {
        if (elements.length <= 1) {
            return null;
        }
        PathElement[] parent = new PathElement[elements.length - 1];
        System.arraycopy(elements, 0, parent, 0, parent.length);
        return new FieldPath(parent);
    }

    /**
     * Appends the given path to this path and returns the result. It may return this path if the source path is not
     * given.
     *
     * @param path the optional path to append
     */
    public FieldPath append(FieldPath path) {
        if (path == null) {
            return this;
        }
        PathElement[] result = new PathElement[elements.length + path.elements.length];
        System.arraycopy(elements, 0, result, 0, elements.length);
        System.arraycopy(path.elements, 0, result, elements.length, path.elements.length);
        return new FieldPath(result);
    }

    /**
     * Returns a new path to the given child field.
     *
     * @param name the required name of the child
     */
    public FieldPath child(String name) {
        PathElement[] elements = new PathElement[this.elements.length + 1];
        System.arraycopy(this.elements, 0, elements, 0, this.elements.length);
        elements[this.elements.length] = "*".equals(name) ? PathElement.ANY_FIELD : new FieldElement(name);
        return new FieldPath(elements);
    }

    public FieldPath child(PathElement element) {
        int last = this.elements.length;
        PathElement[] elements = new PathElement[last + 1];
        System.arraycopy(this.elements, 0, elements, 0, last);
        elements[last] = element;
        return new FieldPath(elements);
    }

    /**
     * Returns a new path to the given indexed child.
     *
     * @param index the index of the child
     */
    public FieldPath child(int index) {
        PathElement[] elements = new PathElement[this.elements.length + 1];
        System.arraycopy(this.elements, 0, elements, 0, this.elements.length);
        elements[this.elements.length] = new IndexedElement(index);
        return new FieldPath(elements);
    }

    /**
     * Returns a new path to the given sibling field.
     *
     * @param element the required element of the sibling
     */
    public FieldPath sibling(PathElement<?> element) {
        Assert.notNull(element, "Unable to navigate to a sibling node, no sibling is given!");
        Object value = element.value();
        if (value instanceof Integer) {
            return sibling((Integer) value);
        } else {
            return sibling(value.toString());
        }
    }

    /**
     * Returns a new path to the given sibling field.
     *
     * @param name the required name of the sibling
     */
    public FieldPath sibling(String name) {
        Assert.notNull(name, "Unable to navigate to a sibling node, no sibling field name is given!");
        PathElement[] elements = new PathElement[this.elements.length];
        int last = elements.length - 1;
        System.arraycopy(this.elements, 0, elements, 0, last);
        elements[last] = "*".equals(name) ? PathElement.ANY_FIELD : new FieldElement(name);
        return new FieldPath(elements);
    }

    /**
     * Returns a new path to the given indexed sibling.
     *
     * @param index the index of the sibling
     */
    public FieldPath sibling(int index) {
        PathElement[] elements = new PathElement[this.elements.length];
        int last = elements.length - 1;
        System.arraycopy(this.elements, 0, elements, 0, last);
        elements[last] = new IndexedElement(index);
        return new FieldPath(elements);
    }

    /**
     * Returns the tail (that is the last) element of this path, it never returns null.
     */
    public PathElement<?> tail() {
        // there is always at least one element
        return elements[elements.length - 1];
    }

    /**
     * Returns the head (that is the first) element of this path, it never returns null.
     */
    public PathElement<?> head() {
        // there is always at least one element
        return elements[0];
    }


    /**
     * Returns the String representation of this path.
     */
    public String value() {
        final StringBuilder sb = new StringBuilder(elements.length * 8);
        for (PathElement item : elements) {
            item.printTo(sb);
        }
        return sb.toString();
    }

    /**
     * Returns true if this path denotes a direct path that is that there is no wildcard field in any element of this
     * path.
     */
    public boolean isDirect() {
        return direct;
    }

    private boolean resolveDirect() {
        for (PathElement item : elements) {
            if (item == PathElement.ANY_FIELD) {
                return false;
            }
        }
        return true;
    }

    /**
     * Returns true if the other path matches this one. This methods differs from the equals in the way that it also
     * handles wildcard elements of any path and it compares only the shortest paths' elements.
     *
     * @param other the optional other path to match against
     */
    public boolean matches(FieldPath other) {
        if (other == null) {
            return false;
        }
        if (this == other) {
            return true;
        }
        int length = Math.min(this.elements.length, other.elements.length);
        for (int i = 0; i < length; i++) {
            if (!elements[i].matches(other.elements[i])) {
                return false;
            }
        }
        return true;
    }

    /**
     * Returns true if the other path matches this one. This methods differs from the equals in the way that it also
     * handles wildcard elements of any path and it compares only the shortest paths' elements.
     *
     * @param path the optional other path to match against
     */
    public boolean matches(String path) {
        return matches(FieldPath.valueOf(path));
    }

    /**
     * Returns true if the given object is FieldPath with the exactly the same set of elements.
     *
     * @param o the optional other path
     */
    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;

        FieldPath that = (FieldPath) o;
        int length = elements.length;
        if (length != that.elements.length) {
            return false;
        }
        // iterate from the end as it is more likely that the paths are not equal from the tail
        for (int i = length - 1; i >= 0; i--) {
            if (!elements[i].equals(that.elements[i])) {
                return false;
            }
        }
        return true;
    }

    @Override
    public int hashCode() {
        return hashCode;
    }

    @Override
    public String toString() {
        return value();
    }

    /**
     * Creates a new field path from the given path. It returns null if empty or no path is given at all. TODO describe
     * more.
     *
     * @param path the optional path
     */
    public static FieldPath valueOf(String path) {
        if (StringUtils.isEmpty(path)) {
            return null;
        }
        PathParser parser = new PathParser(path);
        List<PathElement> items = new ArrayList<>();
        while (parser.hasNext()) {
            Object token = parser.next();
            if (token instanceof Number) {
                items.add(new IndexedElement(((Number) token).intValue()));
            } else if ("*".equals(token)) {
                items.add(PathElement.ANY_FIELD);
            } else {
                items.add(new FieldElement(token.toString()));
            }
        }
        return new FieldPath(items.toArray(new PathElement[items.size()]));
    }

    /**
     * Creates a new field path from the given path. It returns null if empty or no path element is given at all.
     *
     * @param elements the optional path of elements
     */
    public static FieldPath valueOf(List<PathElement<?>> elements) {
        if (elements == null || elements.size() == 0) {
            return null;
        }
        return new FieldPath(elements.toArray(new PathElement[elements.size()]));
    }

    @Override
    public Iterator<PathElement<?>> iterator() {
        return new PathElementIterator();
    }

    public boolean startsWith(FieldPath path) {
        if (path == null || path.length() > this.length()) {
            return false;
        }
        for (int i = 0; i < path.elements.length; i++) {
            PathElement<?> thisElement = this.elements[i];
            PathElement<?> thatElement = path.elements[i];
            if (thisElement.matches(thatElement)) {
                continue;
            }
            return false;
        }
        return true;
    }

    protected class PathElementIterator implements Iterator<PathElement<?>> {
        int i = 0;

        @Override
        public boolean hasNext() {
            return i < elements.length;
        }

        @Override
        public PathElement next() {
            return elements[i++];
        }

    }

    private static class FieldElement implements PathElement<String> {
        private final String field;

        private FieldElement(String field) {
            if (StringUtils.isEmpty(field)) {
                throw new IllegalArgumentException("Unable to create a field element, no or empty field name is given!");
            }
            if (field.indexOf('.') > 0) {
                throw new IllegalArgumentException("Unable to create a field element, the '" + field
                        + "' field contains a dot character which is not allowed!");
            }

            this.field = field;
        }

        @Override
        public String value() {
            return field;
        }

        @Override
        public boolean matches(PathElement other) {
            return (other instanceof FieldElement && ((FieldElement) other).field.equals(field)) || ANY_FIELD == other;
        }

        @Override
        public void printTo(StringBuilder builder) {
            if (builder.length() > 0) {
                builder.append('.');
            }
            builder.append(field);
        }

        @Override
        public boolean equals(Object o) {
            if (this == o) return true;
            if (o == null || getClass() != o.getClass()) return false;

            FieldElement that = (FieldElement) o;
            return field.equals(that.field);
        }

        @Override
        public int hashCode() {
            return field.hashCode();
        }

        @Override
        public String toString() {
            return "'" + field + "'";
        }
    }

    private static class IndexedElement implements PathElement<Integer> {
        private final int index;

        private IndexedElement(int index) {
            if (index < 0) {
                throw new IllegalArgumentException("Unable to create an indexed element, the " + index +
                        " index must be zero or positive!");
            }
            this.index = index;
        }

        @Override
        public void printTo(StringBuilder builder) {
            builder.append('[').append(index).append(']');
        }

        @Override
        public Integer value() {
            return index;
        }

        @Override
        public boolean matches(PathElement other) {
            return ((other instanceof IndexedElement) && ((IndexedElement) other).index == index) || other == ANY_FIELD;
        }

        @Override
        public boolean equals(Object o) {
            if (this == o) return true;
            if (o == null || getClass() != o.getClass()) return false;

            IndexedElement that = (IndexedElement) o;
            return index == that.index;
        }

        @Override
        public int hashCode() {
            return index;
        }

        @Override
        public String toString() {
            return "[" + index + "]";
        }
    }

    private static class PathParser {
        private final char[] value;
        private int i = 0;
        private StringBuilder token = new StringBuilder(8);
        // state variable
        private boolean inArray = false;

        PathParser(String value) {
            this.value = value.toCharArray();
        }

        public boolean hasNext() {
            return i < value.length;
        }

        public Object next() {
            // clear the token first
            token.setLength(0);
            while (i < value.length) {
                char ch = value[i++];
                if (ch == '.') {
                    // item separator
                    if (token.length() == 0) {
                        // probably the end of array + field separator '].' continue on next char
                        continue;
                    }
                    // the read token must be a generic String element
                    return token.toString();
                }
                if (ch == '[') {
                    inArray = true;
                    return token.toString();
                }
                if (ch == ']') {
                    // TODO PathSyntaxException?
                    if (!inArray) {
                        throw new IllegalStateException("End of array ']' encountered at position " + i + " in the '"
                                + new String(value) + "' text, but a start character '[' is missing!");
                    }
                    inArray = false;
                    String value = token.toString();
                    try {
                        return Integer.valueOf(value);
                    } catch (NumberFormatException e) {
                        // an associative array like in JavaScript is probably given
                        return value;
                    }
                }
                token.append(ch);
            }
            return token.toString();
        }
    }

    public static PathElement<?>[] concat(int index, PathElement<?>[] children) {
        PathElement<?>[] result = new PathElement[1 + children.length];
        result[0] = new IndexedElement(index);
        System.arraycopy(children, 0, result, 1, children.length);
        return result;
    }

    public static PathElement<?>[] concat(String field, PathElement<?>[] children) {
        PathElement<?>[] result = new PathElement[1 + children.length];
        result[0] = new FieldElement(field);
        System.arraycopy(children, 0, result, 1, children.length);
        return result;
    }

}
