package org.summerframework.core.text;

import org.springframework.core.io.Resource;

/**
 * Provides information on a token location in a text resource.
 */
public class Location {
    private final Resource resource;
    private final int line;
    private final int column;
    private final int characterOffset;

    public Location(Resource resource, int line, int column, int characterOffset) {
        this.resource = resource;
        this.line = line;
        this.column = column;
        this.characterOffset = characterOffset;
    }

    public Resource getResource() {
        return resource;
    }

    /**
     * Returns the line number where the current node starts, it may return -1 if not known.
     */
    public int getLine() {
        return line;
    }

    /**
     * Returns the column number where the current node starts, it may return -1 if not known.
     */
    public int getColumn() {
        return column;
    }

    /**
     * Returns the byte or character offset into the input source this location is pointing to. If the resource's input
     * source is a file or a byte stream then this is the byte offset into that stream, but if the input source is a
     * character media then the offset is the character offset. Returns -1 if it is not known.
     */
    public int getCharacterOffset() {
        return characterOffset;
    }

    @Override
    public String toString() {
        return resource.getDescription() + " on line " + line + " near column " + column;
    }
}
