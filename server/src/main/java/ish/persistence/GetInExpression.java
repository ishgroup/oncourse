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

package ish.persistence;

import org.apache.cayenne.exp.Expression;
import org.apache.cayenne.exp.Property;

import java.util.List;

/**
   Workaround to support large IN expressions.
 */
@Deprecated
public class GetInExpression {

    // batch size for queries due to jtds limitation in 2000 elements in IN clause
    private static final int BATCH_SIZE = 2000;

    private static final String ID = "id";

    private Property<Long> property;
    private List<Long> relatedRecordsIds;

    private GetInExpression() {

    }

    public static GetInExpression valueOf(List<Long> relatedRecordsIds) {
        return valueOf(Property.create(ID, Long.class), relatedRecordsIds);
    }

    public static GetInExpression valueOf(Property<Long> property, List<Long> relatedRecordsIds) {
        GetInExpression getInExpression = new GetInExpression();
        getInExpression.property = property;
        getInExpression.relatedRecordsIds = relatedRecordsIds;
        return getInExpression;
    }


    public Expression get() {
        Expression exp = property.in(relatedRecordsIds.subList(0, Math.min(BATCH_SIZE, relatedRecordsIds.size())));

        for (int offset = BATCH_SIZE; offset < relatedRecordsIds.size(); offset += BATCH_SIZE) {
            exp = exp.orExp(property.in(relatedRecordsIds.subList(offset, Math.min(offset + BATCH_SIZE, relatedRecordsIds.size()))));
        }

        return exp;
    }

}
