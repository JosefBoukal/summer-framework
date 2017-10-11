package org.summerframework.core.text.parsing.token;

import org.springframework.core.convert.ConversionService;

import java.util.Iterator;

/**
 * @author Josef Boukal
 */
public class GenericTokenizer<T> extends Tokenizer<T, String> {

    private final Class<T> targetType;
    private final ConversionService service;

    public GenericTokenizer(ConversionService service, Class<T> type, String text, String... separators) {
        this(service, type, text.toCharArray(), separators);
    }

    public GenericTokenizer(ConversionService service, Class<T> type, char[] text, String... separators) {
        super(text, separators);
        this.service = service;
        this.targetType = type;
    }

    @Override
    public Iterator<Token<T, String>> iterator() {
        return new GenericTokenizer<>(service, targetType, text, separators);
    }

    @SuppressWarnings("unchecked")
    @Override
    public Token<T, String> next() {
        Token<String, String> token = nextToken();
        if (String.class != targetType) {
            return token.copy(service.convert(token.getValue(), targetType));
        }
        return (Token<T, String>) token;
    }
}
