/*
 * Copyright ish group pty ltd. All rights reserved. http://www.ish.com.au No copying or use of this code is allowed without permission in writing from ish.
 */
package ish.oncourse.services.application;

import ish.oncourse.model.Application;
import ish.oncourse.model.Course;
import ish.oncourse.model.Student;
import ish.oncourse.services.cache.RequestCached;

public interface IApplicationService {

	/**
	 * @param course
	 * @param student
	 * @return application for course-student if exist
	 */
	@RequestCached
	Application findOfferedApplicationBy(Course course, Student student);
	
	Application findNewApplicationBy(Course course, Student student);
}
