package ish.oncourse.services.courseclass;

import ish.oncourse.model.CourseClass;
import org.apache.commons.lang3.time.DateUtils;
import org.junit.Test;

import java.util.Date;

import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

/*
 * Copyright ish group pty ltd. All rights reserved. http://www.ish.com.au No copying or use of this code is allowed without permission in writing from ish.
 */
public class CheckClassAgeTest {

    @Test
    public void testAfterClassStarts() {
        CourseClass courseClass = mock(CourseClass.class);

        when(courseClass.getStartDate()).thenReturn(DateUtils.addDays(new Date(), -1));
        assertTrue(new CheckClassAge().courseClass(courseClass).classAge(ClassAge.valueOf(2, ClassAgeType.afterClassStarts)).check());
        when(courseClass.getStartDate()).thenReturn(DateUtils.addDays(new Date(), -3));
        assertFalse(new CheckClassAge().courseClass(courseClass).classAge(ClassAge.valueOf(2, ClassAgeType.afterClassStarts)).check());

    }

    @Test
    public void testAfterClassEnds() {
        CourseClass courseClass = mock(CourseClass.class);

        when(courseClass.getEndDate()).thenReturn(DateUtils.addDays(new Date(), -1));
        assertTrue(new CheckClassAge().courseClass(courseClass).classAge(ClassAge.valueOf(2, ClassAgeType.afterClassEnds)).check());
        when(courseClass.getEndDate()).thenReturn(DateUtils.addDays(new Date(), -3));
        assertFalse(new CheckClassAge().courseClass(courseClass).classAge(ClassAge.valueOf(2, ClassAgeType.afterClassEnds)).check());

    }

    @Test
    public void testBeforeClassStarts() {
        CourseClass courseClass = mock(CourseClass.class);

        when(courseClass.getStartDate()).thenReturn(DateUtils.addDays(new Date(), 3));
        assertTrue(new CheckClassAge().courseClass(courseClass).classAge(ClassAge.valueOf(2, ClassAgeType.beforeClassStarts)).check());
        when(courseClass.getStartDate()).thenReturn(DateUtils.addDays(new Date(), 1));
        assertFalse(new CheckClassAge().courseClass(courseClass).classAge(ClassAge.valueOf(2, ClassAgeType.beforeClassStarts)).check());

    }


    @Test
    public void testBeforeClassEnds() {
        CourseClass courseClass = mock(CourseClass.class);

        when(courseClass.getEndDate()).thenReturn(DateUtils.addDays(new Date(), 3));
        assertTrue(new CheckClassAge().courseClass(courseClass).classAge(ClassAge.valueOf(2, ClassAgeType.beforeClassEnds)).check());
        when(courseClass.getEndDate()).thenReturn(DateUtils.addDays(new Date(), 1));
        assertFalse(new CheckClassAge().courseClass(courseClass).classAge(ClassAge.valueOf(2, ClassAgeType.beforeClassEnds)).check());

    }

}
