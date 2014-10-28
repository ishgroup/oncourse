package ish.oncourse.services.lifecycle;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;

import java.util.Date;

import ish.common.types.CourseEnrolmentType;
import org.apache.cayenne.ObjectContext;
import org.junit.Before;
import org.junit.Test;

import ish.oncourse.model.College;
import ish.oncourse.model.Course;
import ish.oncourse.model.Taggable;
import ish.oncourse.services.ServiceTestModule;
import ish.oncourse.services.persistence.ICayenneService;
import ish.oncourse.test.ServiceTest;

public class TaggableListenerTest extends ServiceTest {
	
	private ICayenneService cayenneService;
	
	@Before
	public void setup() throws Exception {
		initTest("ish.oncourse.services", "service", ServiceTestModule.class);
		
		this.cayenneService = getService(ICayenneService.class);
	}
	
	@Test
	public void testTaggableIds() {
		ObjectContext context = cayenneService.newContext();
		
		College college = context.newObject(College.class);
		college.setFirstRemoteAuthentication(new Date());
		college.setTimeZone("Australia/Sydney");
		college.setWebServicesSecurityCode("345ttn44$%9");
		college.setRequiresAvetmiss(false);
		
		context.commitChanges();
		
		Course course = context.newObject(Course.class);
		course.setCollege(college);
		course.setAngelId(1L);
		course.setEnrolmentType(CourseEnrolmentType.OPEN_FOR_ENROLMENT);
		
		Taggable t = context.newObject(Taggable.class);
		t.setCollege(college);
		t.setEntityIdentifier("Course");
		t.setEntityAngelId(1L);
		
		context.commitChanges();
		
		assertNotNull(t.getEntityWillowId());
		assertEquals(course.getId(), t.getEntityWillowId());
	}

}
