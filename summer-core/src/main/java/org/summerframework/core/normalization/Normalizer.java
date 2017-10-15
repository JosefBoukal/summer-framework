package org.summerframework.core.normalization;

/**
 * A normalizer is convert value from a given format to its normalized form.
 *
 * @author Josef Boukal
 */
public interface Normalizer<T> {

    T normalize(T value);

}
