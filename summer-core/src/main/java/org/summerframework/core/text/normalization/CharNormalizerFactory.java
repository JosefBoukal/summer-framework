package org.summerframework.core.text.normalization;


import org.summerframework.util.StringUtils;

/**
 * The {@link TextNormalizerFactory} provides {@link TextNormalizer} doing very fast normalization based converting a
 * single char to char.
 */
public class CharNormalizerFactory extends AbstractTextNormalizerFactory<TextNormalizer> {

    protected String location = "/META-INF/normalize/";

    public static final CharNormalizerFactory DEFAULT = new CharNormalizerFactory();

    public CharNormalizerFactory() {
    }

    public CharNormalizerFactory(String location) {
        location = StringUtils.cleanPath(location);
        if (!location.endsWith("/")) {
            location += "/";
        }
        this.location = location;
    }

    @Override
    protected TextNormalizer createNormalizer(String name) {
        StringBuilder location = new StringBuilder(this.location);
        if (StringUtils.hasLength(name)) {
            location.append(name);
        }
        return new CharNormalizer(location.toString());
    }

}
