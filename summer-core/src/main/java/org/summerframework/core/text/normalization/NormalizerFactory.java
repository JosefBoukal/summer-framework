package org.summerframework.core.text.normalization;

/**
 * A normalizer factory that takes care for {@link Normalizer} management.
 *
 * @author Josef Boukal
 */
public interface NormalizerFactory {

    void register(Normalizer normalizer, String name);

    Normalizer normalizer();

    Normalizer normalizer(String name);

}
