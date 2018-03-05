package org.summerframework.core.text.parsing.token;

import org.junit.Assert;
import org.junit.Test;
import org.springframework.core.convert.ConversionService;
import org.springframework.core.convert.support.DefaultConversionService;

public class TokenizerTest {
    private ConversionService conversionService = new DefaultConversionService();

    @Test
    public void testIntAsStrings() {
        String text = "1|2|3|4|10..25|8|9|11";
        StringTokenizer tokenizer = new StringTokenizer(text, "|", "..");
        String[] tokens = new String[]{"1", "2", "3", "4", "10", "25", "8", "9", "11"};
        String[] separators = new String[]{"|", "|", "|", "|", "..", "|", "|", "|", null};
        for (int i = 0, tokensLength = tokens.length; i < tokensLength; i++) {
            String token = tokens[i];
            String separator = separators[i];
            Token<String, String> next = tokenizer.next();
            Assert.assertEquals(token, next.getValue());
            Assert.assertEquals(separator, next.getSeparator());
        }
        Assert.assertFalse(tokenizer.hasNext());
    }

    @Test
    public void testIntegers() {
        String text = "1|2|3|4|10..25|8|9|11";
        GenericTokenizer<Integer> tokenizer = new GenericTokenizer<>(conversionService, Integer.class, text, "|", "..");
        Integer[] tokens = new Integer[]{1, 2, 3, 4, 10, 25, 8, 9, 11};
        String[] separators = new String[]{"|", "|", "|", "|", "..", "|", "|", "|", null};
        for (int i = 0, tokensLength = tokens.length; i < tokensLength; i++) {
            Integer token = tokens[i];
            String separator = separators[i];
            Token<Integer, String> next = tokenizer.next();
            Assert.assertEquals(token, next.getValue());
            Assert.assertEquals(separator, next.getSeparator());
        }
        Assert.assertFalse(tokenizer.hasNext());
    }

    @Test
    public void testCharsAsString() {
        String text = "A|B|C|D|E..CH|D|C|B|X..Z|R";
        StringTokenizer tokenizer = new StringTokenizer(text, "|", "..");
        String[] tokens = new String[]{"A", "B", "C", "D", "E", "CH", "D", "C", "B", "X", "Z", "R"};
        String[] separators = new String[]{"|", "|", "|", "|", "..", "|", "|", "|", "|", "..", "|", null};
        for (int i = 0, tokensLength = tokens.length; i < tokensLength; i++) {
            String token = tokens[i];
            String separator = separators[i];
            Token<String, String> next = tokenizer.next();
            Assert.assertEquals(token, next.getValue());
            Assert.assertEquals(separator, next.getSeparator());
        }
        Assert.assertFalse(tokenizer.hasNext());
    }

    @Test
    public void testEscapes() {
        String text = "school\\|1\\\\|";
        StringTokenizer tokenizer = new StringTokenizer(text, "|", "..");
        String[] expected = new String[]{"school|1\\"};
        for (String e : expected) {
            Assert.assertEquals(e, tokenizer.next().getValue());
        }
        Assert.assertFalse(tokenizer.hasNext());

        text = "school\\|1\\\\";
        tokenizer = new StringTokenizer(text, "|", "..");
        expected = new String[]{"school|1\\"};
        for (String e : expected) {
            Assert.assertEquals(e, tokenizer.next().getValue());
        }
        Assert.assertFalse(tokenizer.hasNext());

    }

}