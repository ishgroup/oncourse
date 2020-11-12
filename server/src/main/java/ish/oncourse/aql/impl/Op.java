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

 */
public enum Op {
    LE,
    GE,
    NE,
    LT,
    GT,
    EQ,
    LIKE,
    CONTAINS,
    STARTS_WITH,
    ENDS_WITH,
    NOT_LIKE,
    NOT_CONTAINS,
    NOT_ENDS_WITH,
    NOT_STARTS_WITH,
    AFTER,
    BEFORE;

    public static Op from(AqlParser.OperatorContext op) {
        if (op.LE() != null) {
            return LE;
        } else if (op.GE() != null) {
            return GE;
        } else if (op.NE() != null) {
            return NE;
        } else if (op.LT() != null) {
            return LT;
        } else if (op.GT() != null) {
            return GT;
        } else if (op.EQ() != null) {
            return EQ;
        } else if (op.LIKE() != null) {
            return LIKE;
        } else if (op.NOT_LIKE() != null) {
            return NOT_LIKE;
        } else if (op.CONTAINS() != null) {
            return CONTAINS;
        } else if (op.NOT_CONTAINS() != null) {
            return NOT_CONTAINS;
        } else if (op.STARTS_WITH() != null) {
            return STARTS_WITH;
        } else if (op.NOT_STARTS_WITH() != null) {
            return NOT_STARTS_WITH;
        } else if (op.ENDS_WITH() != null) {
            return ENDS_WITH;
        } else if (op.NOT_ENDS_WITH() != null) {
            return NOT_ENDS_WITH;
        } else if (op.AFTER() != null) {
            return AFTER;
        } else if (op.BEFORE() != null) {
            return BEFORE;
        }

        return null;
    }
}
