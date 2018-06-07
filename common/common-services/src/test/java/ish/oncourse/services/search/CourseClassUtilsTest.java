package ish.oncourse.services.search;

import ish.oncourse.model.CourseClass;
import ish.oncourse.services.ServiceTestModule;
import ish.oncourse.services.persistence.ICayenneService;
import ish.oncourse.solr.functions.course.DateFunctions;
import ish.oncourse.solr.query.SearchParams;
import ish.oncourse.test.LoadDataSet;
import ish.oncourse.test.tapestry.ServiceTest;
import org.apache.cayenne.Cayenne;
import org.apache.cayenne.ObjectContext;
import org.junit.Before;
import org.junit.Test;
import org.mockito.Mockito;

import java.util.Date;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;

public class CourseClassUtilsTest extends ServiceTest {

    private ICayenneService cayenneService;

    @Before
    public void setup() throws Exception {
        initTest("ish.oncourse.services", "service", ServiceTestModule.class);
		new LoadDataSet().dataSetFile("ish/oncourse/services/search/CourseClassUtilsTest.xml")
				.load(testContext.getDS());
        this.cayenneService = getService(ICayenneService.class);
    }

    @Test
    public void testAfterMatch() {

        CourseClass courseClass = Mockito.mock(CourseClass.class);
        Mockito.when(courseClass.getStartDate()).thenReturn(DateFunctions.toDate("2011-01-02 00:00:00"));

        Date date = DateFunctions.toDate("2011-01-01 00:00:00");
        assertEquals("The course class is after this date", 1f , CourseClassUtils.afterScore(courseClass, date), 0);

        date = DateFunctions.toDate("2011-01-03 00:00:00");
        assertEquals("The course class is 1 day before this date", 0.5f, CourseClassUtils.afterScore(courseClass, date), 0);

        Mockito.when(courseClass.getStartDate()).thenReturn(null);
        assertEquals("startDate is null and score is 0.0", 0.0f, CourseClassUtils.afterScore(courseClass, date), 0);
    }


    @Test
    public void testFocusMatchForTime()
    {
        ObjectContext context = this.cayenneService.newContext();

        CourseClass daytimeClass = Cayenne.objectForPK(context, CourseClass.class, 1);
        assertTrue("The course class is daytime class", 1 == CourseClassUtils.focusMatchForTime(daytimeClass, SearchParamsParser.PARAM_VALUE_daytime));
        assertTrue("The course class is not evening class", 1 > CourseClassUtils.focusMatchForTime(daytimeClass, SearchParamsParser.PARAM_VALUE_evening));

        CourseClass eveningClass = Cayenne.objectForPK(context, CourseClass.class, 2);
        assertTrue("The course class is evening class", 1 == CourseClassUtils.focusMatchForTime(eveningClass, SearchParamsParser.PARAM_VALUE_evening));
        assertTrue("The course class is not daytime class", 1 > CourseClassUtils.focusMatchForTime(eveningClass, SearchParamsParser.PARAM_VALUE_daytime));


        CourseClass selfpacedClass = Cayenne.objectForPK(context, CourseClass.class, 3);
        assertTrue("The course class is evening class", 1 == CourseClassUtils.focusMatchForTime(selfpacedClass, SearchParamsParser.PARAM_VALUE_evening));
        assertTrue("The course class is daytime class", 1 == CourseClassUtils.focusMatchForTime(selfpacedClass, SearchParamsParser.PARAM_VALUE_daytime));


    }

    @Test
    public void testFocusMatchForSite() {
        ObjectContext context = this.cayenneService.newContext();
        CourseClass courseClass = Cayenne.objectForPK(context, CourseClass.class, 1);
        SearchParams params = new SearchParams();

        params.setSiteId(1L);
        assertEquals(1.0f, CourseClassUtils.focusMatchForClass(courseClass, params), 0f);

        params.setSiteId(2L);
        assertEquals(0.0f, CourseClassUtils.focusMatchForClass(courseClass, params), 0f);
    }

}
