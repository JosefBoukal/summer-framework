package org.summerframework.core.normalization;

/**
 * A normalizer is a value converter from a given type and different formats to its normalized form.
 */
public interface Normalizer<T> {

    T normalize(T value);

}
