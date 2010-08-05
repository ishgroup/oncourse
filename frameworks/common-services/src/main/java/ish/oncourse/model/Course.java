package ish.oncourse.model;

import ish.oncourse.model.auto._Course;

import java.util.ArrayList;
import java.util.List;

public class Course extends _Course {
	
	public Integer getId() {
		return (getObjectId() != null && !getObjectId().isTemporary()) ? ((Number) getObjectId()
				.getIdSnapshot().get(ID_PK_COLUMN)).intValue()
				: null;
	}
	
	public List<CourseClass> getEnrollableClasses() {
		List<CourseClass> list = new ArrayList<CourseClass>(getClasses().size());

		for (CourseClass courseClass : getClasses()) {
			if (!courseClass.isCancelled()
					&& courseClass.isHasAvailableEnrolmentPlaces()) {
				list.add(courseClass);
			}
		}

		return list;
	}
}
