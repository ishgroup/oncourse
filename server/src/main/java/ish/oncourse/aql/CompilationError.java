/*
 * Copyright ish group pty ltd 2020.
 *
 * This program is free software: you can redistribute it and/or modify it under the terms of the
 * GNU Affero General Public License version 3 as published by the Free Software Foundation.
 *
 * This program is distributed in the hope that it will be useful, but WITHOUT ANY WARRANTY;
 * without even the implied warranty of MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.
 * See the GNU Affero General Public License for more details.
 */

package ish.oncourse.aql;

/**
 * Simple value object that represents query compilation error.
 *

 */
public class CompilationError {

    private final int line;
    private final int charPositionInLine;
    private final String message;

    public CompilationError(int line, int charPositionInLine, String message) {
        this.line = line;
        this.charPositionInLine = charPositionInLine;
        this.message = message;
    }

    public int getLine() {
        return line;
    }

    public int getCharPositionInLine() {
        return charPositionInLine;
    }

    public String getMessage() {
        return message;
    }
}
