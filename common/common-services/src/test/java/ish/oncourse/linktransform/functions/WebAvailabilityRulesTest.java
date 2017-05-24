package ish.oncourse.linktransform.functions;

import ish.oncourse.model.Course;
import ish.oncourse.model.CourseClass;
import ish.oncourse.services.ServiceModule;
import ish.oncourse.services.courseclass.ICourseClassService;
import ish.oncourse.services.persistence.ICayenneService;
import ish.oncourse.services.site.IWebSiteService;
import ish.oncourse.test.ServiceTest;
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

import static org.junit.Assert.*;

/**
 * Test of availability courses and course classes according to
 * https://www.ish.com.au/s/onCourse/doc/latest/manual/classes.html#classes-Publishing&gsc.tab=0
 * Created by pavel on 5/24/17.
 */
public class WebAvailabilityRulesTest extends ServiceTest {

    private ICayenneService cayenneService;
    private IWebSiteService webSiteService;
    private ICourseClassService courseClassService;

    private static final String TEST_COURSE_CODE = "DJPLF";
    private static final String TEST_COURSE_CLASS_CODE = "1";

    private static final String TEST_COURSE_PATH = "/course/" + TEST_COURSE_CODE;
    private static final String TEST_COURSE_CLASS_PATH = "/class/" + TEST_COURSE_CODE + "-" + TEST_COURSE_CLASS_CODE;

    @Before
    public void prepare() throws Exception {
        initTest("ish.oncourse.services", "service", ServiceModule.class);

        InputStream st = RootTagAvailabilityTest.class.getClassLoader().getResourceAsStream(
                "ish/oncourse/linktransform/WebAvailabilityRulesTest.xml");

        FlatXmlDataSet dataSet = new FlatXmlDataSetBuilder().setColumnSensing(true).build(st);
        DataSource refDataSource = getDataSource("jdbc/oncourse");
        DatabaseOperation.CLEAN_INSERT.execute(new DatabaseConnection(refDataSource.getConnection(), null), dataSet);

        cayenneService = getService(ICayenneService.class);
        webSiteService = getService(IWebSiteService.class);
        courseClassService = getService(ICourseClassService.class);

        initCourseClassEndTime();
    }

    @Test
    public void testAvailabilityCombinations() throws Exception {

        GetCourseByPath courseGetter = GetCourseByPath
                .valueOf(cayenneService.sharedContext(), webSiteService.getCurrentWebSite(), TEST_COURSE_PATH);
        GetCourseClassByPath courseClassGetter = GetCourseClassByPath
                .valueOf(cayenneService.sharedContext(), webSiteService.getCurrentWebSite(), TEST_COURSE_CLASS_PATH);


        setStatus(CourseAvailability.ENABLED_AND_VISIBLE, CourseClassAvailability.DISABLED);
        // Course is included in the search results but the class is not visible on the website
        assertNotNull(courseGetter.get());
        assertNull(courseClassGetter.get());
        assertFalse(checkEnrolmentIsVisibile());


        setStatus(CourseAvailability.ENABLED_AND_VISIBLE, CourseClassAvailability.ALLOWED);
        // Course is included in the search results, but the class is hidden from the list and results views.
        // The class page can be accessed directly via the class URL but there is no option to enrol.
        // Enrolments can be processed via Quick Enrol only.
        assertNotNull(courseGetter.get());
        assertNotNull(courseClassGetter.get());
        assertFalse(checkEnrolmentIsVisibile());

        setStatus(CourseAvailability.ENABLED_AND_VISIBLE, CourseClassAvailability.ALLOWED_AND_VISIBLE);
        // Course and class are included in the search results and online enrolments are available.
        assertNotNull(courseGetter.get());
        assertNotNull(courseClassGetter.get());
        assertTrue(checkEnrolmentIsVisibile());

        setStatus(CourseAvailability.ENABLED_AND_VISIBLE, CourseClassAvailability.CANCELLED);
        // Course is included in the search results. If the class is still shown on the web, the enrol now button will be replaced with the word 'Cancelled'. No enrolments can be taken.
        assertNotNull(courseGetter.get());
        assertNotNull(courseClassGetter.get());
        assertFalse(checkEnrolmentIsVisibile());

        setStatus(CourseAvailability.ENABLED, CourseClassAvailability.DISABLED);
        // No information is available online for the course or class.
        assertNull(courseGetter.get());
        assertNull(courseClassGetter.get());
        assertFalse(checkEnrolmentIsVisibile());

        setStatus(CourseAvailability.ENABLED, CourseClassAvailability.ALLOWED);
        // The course and class are hidden from search, but the class page can be accessed via the URL directly.
        // There is no option to enrol online, enrolments can be processed from Quick Enrol only.
        assertNull(courseGetter.get());
        assertNotNull(courseClassGetter.get());
        assertFalse(checkEnrolmentIsVisibile());

        setStatus(CourseAvailability.ENABLED, CourseClassAvailability.ALLOWED_AND_VISIBLE);
        // The course and class are hidden from search, but the class page can be accessed via the URL directly.
        // Online enrolment is available.
        assertNull(courseGetter.get());
        assertNotNull(courseClassGetter.get());
        assertTrue(checkEnrolmentIsVisibile());

        setStatus(CourseAvailability.ENABLED, CourseClassAvailability.CANCELLED);
        // The course and class are hidden from search, but provided the class has not be removed from the website the class URL will still be available;
        // The enrol now button will be replaced with the word 'cancelled' and no enrolments can be taken.
        assertNull(courseGetter.get());
        assertNotNull(courseClassGetter.get());
        assertFalse(checkEnrolmentIsVisibile());

    }

    private boolean checkEnrolmentIsVisibile(){
        CourseClass courseClass = getTestCourseClass();
        return courseClass.getIsWebVisible() && courseClass.getIsActive() && !courseClass.isCancelled() &&
                (!courseClass.hasEnded() || courseClass.getIsDistantLearningCourse());
    }

    private void setStatus(CourseAvailability courseAvailability, CourseClassAvailability courseClassAvailability) {
        Course course = getTestCourse();
        CourseClass courseClass = getTestCourseClass();

        course.setIsWebVisible(courseAvailability.getWebVisibility());
        //course -> field 'currently offered' is not influent on course/class/enrolment visibility

        courseClass.setIsWebVisible(courseClassAvailability.getWebVisibility());
        courseClass.setIsActive(courseClassAvailability.getActivity());
        courseClass.setCancelled(courseClassAvailability.getCancelled());

        cayenneService.sharedContext().commitChanges();
    }

    private void initCourseClassEndTime(){
        CourseClass courseClass = getTestCourseClass();
        courseClass.setEndDate(new Date(System.currentTimeMillis() + 1000 * 60 * 60 * 24));
    }

    private Course getTestCourse(){
        return ObjectSelect.query(Course.class, Course.CODE.eq(TEST_COURSE_CODE)).selectOne(cayenneService.sharedContext());
    }

    private CourseClass getTestCourseClass() {
        return ObjectSelect.query(CourseClass.class, CourseClass.CODE.eq(TEST_COURSE_CLASS_CODE)
                .andExp(CourseClass.COURSE.dot(Course.CODE).eq(TEST_COURSE_CODE))).selectOne(cayenneService.sharedContext());
    }

    private enum CourseAvailability {
        ENABLED_AND_VISIBLE(true, true), ENABLED(false, true);

        private boolean w;
        private boolean o;

        CourseAvailability(boolean isWebVisibile, boolean isOffered) {
            w = isWebVisibile;
            o = isOffered;
        }

        boolean getWebVisibility(){
            return w;
        }

        boolean getOfferStatus(){
            return o;
        }
    }

    private enum CourseClassAvailability {
        DISABLED(false, false, false), ALLOWED(true, false, false), ALLOWED_AND_VISIBLE(true, true, false), CANCELLED(true, true, true);

        private boolean a;
        private boolean v;
        private boolean c;

        CourseClassAvailability(boolean isActive, boolean isWebVisible, boolean isCancelled){
            a = isActive;
            v = isWebVisible;
            c = isCancelled;
        }

        boolean getActivity(){
            return a;
        }

        boolean getWebVisibility(){
            return v;
        }

        boolean getCancelled(){
            return c;
        }

    }
}


