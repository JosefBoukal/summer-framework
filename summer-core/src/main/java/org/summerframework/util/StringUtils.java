package org.summerframework.util;

import org.springframework.util.Assert;

import java.text.Normalizer;
import java.util.Random;
import java.util.regex.Pattern;

/**
 * The string utility class that builds upon the famous {@link org.springframework.util.StringUtils}.
 */
public abstract class StringUtils extends org.springframework.util.StringUtils {

    public static String prettyPart(String text, int maxChars) {
        return prettyPart(text, maxChars, "...");
    }

    /**
     * Counts the occurrence of the given character in the given text.
     *
     * @param text the optional text to search in
     * @param ch   the character to search for
     */
    public static int countOccurrenceOf(String text, char ch) {
        if (text == null || text.length() == 0) {
            return 0;
        }
        int count = 0;
        for (char c : text.toCharArray()) {
            if (ch == c) {
                count++;
            }
        }
        return count;
    }

    public static String prettyPart(String text, int maxChars, String suffix) {
        if (text == null || text.length() <= maxChars) {
            return text;
        }
        int pos = maxChars, last = maxChars;
        // try to find the first space character
        while (pos >= 0) {
            char ch = text.charAt(pos);
            if (Character.isSpaceChar(ch)) {
                break;
            }
            pos--;
        }
        if (pos > 0) {
            last = pos;
        }
        return text.substring(0, last) + suffix;
    }

    /**
     * Returns the lowercase text with all the upper case letters prefixed by the '-' <code>mark</code>. It returns null
     * if the <code>null</code> text is given.
     *
     * @param text the text to lower case
     * @see #lowerCase(String, char)
     */
    public static String lowerCase(String text) {
        return lowerCase(text, '-');
    }

    /**
     * Returns the lowercase text with all the upper case letters prefixed by the specified <code>mark</code>. It
     * returns null if the <code>null</code> text is given.
     *
     * @param text          the text to lower case
     * @param upperCaseMark the upper case mark to be inserted before each upper case letter
     */
    public static String lowerCase(String text, char upperCaseMark) {
        if (text == null) {
            return null;
        }
        StringBuilder result = new StringBuilder(text.length());
        char[] chars = text.toCharArray();
        for (char ch : chars) {
            if (Character.isUpperCase(ch)) {
                ch = Character.toLowerCase(ch);
                // skip the upper case mark if this is the first letter
                if (result.length() > 0) {
                    result.append(upperCaseMark);
                }
            }
            result.append(ch);
        }
        return result.toString();
    }


    /**
     * Returns the text camel cased using the '_' as upper case mark and first letter in lower case.
     *
     * @param text the text to camel case, must not be null
     * @see #camelCase(String, char, boolean)
     */
    public static String camelCase(String text) {
        return camelCase(text, '_', false);
    }

    /**
     * Camel cases the given <code>text</code> and returns the result.
     *
     * @param text                 the text to camel case, must not be null
     * @param upperCaseMark        character that signals that a next character should be in upper case
     * @param upperCaseFirstLetter if the first letter should be in upper case
     */
    public static String camelCase(String text, char upperCaseMark, boolean upperCaseFirstLetter) {
        StringBuilder result = new StringBuilder(text.length());
        char[] chars = text.toCharArray();
        boolean upperCase = upperCaseFirstLetter;
        // camel case all the letters
        for (char ch : chars) {
            if (ch == upperCaseMark) {
                upperCase = true;
                continue;
            }
            if (upperCase) {
                upperCase = false;
                ch = Character.toUpperCase(ch);
            } else {
                ch = Character.toLowerCase(ch);
            }
            result.append(ch);
        }
        return result.toString();
    }

    /**
     * Camel cases the given <code>text</code> and returns the result.
     *
     * @param text                 the text to camel case, must not be null
     * @param upperCaseMarks       list of characters that signals that a next character should be in upper case
     * @param upperCaseFirstLetter if the first letter should be in upper case
     */
    public static String camelCase(String text, boolean upperCaseFirstLetter, char... upperCaseMarks) {
        StringBuilder result = new StringBuilder(text.length());
        char[] chars = text.toCharArray();
        boolean upperCase = upperCaseFirstLetter;
        // camel case all the letters
        next_char:
        for (char ch : chars) {
            for (char upperCaseMark : upperCaseMarks) {
                if (ch == upperCaseMark) {
                    upperCase = true;
                    continue next_char;
                }
            }
            if (upperCase) {
                upperCase = false;
                ch = Character.toUpperCase(ch);
            } else {
                ch = Character.toLowerCase(ch);
            }
            result.append(ch);
        }
        return result.toString();
    }

    /**
     * Returns the given text as a property name. It uppercase any character that is marked (prefixed) with the minus
     * sign ('-').
     *
     * @param text the text to change to property name, must not be null
     */
    public static String propertyName(String text) {
        return propertyName(text, '-');
    }

    /**
     * Returns the given text as a property name. It uppercase any character that is marked (prefixed) with the given
     * <code>mark</code>.
     *
     * @param text          the text to change to property name, must not be null
     * @param upperCaseMark the mark that identifies all the characters to be in upper case
     */
    public static String propertyName(String text, char upperCaseMark) {
        StringBuilder result = new StringBuilder(text.length());
        char[] chars = text.toCharArray();
        boolean upperCase = false;
        for (char ch : chars) {
            if (result.length() == 0) {
                // always lower case the first letter
                ch = Character.toLowerCase(ch);
            } else if (ch == upperCaseMark) {
                upperCase = true;
                continue;
            } else if (upperCase) {
                upperCase = false;
                ch = Character.toUpperCase(ch);
            }
            result.append(ch);
        }
        return result.toString();
    }

    private static Pattern REMOVE_ACCENTS_PATTERN = Pattern.compile("\\p{InCombiningDiacriticalMarks}+");

    /**
     * Removes accents from the given string.
     *
     * @param text optional text
     */
    public static String removeAccents(String text) {
        if (hasLength(text)) {
            text = Normalizer.normalize(text, Normalizer.Form.NFD);
            text = REMOVE_ACCENTS_PATTERN.matcher(text).replaceAll("");
        }
        return text;
    }

    public static final char[] HEX = "1234567890ABCDEF".toCharArray();

    public static final char[] hex = "1234567890abcdef".toCharArray();

    private static final Random random = new Random();

    public static String randomHex(int length) {
        return random(random, length, hex);
    }

    public static String randomHex(Random random, int length) {
        return random(random, length, hex);
    }

    public static String randomHex(Random random, int length, boolean upper) {
        return random(random, length, upper ? HEX : hex);
    }

    public static String random(Random random, int length, char[] source) {
        Assert.notNull(source, "Source for random must be given!");
        Assert.state(source.length > 0, "Source for random must not be empty!");
        char[] result = new char[length];
        for (int i = 0; i < length; i++) {
            result[i] = source[random.nextInt(source.length)];
        }
        return new String(result);
    }

}
