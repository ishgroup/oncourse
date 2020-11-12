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

package ish.oncourse.aql.impl.converter;

import ish.oncourse.aql.impl.Op;
import org.apache.cayenne.exp.parser.SimpleNode;

import java.util.function.Function;

public class EntityOpConverter implements Function<Op, SimpleNode> {

    private Function<Op, LazyEntityComparisonNode> function;

    private EntityOpConverter() {

    }

    @Override
    public SimpleNode apply(Op op) {
        switch (op) {
            case EQ:
            case NE:
            case LIKE:
            case CONTAINS:
            case NOT_LIKE:
            case NOT_CONTAINS:
                return function.apply(op);
        }
        return null;
    }

    public static EntityOpConverter valueOf(Function<Op, LazyEntityComparisonNode> function) {
        var entityOpConverter = new EntityOpConverter();
        entityOpConverter.function = function;
        return entityOpConverter;
    }
}
