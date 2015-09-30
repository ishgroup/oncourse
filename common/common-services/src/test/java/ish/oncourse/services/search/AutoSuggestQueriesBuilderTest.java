package ish.oncourse.services.search;

import ish.oncourse.model.College;
import org.apache.cayenne.ObjectId;
import org.junit.Test;

import static org.junit.Assert.*;

/*
 * Copyright ish group pty ltd. All rights reserved. http://www.ish.com.au No copying or use of this code is allowed without permission in writing from ish.
 */
public class AutoSuggestQueriesBuilderTest {

    @Test
    public void test() {
        College college = new College();
        college.setObjectId(new ObjectId("College", "id", 1L));

        AutoSuggestQueriesBuilder builder = AutoSuggestQueriesBuilder.valueOf("lake", college).build();
        assertNotNull(builder.getCoursesQuery());
        assertEquals("(name:lake* AND collegeId:1) || (course_code:lake* AND collegeId:1)", builder.getCoursesQuery().getQuery());
        assertNotNull(builder.getTagsQuery());
        assertEquals("(collegeId:1 AND name:lake*)", builder.getTagsQuery().getQuery());
        assertNotNull(builder.getSuburbsQuery());
        assertEquals("((suburb:lake*) || (postcode:lake*))", builder.getSuburbsQuery().getQuery());


        builder = AutoSuggestQueriesBuilder.valueOf("lake well", college).build();
        assertNotNull(builder.getCoursesQuery());
        assertEquals("(name:lake* AND collegeId:1) || (course_code:lake* AND collegeId:1) || (name:well* AND collegeId:1) || (course_code:well* AND collegeId:1)", builder.getCoursesQuery().getQuery());
        assertNotNull(builder.getTagsQuery());
        assertEquals("(collegeId:1 AND name:lake*) || (collegeId:1 AND name:well*)", builder.getTagsQuery().getQuery());
        assertNotNull(builder.getSuburbsQuery());
        assertEquals("((suburb:lake AND suburb:well*) || (postcode:lake AND postcode:well*))", builder.getSuburbsQuery().getQuery());

        builder = AutoSuggestQueriesBuilder.valueOf(" \n \t", college).build();
        assertNull(builder.getCoursesQuery());
        assertNull(builder.getTagsQuery());
        assertNull(builder.getSuburbsQuery());
    }
}
