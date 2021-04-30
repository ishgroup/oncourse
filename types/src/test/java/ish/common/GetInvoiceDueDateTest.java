/*
 * Copyright ish group pty ltd. All rights reserved. http://www.ish.com.au No copying or use of this code is allowed without permission in writing from ish.
 */
package ish.common;

import org.apache.commons.lang3.time.DateUtils;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.Assertions;

import java.util.Calendar;
import java.util.Date;

import static org.junit.Assert.assertEquals;

public class GetInvoiceDueDateTest {

	@Test
	public void testDueDate() {
		GetInvoiceDueDate dueDate =  GetInvoiceDueDate.valueOf(null, null);
		Date date = dueDate.get();

		Assertions.assertEquals(DateUtils.truncate(new Date(), Calendar.HOUR), DateUtils.truncate(date, Calendar.HOUR));

		dueDate =  GetInvoiceDueDate.valueOf(0, 0);
		date = dueDate.get();

		Assertions.assertEquals(DateUtils.truncate(new Date(), Calendar.HOUR), DateUtils.truncate(date, Calendar.HOUR));

		dueDate =  GetInvoiceDueDate.valueOf(1, 0);
		date = dueDate.get();

		Assertions.assertEquals(DateUtils.truncate(new Date(), Calendar.HOUR), DateUtils.truncate(date, Calendar.HOUR));

		dueDate =  GetInvoiceDueDate.valueOf(0, 1);
		date = dueDate.get();

		Assertions.assertEquals(DateUtils.truncate(DateUtils.addDays(new Date(), 1), Calendar.HOUR), DateUtils.truncate(date, Calendar.HOUR));

		dueDate =  GetInvoiceDueDate.valueOf(5, null);
		date = dueDate.get();

		Assertions.assertEquals(DateUtils.truncate(DateUtils.addDays(new Date(), 5), Calendar.HOUR), DateUtils.truncate(date, Calendar.HOUR));

		dueDate =  GetInvoiceDueDate.valueOf(null, 5);
		date = dueDate.get();

		Assertions.assertEquals(DateUtils.truncate(DateUtils.addDays(new Date(), 5), Calendar.HOUR), DateUtils.truncate(date, Calendar.HOUR));





	}
}
