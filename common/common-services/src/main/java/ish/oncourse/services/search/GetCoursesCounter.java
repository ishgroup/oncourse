package ish.oncourse.services.search;

import ish.oncourse.model.Tag;

import java.util.List;
import java.util.Map;

/*
 * Copyright ish group pty ltd. All rights reserved. http://www.ish.com.au No copying or use of this code is allowed without permission in writing from ish.
 */
public class GetCoursesCounter {
    private Tag tag;
    private Map<Long, Long> amounts;
    private long result = 0;

    public long get() {
        if (amounts != null) {
            getCoursesAmount(tag);
        }
        return result;
    }

    private void getCoursesAmount(Tag tag) {
        Long value = amounts.get(tag.getId());
        result += value == null ? 0 : value;
        List<Tag> tags = tag.getTags();
        for (Tag child : tags) {
            getCoursesAmount(child);
        }
    }

    public static GetCoursesCounter valueOf(Tag tag, Map<Long, Long> amounts) {
        GetCoursesCounter result = new GetCoursesCounter();
        result.tag = tag;
        result.amounts = amounts;
        return result;
    }
}
