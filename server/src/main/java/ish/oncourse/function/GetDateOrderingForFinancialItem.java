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

package ish.oncourse.function;

import ish.oncourse.cayenne.FinancialItem;
import org.apache.cayenne.query.Ordering;
import org.apache.cayenne.query.SortOrder;

import java.util.Arrays;
import java.util.List;

/**
 * Created by anarut on 11/10/16.
 */
public class GetDateOrderingForFinancialItem {

    private boolean isAscending;

    private GetDateOrderingForFinancialItem() {
    }

    public static GetDateOrderingForFinancialItem valueOf() {
        return valueOf(true);
    }

    public static GetDateOrderingForFinancialItem valueOf(Boolean isAscending) {
        GetDateOrderingForFinancialItem function = new GetDateOrderingForFinancialItem();
        function.isAscending = isAscending;
        return function;
    }

    public List<Ordering> get() {
        return Arrays.asList(
                new Ordering(FinancialItem.DATE, isAscending ? SortOrder.ASCENDING : SortOrder.DESCENDING),
                new Ordering(FinancialItem.CREATED_ON, isAscending ? SortOrder.ASCENDING : SortOrder.DESCENDING));
    }
}
