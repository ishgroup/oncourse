/*
 * Copyright ish group pty ltd. All rights reserved. http://www.ish.com.au No copying or use of this code is allowed without permission in writing from ish.
 */
package ish.oncourse.services.courseclass;

import ish.oncourse.model.Course;

import ish.oncourse.services.ServiceTestModule;
import ish.oncourse.services.course.ICourseService;
import ish.oncourse.test.ServiceTest;
import org.dbunit.database.DatabaseConnection;
import org.dbunit.dataset.xml.FlatXmlDataSet;
import org.dbunit.dataset.xml.FlatXmlDataSetBuilder;
import org.dbunit.operation.DatabaseOperation;
import org.junit.Before;
import org.junit.Test;

import javax.sql.DataSource;
import java.io.InputStream;
import java.util.Arrays;
import java.util.List;

import static org.testng.AssertJUnit.assertEquals;

public class LoadByIdsTest extends ServiceTest {


	@Before
	public void setup() throws Exception {
		initTest("ish.oncourse.services", "service", ServiceTestModule.class);

		InputStream st = CourseClassServiceTest.class.getClassLoader().getResourceAsStream("ish/oncourse/services/classes/oncourseDataSet.xml");
		FlatXmlDataSetBuilder builder =  new FlatXmlDataSetBuilder();
		builder.setColumnSensing(true);
		FlatXmlDataSet dataSet = builder.build(st);
		DataSource onDataSource = getDataSource("jdbc/oncourse");
		DatabaseOperation.CLEAN_INSERT.execute(new DatabaseConnection(onDataSource.getConnection(), null), dataSet);
	}
	
	
	@Test
	public void test() {
		ICourseService service = getService(ICourseService.class);
		List<Course> classList = service.loadByIds(Arrays.asList("5","1", "4", "2", "3"));

		assertEquals(5, classList.size());
		
		assertEquals(5L, classList.get(0).getId().longValue());
		assertEquals(1L, classList.get(1).getId().longValue());
		assertEquals(4L, classList.get(2).getId().longValue());
		assertEquals(2L, classList.get(3).getId().longValue());
		assertEquals(3L, classList.get(4).getId().longValue());

	}
}
