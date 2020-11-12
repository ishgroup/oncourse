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

package ish.oncourse.server.querying

import org.apache.cayenne.ObjectContext
import org.apache.cayenne.query.SortOrder

class QuerySpec {

    String entity
    String query
    SortOrder sort
    Boolean first = Boolean.FALSE
    ObjectContext context


    void entity(String entity) {
        this.entity = entity
    }

    void query(String query) {
        this.query = query
    }

    void sort(String order) {
        if (order.equalsIgnoreCase("desc")) {
            this.sort = SortOrder.DESCENDING
        } else {
            this.sort = SortOrder.ASCENDING
        }
    }

    void first(Object first) {
        if (first instanceof String) {
            this.first = Boolean.valueOf(first)
        } else if (first instanceof Boolean) {
            this.first = first
        } else {
            this.first = Boolean.FALSE
        }
    }

    void context(ObjectContext context) {
        this.context = context
    }
}


