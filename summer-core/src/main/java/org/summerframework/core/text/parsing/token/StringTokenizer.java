package org.summerframework.core.text.parsing.token;

import java.util.Iterator;

/**
 * TODO
 *
 * @author Josef Boukal
 */
public class StringTokenizer extends Tokenizer<String, String> {

    public StringTokenizer(String text, String... separators) {
        super(text, separators);
    }

    public StringTokenizer(char[] text, String... separators) {
        super(text, separators);
    }

    @Override
    public Iterator<Token<String, String>> iterator() {
        return new StringTokenizer(text, separators);
    }

    @Override
    public Token<String, String> next() {
        return nextToken();
    }
}
