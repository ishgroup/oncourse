package ish.oncourse.linktransform;

import ish.oncourse.linktransform.functions.GetCourseClassByPath;
import ish.oncourse.model.Course;
import ish.oncourse.model.CourseClass;
import ish.oncourse.model.PaymentInSuccessFailAbandonTest;
import ish.oncourse.services.ServiceModule;
import ish.oncourse.services.courseclass.ICourseClassService;
import ish.oncourse.services.persistence.ICayenneService;
import ish.oncourse.services.site.IWebSiteService;
import ish.oncourse.test.ServiceTest;
import org.dbunit.database.DatabaseConnection;
import org.dbunit.dataset.xml.FlatXmlDataSet;
import org.dbunit.dataset.xml.FlatXmlDataSetBuilder;
import org.dbunit.operation.DatabaseOperation;
import org.junit.Before;
import org.junit.Test;

import javax.sql.DataSource;
import java.io.InputStream;

import static org.junit.Assert.*;

/**
 * Test for checking object accessibility on PageLinkTransformer class
 * Created by pavel on 3/9/17.
 */
public class PageLinkTransformerTest extends ServiceTest {

    ICayenneService cayenneService;
    IWebSiteService webSiteService;
    ICourseClassService courseClassService;

    @Before
    public void setup() throws Exception {
        initTest("ish.oncourse.services", "service", ServiceModule.class);

        InputStream st = PaymentInSuccessFailAbandonTest.class.getClassLoader().getResourceAsStream(
                "ish.oncourse.linktransform/pageLinkTransformerTest_expected.xml");

        FlatXmlDataSet dataSet = new FlatXmlDataSetBuilder().setColumnSensing(true).build(st);
        DataSource refDataSource = getDataSource("jdbc/oncourse");
        DatabaseOperation.CLEAN_INSERT.execute(new DatabaseConnection(refDataSource.getConnection(), null), dataSet);

        cayenneService = getService(ICayenneService.class);
        webSiteService = getService(IWebSiteService.class);
        courseClassService = getService(ICourseClassService.class);
    }

    /**
     * Checking web course class web visibility from test dataset.
     * Course 'Leader's forum' is web invisible and marked by web invisible tag 'Leader's Forum'. Course have one
     *  web visible course class without any tag.
     * @throws Exception
     */
    @Test
    public void testCourseClassWebVisibility() throws Exception {
        CourseClass courseClass = GetCourseClassByPath
                .valueOf(cayenneService.sharedContext(), webSiteService.getCurrentCollege(), "/class/DJPLF-1")
                .get();

        assertNotNull(courseClass);
        assertTrue(courseClass.getIsWebVisible());

        Course course = courseClass.getCourse();

        assertNotNull(course);
        assertFalse(course.getIsWebVisible());
    }
}
