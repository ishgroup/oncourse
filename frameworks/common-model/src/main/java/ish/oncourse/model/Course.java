package ish.oncourse.model;

import ish.oncourse.model.auto._Course;
import ish.oncourse.utils.TimestampUtilities;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

public class Course extends _Course {

	public List<CourseClass> getEnrollableClasses() {
		List<CourseClass> list = new ArrayList<CourseClass>(getCourseClasses()
				.size());

		for (CourseClass courseClass : getCourseClasses()) {
			if (!courseClass.isCancelled()
					&& courseClass.isHasAvailableEnrolmentPlaces()) {
				list.add(courseClass);
			}
		}

		return list;
	}

	public List<CourseClass> getCurrentClasses() {
		List<CourseClass> list = new ArrayList<CourseClass>(getCourseClasses()
				.size());

		for (CourseClass courseClass : getCourseClasses()) {
			if (Boolean.TRUE.equals(courseClass.getIsWebVisible())
					&& courseClass.getEndDate() != null
					&& courseClass.getEndDate().after(
							TimestampUtilities.normalisedDate(new Date()))) {
				list.add(courseClass);
			}

		}

		return list;
	}

}
