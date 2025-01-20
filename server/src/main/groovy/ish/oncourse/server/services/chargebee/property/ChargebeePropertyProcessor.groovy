/*
 * Copyright ish group pty ltd 2024.
 *
 * This program is free software: you can redistribute it and/or modify it under the terms of the GNU Affero General Public License version 3 as published by the Free Software Foundation.
 *
 *  This program is distributed in the hope that it will be useful, but WITHOUT ANY WARRANTY; without even the implied warranty of MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE. See the GNU Affero General Public License for more details.
 */

package ish.oncourse.server.services.chargebee.property

import ish.common.chargebee.ChargebeePropertyType

import javax.sql.DataSource
import java.text.SimpleDateFormat

abstract class ChargebeePropertyProcessor {
    private static final SimpleDateFormat SQL_DATE_FORMAT = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss")

    private Date startDate
    private Date endDate

    ChargebeePropertyProcessor(Date startDate, Date endDate) {
        this.startDate = startDate
        this.endDate = endDate
    }


    protected Date getStartDate(){
        return startDate
    }

    protected Date getEndDate(){
        return endDate
    }

    protected String getFormattedStartDate(){
        return SQL_DATE_FORMAT.format(startDate)
    }

    protected String getFormattedEndDate() {
        return SQL_DATE_FORMAT.format(endDate)
    }

    abstract BigDecimal getValue()
    abstract ChargebeePropertyType getType()
}
