/*
 * Copyright ish group pty ltd. All rights reserved. http://www.ish.com.au No copying or use of this code is allowed without permission in writing from ish.
 */
package ish.oncourse.utils;

import org.apache.commons.lang3.time.DateUtils;
import org.junit.Assert;
import org.junit.Test;

import java.util.Calendar;
import java.util.Date;

/**
 * User: akoiro
 * Date: 1/12/17
 */
public class TimestampUtilitiesTest {

	@Test

	public void test_normalisedDate() {
		Date timestamp = new Date();
		Date date = TimestampUtilities.normalisedDate(timestamp);
		Assert.assertEquals(date, DateUtils.truncate(timestamp, Calendar.DAY_OF_MONTH));
		Assert.assertEquals(0, DateUtils.getFragmentInHours(date, Calendar.HOUR_OF_DAY));
		Assert.assertEquals(0, DateUtils.getFragmentInHours(date, Calendar.MINUTE));
		Assert.assertEquals(0, DateUtils.getFragmentInHours(date, Calendar.SECOND));
		Assert.assertEquals(0, DateUtils.getFragmentInHours(date, Calendar.MILLISECOND));
	}
}
