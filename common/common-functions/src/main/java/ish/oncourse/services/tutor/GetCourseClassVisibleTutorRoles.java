/*
 * Copyright ish group pty ltd. All rights reserved. http://www.ish.com.au No copying or use of this code is allowed without permission in writing from ish.
 */
package ish.oncourse.services.tutor;

import ish.oncourse.model.CourseClass;
import ish.oncourse.model.Tutor;
import ish.oncourse.model.TutorRole;
import org.apache.cayenne.ObjectContext;
import org.apache.cayenne.query.ObjectSelect;
import org.apache.cayenne.query.QueryCacheStrategy;

import java.util.List;
import java.util.stream.Collectors;

public class GetCourseClassVisibleTutorRoles {

	private ObjectContext context;
	private CourseClass courseClass;
	private QueryCacheStrategy cacheStrategy = QueryCacheStrategy.LOCAL_CACHE;

	private GetCourseClassVisibleTutorRoles() {}

	public static GetCourseClassVisibleTutorRoles valueOf(ObjectContext context, CourseClass courseClass) {
		GetCourseClassVisibleTutorRoles obj = new GetCourseClassVisibleTutorRoles();
		obj.context = context;
		obj.courseClass = courseClass;
		return obj;
	}


	public GetCourseClassVisibleTutorRoles cacheStrategy(QueryCacheStrategy cacheStrategy) {
		this.cacheStrategy = cacheStrategy;
		return this;
	}

	public GetCourseClassVisibleTutorRoles courseClass(CourseClass courseClass) {
		this.courseClass = courseClass;
		return this;
	}

	public List<TutorRole> get() {
		return ObjectSelect.query(TutorRole.class)
				.where(TutorRole.COURSE_CLASS.eq(courseClass))
				.and(TutorRole.IN_PUBLICITY.isTrue())
				.prefetch(TutorRole.TUTOR.joint())
				.prefetch(TutorRole.TUTOR.dot(Tutor.CONTACT).joint())
				.cacheStrategy(cacheStrategy, TutorRole.class.getSimpleName())
				.select(context)
				/*
				 the stream filter is used instead of Cayenne query expression to have a possibility to cache this request.
				 if we use Cayenne query expression with new Date(), cayenne always will generate a new cache key for such an expression
				 */
				.stream().filter((tr) -> GetIsActiveTutor.valueOf(tr.getTutor()).get())
				.collect(Collectors.toList());

	}
}
