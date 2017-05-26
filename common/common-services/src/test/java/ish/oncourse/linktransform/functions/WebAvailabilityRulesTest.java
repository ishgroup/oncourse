package ish.oncourse.linktransform.functions;

import ish.oncourse.model.Course;
import ish.oncourse.model.CourseClass;
import ish.oncourse.model.WebSite;
import ish.oncourse.services.ServiceModule;
import ish.oncourse.services.persistence.ICayenneService;
import ish.oncourse.services.site.IWebSiteService;
import ish.oncourse.test.ServiceTest;
import org.apache.cayenne.ObjectContext;
import org.apache.cayenne.query.ObjectSelect;
import org.dbunit.database.DatabaseConnection;
import org.dbunit.dataset.xml.FlatXmlDataSet;
import org.dbunit.dataset.xml.FlatXmlDataSetBuilder;
import org.dbunit.operation.DatabaseOperation;
import org.junit.Before;
import org.junit.Test;

import javax.sql.DataSource;
import java.io.InputStream;
import java.sql.Date;
import java.util.List;

import static org.junit.Assert.*;

/**
 * Test of availability courses and course classes according to
 * https://www.ish.com.au/s/onCourse/doc/latest/manual/classes.html#classes-Publishing&gsc.tab=0
 * Created by pavel on 5/24/17.
 */
public class WebAvailabilityRulesTest extends ServiceTest {

    private ObjectContext context;
    private WebSite webSite;

    @Before
    public void prepare() throws Exception {
        initTest("ish.oncourse.services", "service", ServiceModule.class);

        InputStream st = RootTagAvailabilityTest.class.getClassLoader().getResourceAsStream(
                "ish/oncourse/linktransform/WebAvailabilityRulesTest.xml");

        FlatXmlDataSet dataSet = new FlatXmlDataSetBuilder().setColumnSensing(true).build(st);
        DataSource refDataSource = getDataSource("jdbc/oncourse");
        DatabaseOperation.CLEAN_INSERT.execute(new DatabaseConnection(refDataSource.getConnection(), null), dataSet);

        ICayenneService cayenneService = getService(ICayenneService.class);
        IWebSiteService webSiteService = getService(IWebSiteService.class);

        context = cayenneService.newContext();
        webSite = webSiteService.getCurrentWebSite();

        initCourseClassEndTime();
    }

    @Test
    public void testAvailabilityCombinations() throws Exception {

        // Course is included in the search results but the class is not visible on the website
        assertNotNull(GetCourseByPath.valueOf(context, webSite, "/course/EnabledAndVisible1").get());
        assertNull(GetCourseClassByPath.valueOf(context, webSite, "/class/EnabledAndVisible1-Disabled1").get());
        assertFalse(checkEnrolmentIsVisibile("EnabledAndVisible1", "Disabled1"));

        // Course is included in the search results, but the class is hidden from the list and results views.
        // The class page can be accessed directly via the class URL but there is no option to enrol.
        // Enrolments can be processed via Quick Enrol only.
        assertNotNull(GetCourseByPath.valueOf(context, webSite, "/course/EnabledAndVisible1").get());
        assertNotNull(GetCourseClassByPath.valueOf(context, webSite, "/class/EnabledAndVisible1-Allowed1").get());
        assertFalse(checkEnrolmentIsVisibile("EnabledAndVisible1", "Allowed1"));

        // Course and class are included in the search results and online enrolments are available.
        assertNotNull(GetCourseByPath.valueOf(context, webSite, "/course/EnabledAndVisible1").get());
        assertNotNull(GetCourseClassByPath.valueOf(context, webSite, "/class/EnabledAndVisible1-AllowedAndVisible1").get());
        assertTrue(checkEnrolmentIsVisibile("EnabledAndVisible1", "AllowedAndVisible1"));

        // special case of 'cancelled'
        assertNotNull(GetCourseByPath.valueOf(context, webSite, "/course/EnabledAndVisible1").get());
        assertNull(GetCourseClassByPath.valueOf(context, webSite, "/class/EnabledAndVisible1-CancelledInvisible1").get());
        assertFalse(checkEnrolmentIsVisibile("EnabledAndVisible1", "CancelledInvisible1"));

        // Course is included in the search results. If the class is still shown on the web, the enrol now button will be replaced with the word 'Cancelled'. No enrolments can be taken.
        assertNotNull(GetCourseByPath.valueOf(context, webSite, "/course/EnabledAndVisible1").get());
        assertNotNull(GetCourseClassByPath.valueOf(context, webSite, "/class/EnabledAndVisible1-CancelledVisible1").get());
        assertFalse(checkEnrolmentIsVisibile("EnabledAndVisible1", "CancelledVisible1"));

        // No information is available online for the course or class.
        assertNull(GetCourseByPath.valueOf(context, webSite, "/course/Enabled1").get());
        assertNull(GetCourseClassByPath.valueOf(context, webSite, "/class/Enabled1-Disabled1").get());
        assertFalse(checkEnrolmentIsVisibile("Enabled1", "Disabled1"));

        // The course and class are hidden from search, but the class page can be accessed via the URL directly.
        // There is no option to enrol online, enrolments can be processed from Quick Enrol only.
        assertNull(GetCourseByPath.valueOf(context, webSite, "/course/Enabled1").get());
        assertNotNull(GetCourseClassByPath.valueOf(context, webSite, "/class/Enabled1-Allowed1").get());
        assertFalse(checkEnrolmentIsVisibile("Enabled1", "Allowed1"));

        // The course and class are hidden from search, but the class page can be accessed via the URL directly.
        // Online enrolment is available.
        assertNull(GetCourseByPath.valueOf(context, webSite, "/course/Enabled1").get());
        assertNotNull(GetCourseClassByPath.valueOf(context, webSite, "/class/Enabled1-AllowedAndVisible1").get());
        assertTrue(checkEnrolmentIsVisibile("Enabled1", "AllowedAndVisible1"));

        // The course and class are hidden from search, but provided the class has not be removed from the website the class URL will still be available;
        // The enrol now button will be replaced with the word 'cancelled' and no enrolments can be taken.
        assertNull(GetCourseByPath.valueOf(context, webSite, "/course/Enabled1").get());
        assertNull(GetCourseClassByPath.valueOf(context, webSite, "/class/Enabled1-CancelledInvisible1").get());
        assertFalse(checkEnrolmentIsVisibile("Enabled1", "CancelledInvisible1"));

        // special case of 'cancelled'
        assertNull(GetCourseByPath.valueOf(context, webSite, "/course/Enabled1").get());
        assertNotNull(GetCourseClassByPath.valueOf(context, webSite, "/class/Enabled1-CancelledVisible1").get());
        assertFalse(checkEnrolmentIsVisibile("Enabled1", "CancelledVisible1"));
    }

    /**
     * Check enrollment visibility according to condition in CourseClassItem.java (in inaccessible module)
     *
     * @param courseCode
     * @param courseClassCode
     * @return
     */
    private boolean checkEnrolmentIsVisibile(String courseCode, String courseClassCode) {
        CourseClass courseClass = ObjectSelect.query(CourseClass.class, CourseClass.CODE.eq(courseClassCode)
                .andExp(CourseClass.COURSE.dot(Course.CODE).eq(courseCode))).selectOne(context);
        return courseClass.getIsWebVisible() && courseClass.getIsActive() && !courseClass.isCancelled() &&
                (!courseClass.hasEnded() || courseClass.getIsDistantLearningCourse());
    }

    /**
     * Make classes available for enrolment visibility - move all classes 'end date' forward for a one day
     */
    private void initCourseClassEndTime() {
        List<CourseClass> classes = ObjectSelect.query(CourseClass.class).select(context);
        for (CourseClass courseClass : classes) {
            courseClass.setEndDate(new Date(System.currentTimeMillis() + 1000 * 60 * 60 * 24));
        }
        context.commitChanges();
    }
}


