package ish.oncourse.linktransform.functions;

import ish.oncourse.model.CourseClass;
import ish.oncourse.services.ServiceTestModule;
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

import static org.junit.Assert.assertNotNull;

/**
 * Created by pavel on 4/13/17.
 */
public class RootTagAvailabilityTest extends ServiceTest {
    ICayenneService cayenneService;
    IWebSiteService webSiteService;
    ICourseClassService courseClassService;

    @Before
    public void setup() throws Exception {
        initTest("ish.oncourse.services", "service", ServiceTestModule.class);

        InputStream st = RootTagAvailabilityTest.class.getClassLoader().getResourceAsStream(
                "ish/oncourse/linktransform/RootTagAvailabilityTest.xml");

        FlatXmlDataSet dataSet = new FlatXmlDataSetBuilder().setColumnSensing(true).build(st);
        DataSource refDataSource = getDataSource("jdbc/oncourse");
        DatabaseOperation.CLEAN_INSERT.execute(new DatabaseConnection(refDataSource.getConnection(), null), dataSet);

        cayenneService = getService(ICayenneService.class);
        webSiteService = getService(IWebSiteService.class);
        courseClassService = getService(ICourseClassService.class);
    }

    @Test
    public void testAvailability() throws Exception {
        CourseClass courseClass = GetCourseClassByPath
                .valueOf(cayenneService.sharedContext(), webSiteService.getCurrentWebSite(), "/class/DJPLF-1")
                .get();
        assertNotNull(courseClass);
    }
}
