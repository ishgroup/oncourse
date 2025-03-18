/*
 * Copyright ish group pty ltd 2024.
 *
 * This program is free software: you can redistribute it and/or modify it under the terms of the GNU Affero General Public License version 3 as published by the Free Software Foundation.
 *
 *  This program is distributed in the hope that it will be useful, but WITHOUT ANY WARRANTY; without even the implied warranty of MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE. See the GNU Affero General Public License for more details.
 */

package ish.oncourse.server.services.chargebee.property

import ish.oncourse.server.util.DbConnectionUtils

import javax.sql.DataSource

abstract class ChargebeeSimplePropertyProcessor extends ChargebeePropertyProcessor {
    protected DataSource dataSource

    ChargebeeSimplePropertyProcessor(Date startDate, Date endDate, DataSource dataSource) {
        super(startDate, endDate)
        this.dataSource = dataSource
    }

    @Override
    BigDecimal getValue() {
        return DbConnectionUtils.getBigDecimalForDbQuery(getQuery(), dataSource)
    }

    abstract String getQuery()
}
