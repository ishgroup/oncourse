package ish.oncourse.portal.services;

import ish.oncourse.model.CourseClass;
import ish.oncourse.model.Session;
import org.apache.commons.lang3.time.DateUtils;
import org.junit.Assert;
import org.junit.Test;

import java.util.Collections;
import java.util.Date;

import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

public class IsCurrentTutorClassTest {

    @Test
    public void testDistantLearningCourse() {
        CourseClass courseClass = mock(CourseClass.class);
        when(courseClass.getIsDistantLearningCourse()).thenReturn(Boolean.TRUE);
        Assert.assertTrue(PortalService.IsCurrentTutorClass.valueOf(courseClass, new Date()).is());
    }

    @Test
    public void testHasSessionsAndDateAfterToday() {
        CourseClass courseClass = mock(CourseClass.class);
        when(courseClass.getIsDistantLearningCourse()).thenReturn(Boolean.FALSE);
        when(courseClass.getSessions()).thenReturn(Collections.singletonList(new Session()));
        when(courseClass.getEndDate()).thenReturn(DateUtils.addDays(new Date(), 1));
        Assert.assertTrue(PortalService.IsCurrentTutorClass.valueOf(courseClass, new Date()).is());
    }

    @Test
    public void testNotSessions() {
        CourseClass courseClass = mock(CourseClass.class);
        when(courseClass.getIsDistantLearningCourse()).thenReturn(Boolean.FALSE);
        when(courseClass.getSessions()).thenReturn(Collections.<Session>emptyList());
        Assert.assertTrue(PortalService.IsCurrentTutorClass.valueOf(courseClass, new Date()).is());
    }

    @Test
    public void testHasSessionsAndDateBeforeToday() {
        CourseClass courseClass = mock(CourseClass.class);
        when(courseClass.getIsDistantLearningCourse()).thenReturn(Boolean.FALSE);
        when(courseClass.getSessions()).thenReturn(Collections.singletonList(new Session()));
        when(courseClass.getEndDate()).thenReturn(DateUtils.addDays(new Date(), -1));
        Assert.assertFalse(PortalService.IsCurrentTutorClass.valueOf(courseClass, new Date()).is());
    }

}
