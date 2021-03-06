package org.summerframework.core.text.normalization;

import lombok.extern.slf4j.Slf4j;
import org.junit.Assert;
import org.junit.Test;
import org.summerframework.util.DurationUtils;

import java.util.Locale;

@Slf4j
public class CharNormalizerTest {

    /**
     * Method: normalize(CharSequence text)
     */
    @Test
    public void testNormalizeWithLocale() throws Exception {
        CharNormalizer normalizer = new CharNormalizer("/META-INF/normalize/url/");
        Locale cs_CZ = new Locale("cs", "CZ");
        long time = System.currentTimeMillis();
        int count = 10000;
        for (int i = 0; i < count; i++) {
            normalizer.normalize("Český fájl s velmi dlouhým názvem a šuper čuper češtinou.txt", cs_CZ);
        }
        if (log.isDebugEnabled()) {
            time = System.currentTimeMillis() - time;
            log.debug("It took " + DurationUtils.milliDuration(time) + " to normalize text " + count + " times");
        }
        String expected = "scrzyaieuuSCRZYAEUU";
        String actual = normalizer.normalize("ščřžýáíéúůŠČŘŽÝÁÉÚŮ", cs_CZ);
        Assert.assertEquals(expected, actual);
    }

    /**
     * Method: normalize(CharSequence text)
     */
    @Test
    public void testNormalizePathWithLocale() throws Exception {
        CharNormalizer normalizer = new CharNormalizer("/META-INF/normalize/path/");
        Locale cs_CZ = new Locale("cs", "CZ");
        long time = System.currentTimeMillis();
        int count = 10000;
        for (int i = 0; i < count; i++) {
            normalizer.normalize("Český fájl s velmi dlouhým názvem a šuper čuper češtinou.txt", cs_CZ);
        }
        if (log.isDebugEnabled()) {
            time = System.currentTimeMillis() - time;
            log.debug("It took " + DurationUtils.milliDuration(time) + " to normalize text " + count + " times");
        }
        String expected = "scrzyaieuu SCRZYAEUU";
        String actual = normalizer.normalize("ščřžýáíéúů ŠČŘŽÝÁÉÚŮ", cs_CZ);
        Assert.assertEquals(expected, actual);
    }
}
