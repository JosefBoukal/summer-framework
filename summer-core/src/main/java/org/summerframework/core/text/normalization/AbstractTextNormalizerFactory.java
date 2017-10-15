package org.summerframework.core.text.normalization;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.HashMap;
import java.util.Map;

/**
 * TODO
 *
 * @author Josef Boukal
 */
abstract class AbstractTextNormalizerFactory<T extends TextNormalizer> implements TextNormalizerFactory {
    private static final Logger log = LoggerFactory.getLogger(AbstractTextNormalizerFactory.class);

    protected Map<String, TextNormalizer> normalizers = new HashMap<>();

    public TextNormalizer normalizer() {
        return normalizer(null);
    }

    public TextNormalizer normalizer(String name) {
        TextNormalizer result = normalizers.get(name);
        if (result == null) {
            result = createNormalizer(name);
            normalizers.put(name, result);
        }
        return result;
    }

    protected abstract T createNormalizer(String name);

    public void register(TextNormalizer normalizer, String name) {
        TextNormalizer previous = normalizers.put(name, normalizer);
        if (previous != null) {
            log.warn("The " + previous + " normalizer of the '" + name + "' has been replaced by a new one!");
        }
    }

}
