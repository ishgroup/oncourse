package ish.oncourse.website.pages;

import java.util.Collection;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import ish.oncourse.model.Course;
import ish.oncourse.model.CourseClass;
import ish.oncourse.model.Session;
import ish.oncourse.model.Site;
import ish.oncourse.services.course.ICourseService;

import org.apache.tapestry5.annotations.Property;
import org.apache.tapestry5.annotations.SetupRender;
import org.apache.tapestry5.ioc.annotations.Inject;

public class CourseList {

	@Inject
	private ICourseService courseService;

	@Property
	private List<Course> courses;

	@Property
	private Course course;

	@SetupRender
	public void beforeRender() {
		this.courses = courseService.getCourses();
	}

	public Collection<Site> getMapSites() {
		Set<Site> sites = new HashSet<Site>();
		for (Course course : courses) {
			for (CourseClass courseClass : course.getClasses()) {
				for(Session s:courseClass.getSessions()){
					sites.add(s.getRoom().getSite());
				}
			}
		}
		return sites;
	}
}
