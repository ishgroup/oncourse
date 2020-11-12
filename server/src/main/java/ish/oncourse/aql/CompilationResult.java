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

import org.apache.cayenne.exp.Expression;

import java.util.Collections;
import java.util.List;
import java.util.Optional;

/**
 * Class that holds AQL query compilation result.
 * <p>
 * Usage:<pre>{@code
 *
 * CompilationResult result = sqlService.compile(...);
 * Expression expr = result.getCayenneExpression()
 *      .orElseThrow(() -> reportErrors(result.getErrors()));
 *
 * }</pre>
 *

 */
public class CompilationResult {

    private final Expression expression;
    private final List<CompilationError> errors;

    public CompilationResult(Expression expression) {
        this.expression = expression;
        this.errors = Collections.emptyList();
    }

    public CompilationResult(List<CompilationError> errors) {
        this.expression = null;
        this.errors = errors;
    }

    /**
     * This method will return empty list if compilation succeeded.
     *
     * @return list of error messages
     */
    public List<CompilationError> getErrors() {
        return errors;
    }

    /**
     * @return optional of Cayenne Expression or empty if query is invalid
     */
    public Optional<Expression> getCayenneExpression() {
        return expression == null
                ? Optional.empty()
                : Optional.of(expression);
    }
}
