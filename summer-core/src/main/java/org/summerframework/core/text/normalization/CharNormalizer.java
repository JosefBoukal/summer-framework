package org.summerframework.core.text.normalization;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.context.i18n.LocaleContextHolder;
import org.springframework.core.io.ClassPathResource;
import org.springframework.core.io.Resource;
import org.summerframework.core.text.TextParsingException;
import org.summerframework.core.text.parsing.AbstractResourceParser;
import org.summerframework.util.StringUtils;

import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Locale;
import java.util.Map;

/**
 * A char normalizer is an utility class that is capable of converting characters to other characters using the fast
 * array of char tables.
 */
public class CharNormalizer implements TextNormalizer {
    private static final Logger log = LoggerFactory.getLogger(CharNormalizer.class);
    private static final char[] ASCII_TABLE = new char[127];

    static {
        // from 32 to 125 inclusive
        for (int i = 0x20; i < ASCII_TABLE.length - 1; i++) {
            ASCII_TABLE[i] = (char) i;
        }
    }

    private final String location;
    private final Map<Locale, char[]> tables = new HashMap<>();
    private char[] defaultTable;

    // the default (ASCII) normalizer
    public CharNormalizer() {
        this.location = "/META-INF/normalize/";
    }

    CharNormalizer(String location) {
        // normalize location
        StringUtils.cleanPath(location);
        if (!location.endsWith("/")) {
            location += "/";
        }
        this.location = location;
    }

    private char[] getDefaultTable() {
        if (defaultTable == null) {
            // one shot table load
            Resource resource = new ClassPathResource(location + "default.table");
            defaultTable = loadTable(resource);
            if (defaultTable == null) {
                log.warn("Unable to load the default table from the '" + resource.getDescription() + "', using the ASCII table");
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
        char[] table = getTable(locale);
        StringBuilder result = new StringBuilder(length);
        for (int i = 0; i < length; i++) {
            int index = text.charAt(i);
            if (index >= table.length) {
                if (log.isDebugEnabled()) {
                    log.debug("The " + index + " is out of the table range");
                }
                continue;
            }
            char mapped = table[index];
            if (mapped == 0) {
                if (log.isTraceEnabled()) {
                    log.trace("The '" + (char) index + "' is not mapped");
                }
                continue;
            }
            result.append(mapped);
        }
        return result.toString();
    }

    private char[] getTable(Locale locale) {
        char[] result = tables.get(locale);
        if (result == null) {
            result = loadTable(locale);
            tables.put(locale, result);
        }
        return result;
    }

    private char[] loadTable(Locale locale) {
        List<String> locations = new ArrayList<>(2);
        locations.add(location + locale.getLanguage() + ".table");
        if (locale.getCountry() != null) {
            locations.add(location + locale.getLanguage() + "_" + locale.getCountry() + ".table");
        }
        for (String location : locations) {
            Resource resource = new ClassPathResource(location);
            char[] result = loadTable(resource);
            if (result != null) {
                return result;
            }
        }
        if (log.isDebugEnabled()) {
            log.debug("Unable to load a normalization table for the '" + locale + "' locale, using the default table");
        }
        return getDefaultTable();
    }

    private char[] loadTable(Resource resource) {
        if (resource.exists()) {
            TableParser parser = new TableParser(resource);
            try {
                return parser.getTable();
            } catch (IOException e) {
                log.error("Unable to load the '" + resource.getDescription() + "' normalization table!");
            }
        }
        return null;
    }

    public static class TableParser extends AbstractResourceParser {
        private final List<Line> lines = new ArrayList<>(256);
        private Line line;

        TableParser(Resource resource) {
            super(resource);
        }

        char[] getTable() throws IOException, TextParsingException {
            parse();
            int size = getTableSize();
            if (size > MAX_TABLE_SIZE) {
                log.warn("The " + resource + " has table with the size of " + size + " entries, it has been reduced!");
                size = MAX_TABLE_SIZE;
            }
            char[] result = new char[size];
            for (Line line : lines) {
                int offset = line.offset;
                List<Character> values = line.values;
                for (int i = 0, valuesSize = values.size(); i < valuesSize; i++) {
                    Character value = values.get(i);
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

        int getTableSize() {
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
                    token.delete(0, 1);
                    // it is a comment, ignore it
                    readLine();
                    String comment = token.toString().trim();
                    if (comment.startsWith("space")) {
                        processSpaceComment(comment);
                    }
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

        void processSpaceComment(String comment) {
            int equal = comment.indexOf('=');
            if (equal < 0) {
                log.info("Unrecognized space comment '" + comment + "'");
                return;
            }
            String value = comment.substring(equal + 1);
            String[] tokens = value.split(" ");
            for (String token : tokens) {
                token = token.trim();
                if (StringUtils.hasLength(token)) {
                    try {
                        int offset = Integer.valueOf(token, 16);
                        Line line = new Line(offset);
                        line.add(" ");
                        lines.add(line);
                    } catch (NumberFormatException e) {
                        log.warn("Invalid space comment! " + e.getLocalizedMessage());
                    }
                }
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

        private class Line extends TextNormalizer.Line<Character> {

            private Line(int offset) {
                super(offset);
            }

            void add(String value) {
                values.add(value.charAt(0));
            }
        }
    }
}
