package ish.oncourse.services.course

import ish.oncourse.model.College
import ish.oncourse.model.Course
import org.apache.cayenne.ObjectContext
import org.apache.cayenne.query.ObjectSelect
import org.apache.cayenne.query.QueryCacheStrategy

/**
 *
 * User: akoiro
 * Date: 20/3/18
 *
 * This function returns web visible courses for specific college
 */
class GetCourses {

    private ObjectContext objectContext
    private ObjectSelect<Course> query

    GetCourses(ObjectContext objectContext, College college) {
        this.objectContext = objectContext
        this.query = ObjectSelect.query(Course)
                .cacheStrategy(QueryCacheStrategy.LOCAL_CACHE, Course.class.getSimpleName())
                .where(Course.COLLEGE.eq(college))
                .and(Course.IS_WEB_VISIBLE.eq(true))
    }

    GetCourses offset(int start) {
        if (start > -1)
            this.query.offset(start)
        return this
    }

    GetCourses limit(int rows) {
        if (rows > -1)
            this.query.limit(rows)
        return this
    }

    List<Course> get() {
        return new LinkedList<>(this.query.select(this.objectContext))
    }
}
