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
package ish.oncourse.cayenne

interface MappedSelectParams {


    String ENTITY_COUNT_QUERY = "EntityCount"
    String MAX_QUERY = "MaxQuery"
    String SUM_QUERY = "SumQuery"

    String FIELD_NAME = "fieldName"
    String ENTITY_NAME_PARAMETER = "entityName"
    String WHERE_CLAUSE_PARAMETER = "whereClause"

    String COUNT_COLUMN = "C"
    String AMOUNT_SUM_COLUMN = "AMOUNT_SUM"

}
