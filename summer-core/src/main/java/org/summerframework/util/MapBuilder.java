package org.summerframework.util;

import java.util.LinkedHashMap;
import java.util.Map;

/**
 * The Map builder with String keys and typed values using fluent/chained API.
 */
public class MapBuilder<V> {
    private static final int DEFAULT_CAPACITY = 8;
    private final Map<String, V> sink;

    public MapBuilder() {
        this(new LinkedHashMap<>(DEFAULT_CAPACITY));
    }

    /**
     * Creates an empty Map builder with the given sink.
     */
    public MapBuilder(Map<String, V> sink) {
        this.sink = sink;
    }

    /**
     * Creates a builder with the the given <code>value</code> and <code>key</code> as the first argument.
     */
    public MapBuilder(String key, V value) {
        this(new LinkedHashMap<>(DEFAULT_CAPACITY));
        set(key, value);
    }

    /**
     * Adds the given id with the "id" key to the map.
     *
     * @param id the id object for the "id" key
     */
    public MapBuilder<V> id(V id) {
        sink.put("id", id);
        return this;
    }

    /**
     * Adds the given value with the "value" key.
     *
     * @param value the value for the value key
     */
    public MapBuilder<V> value(V value) {
        sink.put("value", value);
        return this;
    }

    /**
     * Sets the given value under the given key name to this builder's sink.
     *
     * @param name  the name of the key
     * @param value the value, may be null
     */
    public MapBuilder<V> set(String name, V value) {
        sink.put(name, value);
        return this;
    }

    /**
     * Builds the map of arguments from the local state. The result is a modifiable map with no reference to this
     * builder so the builder may be used to continue with the actual state.
     */
    public Map<String, V> build() {
        return new LinkedHashMap<>(this.sink);
    }

}
