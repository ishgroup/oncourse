package ish.oncourse.services.search;

import ish.oncourse.solr.query.DayOption;
import ish.oncourse.utils.TimestampUtilities;
import org.apache.commons.collections.CollectionUtils;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import java.util.ArrayList;
import java.util.List;
import java.util.Set;

/*
 * Copyright ish group pty ltd. All rights reserved. http://www.ish.com.au No copying or use of this code is allowed without permission in writing from ish.
 */
public class FocusMatchForDays {
    private static final Logger logger = LogManager.getLogger();

    private List<String> uniqueDays = new ArrayList<>();
    private DayOption searchDay;

    public float match() {
        boolean match = false;
        switch (searchDay) {
            case weekday:
                match = CollectionUtils.containsAny(TimestampUtilities.DaysOfWorkingWeekNames, uniqueDays);
                break;
            case weekend:
                match = CollectionUtils.containsAny(TimestampUtilities.DaysOfWeekendNames, uniqueDays);
                break;
            case mon:
            case tues:
            case wed:
            case thurs:
            case fri:
            case sat:
            case sun:
                match = uniqueDays.contains(searchDay.getFullName());
                break;
            default:
                logger.debug(new IllegalArgumentException());
                break;
        }

        return match ? 1.0f : 0f;
    }

    public static FocusMatchForDays valueOf(Set<String> daysOfWeek, DayOption searchDay) {
        FocusMatchForDays result = new FocusMatchForDays();
        result.uniqueDays.addAll(daysOfWeek);
        result.searchDay = searchDay;
        return result;
    }
}
