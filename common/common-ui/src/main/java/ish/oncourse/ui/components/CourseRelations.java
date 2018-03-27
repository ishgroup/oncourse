package ish.oncourse.ui.components;

import ish.oncourse.model.Course;
import ish.oncourse.services.course.ICourseService;
import ish.oncourse.ui.base.ISHCommon;
import ish.oncourse.ui.utils.CourseItemModel;
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

	@SetupRender
    public void beforeRender() {
		courseRelations = CourseItemModel.selectRelatedCourses(course);
    }
}
