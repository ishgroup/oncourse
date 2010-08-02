package ish.oncourse.model;

import ish.oncourse.model.auto._Course;

import java.util.ArrayList;
import java.util.List;

public class Course extends _Course {
	public List<CourseClass> getEnrollableClasses() {
		List<CourseClass> list = new ArrayList<CourseClass>(getClasses().size());

		for (CourseClass courseClass : getClasses()) {
			if (!courseClass.isCancelled()
					&& !courseClass.hasAvailableEnrolmentPlaces()) {
				list.add(courseClass);
			}
		}

		return list;
	}
}
