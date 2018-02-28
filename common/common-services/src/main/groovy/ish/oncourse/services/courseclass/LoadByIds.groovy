package ish.oncourse.services.courseclass

import ish.oncourse.model.College
import ish.oncourse.model.Course
import ish.oncourse.model.CourseClass
import org.apache.cayenne.ObjectContext
import org.apache.cayenne.exp.ExpressionFactory
import org.apache.cayenne.query.ObjectSelect

import static org.apache.cayenne.query.QueryCacheStrategy.LOCAL_CACHE

class LoadByIds {
    static List<Course> load(List<String> stringIds, ObjectContext context, College college) {

        if (!stringIds || stringIds.size() == 0) {
            return Collections.emptyList()
        }

        List<Long> longIds = stringIds.findAll { it && it.isLong() }.collect { it.toLong() }
        List<Course> courseList = new LinkedList<>(ObjectSelect.query(Course.class)
                .where(Course.COLLEGE.eq(college))
                .and(Course.IS_WEB_VISIBLE.isTrue())
                .and(ExpressionFactory.inDbExp(CourseClass.ID_PK_COLUMN, longIds))
                .cacheStrategy(LOCAL_CACHE, Course.class.getSimpleName())
                .select(context))

        return courseList.sort { longIds.indexOf(it.id) }
    }
}
