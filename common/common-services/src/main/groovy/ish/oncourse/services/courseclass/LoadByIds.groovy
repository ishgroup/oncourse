package ish.oncourse.services.courseclass

import ish.oncourse.model.College
import ish.oncourse.model.Course
import ish.oncourse.model.CourseClass
import ish.oncourse.model.Room
import org.apache.cayenne.ObjectContext
import org.apache.cayenne.exp.ExpressionFactory
import org.apache.cayenne.query.ObjectSelect

import static org.apache.cayenne.query.QueryCacheStrategy.SHARED_CACHE

class LoadByIds {
    static List<Course> load(List<String> stringIds, ObjectContext context, College college) {

        if (!stringIds || stringIds.size() == 0) {
            return Collections.emptyList()
        }

        List<Long> longIds = stringIds.findAll { it && it.isLong() }.collect { it.toLong() }
        List<Course> courseList = ObjectSelect.query(Course.class)
                .where(Course.COLLEGE.eq(college))
                .and(Course.IS_WEB_VISIBLE.isTrue())
                .and(ExpressionFactory.inDbExp(CourseClass.ID_PK_COLUMN, longIds))
                .prefetch(Course.COURSE_CLASSES.joint())
                .prefetch(Course.COURSE_CLASSES.dot(CourseClass.COLLEGE).joint())
                .prefetch(Course.COURSE_CLASSES.dot(CourseClass.SESSIONS).joint())
                .prefetch(Course.COURSE_CLASSES.dot(CourseClass.ROOM).joint())
                .prefetch(Course.COURSE_CLASSES.dot(CourseClass.ROOM).dot(Room.SITE).joint())
                .cacheStrategy(SHARED_CACHE, Course.class.getSimpleName())
                .select(context)

        return courseList.sort { longIds.indexOf(it.id) }
    }
}
