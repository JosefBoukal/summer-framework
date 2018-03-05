package org.summerframework.core.text.parsing;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.core.io.Resource;
import org.summerframework.core.text.TextParsingException;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.Reader;
import java.util.Locale;

/**
 * Abstract text resource parser base class.
 */
public abstract class AbstractResourceParser {
    public static final char LF = '\n';
    // CR should be private and not exposed - CR is used only locally
    private static final char CR = '\r';
    protected final Logger log = LoggerFactory.getLogger(this.getClass());

    public static final int EOF = -1;

    protected final Resource resource;
    protected final String encoding;
    protected Reader reader;
    protected int line = 1;
    protected int column = 0;

    protected StringBuilder token = new StringBuilder(64);
    protected int ch = EOF;
    protected int previous = EOF;
    // ahead should be private and not exposed to inheritors
    private int ahead = EOF;

    public AbstractResourceParser(Resource resource) {
        this(resource, null);
    }

    public AbstractResourceParser(Resource resource, String encoding) {
        this.resource = resource;
        if (encoding == null) {
            // fallback default value
            encoding = "UTF-8";
        }
        this.encoding = encoding;
    }

    protected abstract void process() throws IOException;

    protected void parse() throws IOException, TextParsingException {
        log.debug("Processing the {} with the '{}' encoding", resource, encoding);
        this.reader = new BufferedReader(new InputStreamReader(resource.getInputStream(), encoding), 1024 * 4);
        try {
            for (; ; ) {
                readChar();
                if (ch == EOF) {
                    break;
                }
                process();
                //noinspection ConstantConditions
                if (ch == EOF) {
                    // EOF after process
                    break;
                }
            }
        } finally {
            reader.close();
        }
    }

    protected void ignoreUntil(char... stopChars) throws IOException, TextParsingException {
        for (; ; ) {
            readChar();
            checkEOF();
            for (char stopChar : stopChars) {
                if (stopChar == ch) {
                    // found the stop char, stop
                    return;
                }
            }
            if (ch == ' ' || ch == '\t' || ch == LF) {
                // ignore whitespace
                continue;
            }
            // not a white space, throw unexpected char error
            StringBuilder expecting = new StringBuilder(16);
            if (stopChars.length == 1) {
                expecting.append('\'').append(stopChars[0]).append('\'');
            } else {
                for (char stopChar : stopChars) {
                    if (expecting.length() > 0) {
                        expecting.append(" or '");
                    } else {
                        expecting.append('\'');
                    }
                    expecting.append(stopChar).append('\'');
                }
            }
            String message = String.format(Locale.ROOT,
                    "Expecting %5$s at line %2$d near column %3$d, but the '%4$s' char is given in the '%1$s'!",
                    resource, line, column, ch, expecting
            );
            throw new TextParsingException(message);
        }
    }

    protected void readLine() throws IOException, TextParsingException {
        while (ch != EOF) {
            if (ch == LF) {
                break;
            }
            token.append((char) ch);
            readChar();
        }
    }

    protected void readUntil(char stopChar) throws IOException, TextParsingException {
        while (ch != stopChar) {
            token.append((char) ch);
            readChar();
            checkEOF();
        }
    }

    protected void readUntil(char... stopChars) throws IOException, TextParsingException {
        read_token:
        for (; ; ) {
            for (char stopChar : stopChars) {
                if (stopChar == ch) {
                    break read_token;
                }
            }
            token.append((char) ch);
            readChar();
            checkEOF();
        }
    }

    protected void nextToken(char stopChar) throws IOException, TextParsingException {
        token.setLength(0);
        while (ch != stopChar) {
            token.append((char) ch);
            readChar();
            if (ch == LF || ch == EOF) {
                break;
            }
        }
    }

    protected void readChar() throws IOException {
        previous = ch;
        if (ahead != EOF) {
            ch = ahead;
            ahead = EOF;
            return;
        }
        ch = reader.read();
        // LF - Line Feed, '\n' - 10, CR - Carriage Return, '\r' - 13
        // CR+LF: Microsoft Windows, Symbian OS, Palm OS, etc.
        // LF   : Unix and Unix-like systems
        // CR   : Mac OS up to version 9
        if (ch == CR || ch == LF) {
            // this is a new line, read ahead
            ahead = reader.read();
            if (ahead == CR || ahead == LF) {
                // two chars new line, ignore the char just read and read the next char ahead
                ahead = reader.read();
            }
            // this is a new line
            ch = LF;
            line++;
            column = 0;
            onNewLine();
        } else {
            column++;
        }
    }

    protected void checkEOF() throws TextParsingException {
        if (ch == EOF) {
            String message = String.format(Locale.ROOT,
                    "An unexpected end of file reached at line %2$d, column %3$d of the %1$s!",
                    resource.getDescription(), line, column
            );
            throw new TextParsingException(message);
        }
    }

    /**
     * Callback method called after a new line is detected.
     */
    protected void onNewLine() {
    }

    public int getLine() {
        return line;
    }

    public int getColumn() {
        return column;
    }

}
