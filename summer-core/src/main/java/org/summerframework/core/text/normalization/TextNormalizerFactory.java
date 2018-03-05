package org.summerframework.core.text.normalization;

/**
 * A normalizer factory that takes care for {@link TextNormalizer} management.
 */
public interface TextNormalizerFactory {

    void register(TextNormalizer normalizer, String name);

    TextNormalizer normalizer();

    TextNormalizer normalizer(String name);

}
