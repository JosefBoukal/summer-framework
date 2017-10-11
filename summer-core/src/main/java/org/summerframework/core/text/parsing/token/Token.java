package org.summerframework.core.text.parsing.token;

/**
 * A simple token with value and separator as generic type parameters.
 *
 * @author Josef Boukal
 */
public class Token<T, S> {
    private final T value;
    private final S separator;

    public Token(T value, S separator) {
        this.value = value;
        this.separator = separator;
    }

    public T getValue() {
        return value;
    }

    public S getSeparator() {
        return separator;
    }

    protected <T> Token<T, S> copy(T value) {
        return new Token<>(value, separator);
    }

    @Override
    public String toString() {
        final StringBuilder sb = new StringBuilder(value.toString());
        if (separator != null) {
            sb.append(separator);
        }
        return sb.toString();
    }
}
