package ish.oncourse.ui.components;

import ish.oncourse.model.Course;
import ish.oncourse.services.course.ICourseService;
import ish.oncourse.ui.base.ISHCommon;
import ish.oncourse.ui.utils.CourseItemModelGeneric;
import org.apache.tapestry5.annotations.Parameter;
import org.apache.tapestry5.annotations.Property;
import org.apache.tapestry5.annotations.SetupRender;
import org.apache.tapestry5.ioc.annotations.Inject;

import java.util.List;

public class CourseRelations extends ISHCommon {

	@Inject
	private ICourseService courseService;

	@Property
    @Parameter(required = true)
    private Course course;
	
	@Property
	private Course relatedCourse;
	
	@Property
    private List<Course> courseRelations;

	@Property
	@Parameter
	private CourseItemModelGeneric model;

	@SetupRender
    public void beforeRender() {
		if (model != null) {
			courseRelations = model.getRelatedCourses();
		}
		else {
			courseRelations = course.getRelatedCourses();
		}
    }
}
