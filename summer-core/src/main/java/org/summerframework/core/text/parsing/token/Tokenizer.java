package org.summerframework.core.text.parsing.token;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

/**
 * The generic tokenizer.
 *
 * @author Josef Boukal
 */
public abstract class Tokenizer<T, S> implements Iterable<Token<T, S>>, Iterator<Token<T, S>> {

    protected final char[] text;
    protected final int size;
    protected final String[] separators;
    protected char escape = '\\';

    protected int index = 0;

    protected Tokenizer(String text, String... separators) {
        this(text.toCharArray(), separators);
    }

    protected Tokenizer(char[] text, String... separators) {
        this.text = text;
        this.size = text.length;
        this.separators = separators;
    }

    public void setEscape(char escape) {
        this.escape = escape;
    }

    protected Token<String, String> nextToken() {
        char previous = (char) -1;
        StringBuilder token = new StringBuilder(32);
        while (index < size) {
            char ch = text[index++];
            if (previous == escape) {
                token.append(ch);
                previous = (char) -1;
                continue;
            }
            if (ch == escape) {
                previous = ch;
                continue;
            }
            token.append(ch);
            String separator = getSeparator(token);
            if (separator != null) {
                int end = token.length() - separator.length();
                return new Token<>(token.substring(0, end), separator);
            }
            previous = ch;
        }
        if (token.length() > 0) {
            return new Token<>(token.toString(), null);
        }
        return null;
    }

    protected String getSeparator(StringBuilder token) {
        next_separator:
        for (String separator : separators) {
            char[] chars = separator.toCharArray();
            int s, t;
            for (s = chars.length - 1, t = token.length() - 1; s >= 0 && t >= 0; s--, t--) {
                if (token.charAt(t) != chars[s]) {
                    continue next_separator;
                }
            }
            if (s < 0) {
                return separator;
            }
        }
        return null;
    }

    @Override
    public boolean hasNext() {
        return index < size;
    }

    @Override
    public void remove() {
        throw new UnsupportedOperationException("Remove is not supported on " + this + " tokenizer!");
    }

    public List<Token<T, S>> all() {
        List<Token<T, S>> result = new ArrayList<>(16);
        for (Token<T, S> token : this) {
            result.add(token);
        }
        return result;
    }
}
