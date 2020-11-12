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

/**
 * Helper enum to process math operators
 *

 */
public enum MathOp {
    PLUS,
    MINUS,
    MUL,
    DIV,
    MOD;

    public static MathOp from(AqlParser.MathOperatorContext op) {
        if (op.DIV() != null) {
            return DIV;
        } else if (op.MINUS() != null) {
            return MINUS;
        } else if (op.PLUS() != null) {
            return PLUS;
        } else if (op.MUL() != null) {
            return MUL;
        } else if (op.MOD() != null) {
            return MOD;
        }

        throw new IllegalArgumentException("Unknown math operation " + op.getText());
    }
}
