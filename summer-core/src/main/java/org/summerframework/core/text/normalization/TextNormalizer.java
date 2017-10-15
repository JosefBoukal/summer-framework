package org.summerframework.core.text.normalization;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.Locale;

/**
 * A text normalizer is an utility that converts character sequence to normalized values.
 *
 * @author Josef Boukal
 */
public interface TextNormalizer {

    int MAX_TABLE_SIZE = 0xFFFF;

    String normalize(CharSequence text);

    String normalize(CharSequence text, Locale locale);

    abstract class Line<T> implements Iterable<T> {
        private static final Logger log = LoggerFactory.getLogger(Line.class);

        final int offset;
        final List<T> values = new ArrayList<>(32);

        public Line(int offset) {
            this.offset = offset;
        }

        public void capture(String value) {
            if (value.length() == 0) {
                // already captured or nothing to capture
                return;
            }
            add(value);
            if (values.size() > 32) {
                log.warn("The '" + value + "' added value caused the line to exceed 32 tokens!");
            }
        }

        // invoked when the value has at least one char
        abstract void add(String value);

        @Override
        public boolean equals(Object o) {
            if (this == o) return true;
            if (o == null || getClass() != o.getClass()) return false;

            Line that = (Line) o;
            return offset == that.offset;
        }

        @Override
        public int hashCode() {
            return offset;
        }

        @Override
        public Iterator<T> iterator() {
            return values.iterator();
        }

        @Override
        public String toString() {
            StringBuilder sb = new StringBuilder(72);
            sb.append(Integer.toHexString(offset));
            for (T value : values) {
                sb.append(' ');
                sb.append(value);
            }
            return sb.toString();
        }
    }

}
