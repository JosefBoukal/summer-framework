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
abstract class AbstractNormalizerFactory<T extends Normalizer> implements NormalizerFactory {
    private static final Logger log = LoggerFactory.getLogger(AbstractNormalizerFactory.class);

    protected Map<String, Normalizer> normalizers = new HashMap<>();

    public Normalizer normalizer() {
        return normalizer(null);
    }

    public Normalizer normalizer(String name) {
        Normalizer result = normalizers.get(name);
        if (result == null) {
            result = createNormalizer(name);
            normalizers.put(name, result);
        }
        return result;
    }

    protected abstract T createNormalizer(String name);

    public void register(Normalizer normalizer, String name) {
        Normalizer previous = normalizers.put(name, normalizer);
        if (previous != null) {
            log.warn("The " + previous + " normalizer of the '" + name + "' has been replaced by a new one!");
        }
    }

}
