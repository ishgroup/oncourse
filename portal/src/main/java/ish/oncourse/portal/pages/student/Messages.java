package ish.oncourse.portal.pages.student;

import java.util.List;

import org.apache.tapestry5.annotations.Property;
import org.apache.tapestry5.annotations.SetupRender;
import org.apache.tapestry5.ioc.annotations.Inject;

import ish.oncourse.model.CourseClass;
import ish.oncourse.model.Discussion;
import ish.oncourse.portal.access.IAuthenticationService;
import ish.oncourse.portal.annotations.UserRole;
import ish.oncourse.services.courseclass.ICourseClassService;
@Deprecated
@UserRole("student")
public class Messages {
	
	@Inject
	private IAuthenticationService authService;
	
	@Inject
	private ICourseClassService courseClassService;
	
	@Property
	private List<CourseClass> classes;
	
	@Property
	private CourseClass currentClass;
	
	@Property
	private Discussion discussion;
	
	
	@SetupRender
	void beforeRender() {
		
		this.classes = courseClassService.getContactCourseClasses(authService.getUser());
		
	}
	
	
}
