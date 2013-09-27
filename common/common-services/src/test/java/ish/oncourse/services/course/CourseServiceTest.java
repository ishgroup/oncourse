package ish.oncourse.services.course;

import ish.oncourse.model.Course;
import ish.oncourse.model.Tag;
import ish.oncourse.services.ServiceTestModule;
import ish.oncourse.services.persistence.ICayenneService;
import ish.oncourse.services.tag.ITagService;
import ish.oncourse.test.ServiceTest;
import org.dbunit.database.DatabaseConnection;
import org.dbunit.dataset.xml.FlatXmlDataSet;
import org.dbunit.dataset.xml.FlatXmlDataSetBuilder;
import org.dbunit.operation.DatabaseOperation;
import org.junit.Before;
import org.junit.Test;

import javax.sql.DataSource;
import java.io.InputStream;
import java.util.List;

import static org.junit.Assert.*;

public class CourseServiceTest extends ServiceTest {
	private ITagService tagService;
	@SuppressWarnings("unused")
	private ICayenneService cayenneService;
	private ICourseService service;
	
	@Before
	public void setup() throws Exception {
		initTest("ish.oncourse.services", "service", ServiceTestModule.class);
		InputStream st = CourseServiceTest.class.getClassLoader().getResourceAsStream(
			"ish/oncourse/services/classes/oncourseDataSet.xml");

		FlatXmlDataSetBuilder builder = new FlatXmlDataSetBuilder();
		builder.setColumnSensing(true);
		FlatXmlDataSet dataSet = builder.build(st);

		DataSource refDataSource = getDataSource("jdbc/oncourse");
		DatabaseOperation.CLEAN_INSERT.execute(new DatabaseConnection(refDataSource.getConnection(), null), dataSet);

		this.tagService = getService(ITagService.class);
		this.cayenneService = getService(ICayenneService.class);
		service = getService(ICourseService.class);
	}
	
	@Test
	public void testGetCoursesForRange() {
		assertNotNull("Not able to init course service", service);
		List<Course> result = service.getCourses(0, 30);
		assertTrue("getCourses should return not empty list", !result.isEmpty());
		assertEquals("5 courses should be available", 5, result.size());
		result = service.getCourses(0, 3);
		assertTrue("getCourses should return not empty list", !result.isEmpty());
		assertEquals("3 courses should be available because we limit the count", 3, result.size());
		assertTrue("getCourses should return not empty list", !result.isEmpty());
		result = service.getCourses(3, 5);
		assertEquals("2 courses should be available because we limit the count and shift from the start", 2, result.size());
	}
	
	@Test
	public void testGetCoursesForTagNameSortOrderAndLimit() {
		assertNotNull("Not able to init course service", service);
		//test default alphabetical desc sorting
		List<Course> result = service.getCourses(null, null, false, null);
		assertTrue("getCourses should return not empty list", !result.isEmpty());
		assertEquals("5 courses should be available", 5, result.size());
		assertEquals("The id of the top course should be 5", 5L, result.get(0).getId().longValue());
		//test default alphabetical asc sorting
		result = service.getCourses(null, null, true, null);
		assertTrue("getCourses should return not empty list", !result.isEmpty());
		assertEquals("5 courses should be available", 5, result.size());
		assertEquals("The id of the top course should be 2", 2L, result.get(0).getId().longValue());
		//test with limit
		result = service.getCourses(null, null, true, 3);
		assertTrue("getCourses should return not empty list", !result.isEmpty());
		assertEquals("3 courses should be available because limit used", 3, result.size());
		assertEquals("The id of the top course should be 2", 2L, result.get(0).getId().longValue());
		//test availability
		result = service.getCourses(null, Sort.availability, true, null);
		assertTrue("getCourses should return not empty list", !result.isEmpty());
		assertEquals("5 courses should be available", 5, result.size());
		assertEquals("The id of the top course should be 3", 3L, result.get(0).getId().longValue());
		result = service.getCourses(null, Sort.availability, false, null);
		assertTrue("getCourses should return not empty list", !result.isEmpty());
		assertEquals("5 courses should be available", 5, result.size());
		assertEquals("The id of the top course should be 1", 1L, result.get(0).getId().longValue());
		//test date
		result = service.getCourses(null, Sort.date, true, null);
		assertTrue("getCourses should return not empty list", !result.isEmpty());
		assertEquals("5 courses should be available", 5, result.size());
		assertEquals("The id of the top course should be 1", 1L, result.get(0).getId().longValue());
		result = service.getCourses(null, Sort.date, false, null);
		assertTrue("getCourses should return not empty list", !result.isEmpty());
		assertEquals("5 courses should be available", 5, result.size());
		assertEquals("The id of the top course should be 3", 3L, result.get(0).getId().longValue());
		//test with unexisted tag
		result = service.getCourses("unexisted tag name", Sort.date, true, null);
		assertTrue("getCourses should return empty list", result.isEmpty());
		Tag subjects = tagService.getSubjectsTag();
		assertNotNull("Subjects tag should be founded", subjects);
		//test with existed tag
		result = service.getCourses(subjects.getName(), Sort.date, true, null);
		assertTrue("getCourses should return not empty list", !result.isEmpty());
		assertEquals("5 courses should be linked with tag", 5, result.size());
		assertEquals("The id of the top course should be 1", 1L, result.get(0).getId().longValue());
		result = service.getCourses(subjects.getName(), Sort.date, false, null);
		assertTrue("getCourses should return not empty list", !result.isEmpty());
		assertEquals("5 courses should be linked with tag", 5, result.size());
		assertEquals("The id of the top course should be 3", 3L, result.get(0).getId().longValue());
	}

}
