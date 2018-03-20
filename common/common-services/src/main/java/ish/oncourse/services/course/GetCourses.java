/*
 * Copyright ish group pty ltd. All rights reserved. http://www.ish.com.au No copying or use of this code is allowed without permission in writing from ish.
 */
package ish.oncourse.services.course;

import ish.oncourse.model.College;
import ish.oncourse.model.Course;
import org.apache.cayenne.ObjectContext;
import org.apache.cayenne.query.ObjectSelect;
import org.apache.cayenne.query.QueryCacheStrategy;

import java.util.LinkedList;
import java.util.List;

/**
 * This function returns web visible courses for specific college
 */
public class GetCourses {
	private ObjectContext objectContext;
	private ObjectSelect<Course> query;

	public GetCourses(ObjectContext objectContext, College college) {
		this.objectContext = objectContext;
		this.query = ObjectSelect.query(Course.class)
				.cacheStrategy(QueryCacheStrategy.LOCAL_CACHE, Course.class.getSimpleName())
				.where(Course.COLLEGE.eq(college))
				.and(Course.IS_WEB_VISIBLE.eq(true));
	}

	public GetCourses offset(int start) {
		if (start > -1)
			this.query.offset(start);
		return this;
	}

	public GetCourses limit(int rows) {
		if (rows > -1)
			this.query.limit(rows);
		return this;
	}

	public List<Course> get() {
		return new LinkedList<>(this.query.select(this.objectContext));
	}

}
