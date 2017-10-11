package org.summerframework.core.text.normalization;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.context.i18n.LocaleContextHolder;
import org.springframework.core.io.ClassPathResource;
import org.springframework.core.io.Resource;
import org.summerframework.core.text.TextParsingException;
import org.summerframework.core.text.parsing.AbstractResourceParser;
import org.summerframework.util.StringUtils;

import java.io.FileNotFoundException;
import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.Set;

/**
 * A text normalizer is an utility class that is capable of converting text characters to other characters (or even)
 * texts using the fast array of String tables.
 *
 * @author Josef Boukal
 */
public class TextNormalizer implements Normalizer {
    private static final Logger log = LoggerFactory.getLogger(TextNormalizer.class);
    public static final String[] ASCII_TABLE = new String[127];

    static {
        // from 32 to 125 inclusive
        for (int i = 0x20; i < ASCII_TABLE.length - 1; i++) {
            ASCII_TABLE[i] = String.valueOf((char) i);
        }
    }

    private final String location;
    private final Map<Locale, String[]> tables = new HashMap<>();
    private String[] defaultTable;

    // the default (ASCII) normalizer
    public TextNormalizer() {
        this.location = "/META-INF/normalize/";
    }

    public TextNormalizer(String location) {
        // normalize location
        if (!location.endsWith("/")) {
            location += "/";
        }
        this.location = location;
    }

    protected String[] getDefaultTable() {
        if (defaultTable == null) {
            // one shot table load
            Resource resource = new ClassPathResource(location + "default.table");
            defaultTable = loadTable(resource);
            if (defaultTable == null) {
                if (log.isWarnEnabled()) {
                    log.warn("Unable to load the default table from the '" + resource.getDescription() + "', using the ASCII table");
                }
                defaultTable = ASCII_TABLE;
            }
        }
        return defaultTable;
    }

    public String normalize(CharSequence text) {
        return normalize(text, LocaleContextHolder.getLocale());
    }

    public String normalize(CharSequence text, Locale locale) {
        if (!StringUtils.hasLength(text)) {
            return text.toString();
        }
        int length = text.length();
        String[] table = getTable(locale);
        StringBuilder result = new StringBuilder(length);
        for (int i = 0; i < length; i++) {
            int index = text.charAt(i);
            if (index >= table.length) {
                if (log.isTraceEnabled()) {
                    log.trace("The " + index + " is out of the table range");
                }
                continue;
            }
            String mapped = table[index];
            if (mapped == null) {
                if (log.isTraceEnabled()) {
                    log.trace("The '" + (char) index + "' is not mapped");
                }
                continue;
            }
            result.append(mapped);
        }
        return result.toString();
    }

    public String[] getTable(Locale locale) {
        String[] result = tables.get(locale);
        if (result == null) {
            result = loadTable(locale);
            tables.put(locale, result);
        }
        return result;
    }

    private String[] loadTable(Locale locale) {
        List<String> locations = new ArrayList<>(2);
        locations.add(location + locale.getLanguage() + ".table");
        String country = locale.getCountry();
        if (StringUtils.hasLength(country)) {
            locations.add(location + locale.getLanguage() + "_" + country + ".table");
        }
        for (String location : locations) {
            Resource resource = new ClassPathResource(location);
            String[] result = loadTable(resource);
            if (result != null) {
                return result;
            }
        }
        if (log.isInfoEnabled()) {
            log.info("Unable to load a normalization table for the '" + locale + "' locale, using the default table");
        }
        return getDefaultTable();
    }

    private String[] loadTable(Resource resource) {
        if (resource.exists()) {
            TableParser parser = new TableParser(resource);
            try {
                return parser.getTable();
            } catch (FileNotFoundException e) {
                if (log.isWarnEnabled()) {
                    log.warn("The '" + resource.getDescription() + "' normalization table doesn't exist!");
                }
            } catch (IOException e) {
                if (log.isErrorEnabled()) {
                    log.error("Unable to load the '" + resource.getDescription() + "' normalization table!", e);
                }
            }
        }
        return null;
    }

    public static class TableParser extends AbstractResourceParser {
        private final Set<Line> lines = new HashSet<>(1024);
        private Line line;

        public TableParser(Resource resource) {
            super(resource);
        }

        public String[] getTable() throws IOException, TextParsingException {
            parse();
            int size = getTableSize();
            if (size > MAX_TABLE_SIZE) {
                log.warn("The '" + resource + "' has table with the size of " + size + " entries, it has been reduced!");
                size = MAX_TABLE_SIZE;
            }
            String[] result = new String[size];
            for (Line line : lines) {
                int offset = line.offset;
                List<String> values = line.values;
                for (int i = 0, valuesSize = values.size(); i < valuesSize; i++) {
                    String value = values.get(i);
                    int index = offset + i;
                    if (index < result.length) {
                        result[index] = value;
                    } else {
                        if (log.isDebugEnabled()) {
                            log.debug("The '" + value + "' value ignored, it is out of the table range!");
                        }
                    }
                }
            }
            return result;
        }

        protected int getTableSize() {
            int result = 0;
            for (Line line : lines) {
                int size = line.offset + line.values.size();
                if (size > result) {
                    result = size;
                }
            }
            return result;
        }

        @Override
        protected void process() throws IOException {
            if (line == null) {
                // at the beginning of a new line
                token.setLength(0);
                // read offset
                readUntil(' ', LF);
                if (token.length() > 0 && token.charAt(0) == '#') {
                    // it is a comment, ignore it
                    readLine();
                } else {
                    int offset = Integer.parseInt(token.toString(), 16);
                    line = new Line(offset);
                    lines.add(line);
                }
                return;
            }
            nextToken(' ');
            if (line != null) {
                // a token read (not a new line)
                line.capture(token.toString());
            }
        }

        @Override
        protected void onNewLine() {
            if (line != null) {
                line.capture(token.toString());
                token.setLength(0);
            }
            line = null;
        }

        private class Line extends Normalizer.Line<String> {

            private Line(int offset) {
                super(offset);
            }

            void add(String value) {
                values.add(value);
            }
        }
    }

}
