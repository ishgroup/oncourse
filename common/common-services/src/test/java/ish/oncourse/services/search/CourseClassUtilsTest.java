package ish.oncourse.services.search;

import ish.oncourse.model.CourseClass;
import ish.oncourse.services.ServiceModule;
import ish.oncourse.services.lifecycle.QueueableLifecycleListenerTest;
import ish.oncourse.services.persistence.ICayenneService;
import ish.oncourse.test.ServiceTest;
import org.apache.cayenne.Cayenne;
import org.apache.cayenne.ObjectContext;
import org.dbunit.database.DatabaseConnection;
import org.dbunit.dataset.xml.FlatXmlDataSet;
import org.dbunit.dataset.xml.FlatXmlDataSetBuilder;
import org.dbunit.operation.DatabaseOperation;
import org.junit.Before;
import org.junit.Test;

import javax.sql.DataSource;
import java.io.InputStream;

import static org.junit.Assert.assertTrue;

public class CourseClassUtilsTest extends ServiceTest {

    private ICayenneService cayenneService;

    @Before
    public void setup() throws Exception {
        initTest("ish.oncourse.services", "service", ServiceModule.class);

        InputStream st = QueueableLifecycleListenerTest.class.getClassLoader().getResourceAsStream(
                "ish/oncourse/services/search/CourseClassUtilsTest.xml");

        FlatXmlDataSet dataSet = new FlatXmlDataSetBuilder().build(st);
        DataSource dataSource = getDataSource("jdbc/oncourse");
        DatabaseOperation.CLEAN_INSERT.execute(new DatabaseConnection(dataSource.getConnection(), null), dataSet);

        this.cayenneService = getService(ICayenneService.class);
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
}
