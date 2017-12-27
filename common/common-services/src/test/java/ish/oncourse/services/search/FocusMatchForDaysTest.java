package ish.oncourse.services.search;

import ish.oncourse.solr.query.DayOption;
import ish.oncourse.utils.TimestampUtilities;
import org.apache.commons.lang3.time.DateUtils;
import org.junit.Test;

import java.util.*;

import static org.junit.Assert.assertEquals;

/*
 * Copyright ish group pty ltd. All rights reserved. http://www.ish.com.au No copying or use of this code is allowed without permission in writing from ish.
 */
public class FocusMatchForDaysTest {
    @Test
    public void test() {

        Calendar date = Calendar.getInstance();
        date.set(Calendar.DAY_OF_WEEK, 2);

        Date mon = date.getTime(),
                tues = DateUtils.addDays(mon, 1),
                wed = DateUtils.addDays(mon, 2),
                thurs = DateUtils.addDays(mon, 3),
                fri = DateUtils.addDays(mon, 4),
                sat = DateUtils.addDays(mon, 5),
                sun = DateUtils.addDays(mon, 6);

        assertDay(mon, new DayOption[]{DayOption.mon, DayOption.weekday});
        assertDay(tues, new DayOption[]{DayOption.tues, DayOption.weekday});
        assertDay(wed, new DayOption[]{DayOption.wed, DayOption.weekday});
        assertDay(thurs, new DayOption[]{DayOption.thurs, DayOption.weekday});
        assertDay(fri, new DayOption[]{DayOption.fri, DayOption.weekday});
        assertDay(sat, new DayOption[]{DayOption.sat, DayOption.weekend});
        assertDay(sun, new DayOption[]{DayOption.sun, DayOption.weekend});
    }

    private void assertDay(Date testDate, DayOption[] matched) {
        HashSet<String> days = new HashSet<>();
        days.add(TimestampUtilities.dayOfWeek(testDate, true, TimeZone.getDefault()));

        for (DayOption dayOption : DayOption.values()) {
            assertEquals(Arrays.asList(matched).contains(dayOption) ? 1f: 0f, FocusMatchForDays.valueOf(days, dayOption).match(), 0);
        }
    }
}
