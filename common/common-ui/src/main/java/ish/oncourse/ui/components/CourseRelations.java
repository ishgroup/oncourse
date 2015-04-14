package ish.oncourse.ui.components;

import ish.oncourse.model.Course;
import org.apache.tapestry5.annotations.Parameter;
import org.apache.tapestry5.annotations.Property;
import org.apache.tapestry5.annotations.SetupRender;

import java.util.Collections;
import java.util.Comparator;
import java.util.List;

public class CourseRelations {
	
	@Property
    @Parameter(required = true)
    private Course course;
	
	@Property
	private Course relatedCourse;
	
	@Property
	//@Parameter(required = true)
    private List<Course> courseRelations;
	
	@SetupRender
    public void beforeRender() {
		courseRelations = course.getRelatedToCourses();
		Collections.sort(courseRelations, new Comparator<Course>() {
			@Override
			public int compare(Course o1, Course o2) {
				if (o1 != null && o2!= null && o1.getName() != null) {
					return o1.getName().compareTo(o2.getName());
				}
				return 0;
			}
		});
    }

}
