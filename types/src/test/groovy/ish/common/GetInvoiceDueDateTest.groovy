/*
 * Copyright ish group pty ltd 2021.
 *
 * This program is free software: you can redistribute it and/or modify it under the terms of the GNU Affero General Public License version 3 as published by the Free Software Foundation.
 *
 *  This program is distributed in the hope that it will be useful, but WITHOUT ANY WARRANTY; without even the implied warranty of MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE. See the GNU Affero General Public License for more details.
 */
package ish.common

import groovy.transform.CompileStatic
import org.apache.commons.lang3.time.DateUtils
import org.junit.jupiter.api.Assertions
import org.junit.jupiter.api.Test

@CompileStatic
class GetInvoiceDueDateTest {

	@Test
    void testDueDate() {
		GetInvoiceDueDate dueDate =  GetInvoiceDueDate.valueOf(null, null)
        Date date = dueDate.get()

        Assertions.assertEquals(DateUtils.truncate(new Date(), Calendar.HOUR), DateUtils.truncate(date, Calendar.HOUR))

        dueDate =  GetInvoiceDueDate.valueOf(0, 0)
        date = dueDate.get()

        Assertions.assertEquals(DateUtils.truncate(new Date(), Calendar.HOUR), DateUtils.truncate(date, Calendar.HOUR))

        dueDate =  GetInvoiceDueDate.valueOf(1, 0)
        date = dueDate.get()

        Assertions.assertEquals(DateUtils.truncate(new Date(), Calendar.HOUR), DateUtils.truncate(date, Calendar.HOUR))

        dueDate =  GetInvoiceDueDate.valueOf(0, 1)
        date = dueDate.get()

        Assertions.assertEquals(DateUtils.truncate(DateUtils.addDays(new Date(), 1), Calendar.HOUR), DateUtils.truncate(date, Calendar.HOUR))

        dueDate =  GetInvoiceDueDate.valueOf(5, null)
        date = dueDate.get()

        Assertions.assertEquals(DateUtils.truncate(DateUtils.addDays(new Date(), 5), Calendar.HOUR), DateUtils.truncate(date, Calendar.HOUR))

        dueDate =  GetInvoiceDueDate.valueOf(null, 5)
        date = dueDate.get()

        Assertions.assertEquals(DateUtils.truncate(DateUtils.addDays(new Date(), 5), Calendar.HOUR), DateUtils.truncate(date, Calendar.HOUR))


    }
}
