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

package ish.oncourse.aql.impl;

import org.antlr.v4.runtime.BaseErrorListener;
import org.antlr.v4.runtime.Lexer;
import org.antlr.v4.runtime.RecognitionException;
import org.antlr.v4.runtime.Recognizer;

import java.util.Objects;

/**
 * Internal error listener that will accumulate all parsing errors.
 * Lexer errors are accumulated apart, see {@link ish.oncourse.aql.CompilationResult#getLexerErrors()}.
 */
class AqlErrorListener extends BaseErrorListener {

    private final CompilationContext context;

    AqlErrorListener(CompilationContext context) {
        this.context = Objects.requireNonNull(context);
    }

    @Override
    public void syntaxError(Recognizer<?, ?> recognizer, Object offendingSymbol,
                            int line, int charPositionInLine,
                            String msg, RecognitionException e) {
        if (recognizer instanceof Lexer) {
            context.reportLexerError(line, charPositionInLine, msg);
        } else {
            context.reportError(line, charPositionInLine, msg);
        }
    }
}
