package org.summerframework.core.text.normalization;

import org.junit.Assert;
import org.junit.Test;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.summerframework.util.DurationUtils;

import java.util.Locale;

/**
 * TextNormalizer Tester.
 */
public class TextNormalizerTest {
    private static final Logger log = LoggerFactory.getLogger(TextNormalizerTest.class);

    @Test
    public void testNormalizeWithEnglish() {
        StringNormalizer normalizer = new StringNormalizer("/META-INF/normalize/url/");
        String expected;
        String actual;
        expected = "AAAAAACEEEEIIIIDNOOOOOOUUUUY";
        actual = normalizer.normalize("ÀÁÂÃÄÅÆÇÈÉÊËÌÍÎÏÐÑÒÓÔÕÖ×ØÙÚÛÜÝ", Locale.ENGLISH);
        Assert.assertEquals(expected, actual);
        expected = "AaGgGgKkOoOoGgNnAaOo";
        actual = normalizer.normalize("ǠǡǢǣǤǥǦǧǨǩǪǫǬǭǮǰǱǲǳǴǵǶǷǸǹǺǻǼǽǾǿ", Locale.ENGLISH);
        Assert.assertEquals(expected, actual);
    }

    @Test
    public void testNormalizeWithCzech() {
        StringNormalizer normalizer = new StringNormalizer("/META-INF/normalize/url/");
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
        String expected = "scrzyaieeuuSCRZYAEEUUDdTtNnOo";
        String actual = normalizer.normalize("ščřžýáíéěúůŠČŘŽÝÁÉĚÚŮĎďŤťŇňÓó");
        Assert.assertEquals(expected, actual);
    }
}
