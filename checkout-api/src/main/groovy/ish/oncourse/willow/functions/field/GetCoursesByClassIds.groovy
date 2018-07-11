package ish.oncourse.willow.functions.field

import ish.oncourse.model.College
import ish.oncourse.model.Course
import ish.oncourse.model.CourseClass
import org.apache.cayenne.ObjectContext
import org.apache.cayenne.exp.ExpressionFactory
import org.apache.cayenne.query.ObjectSelect
import org.apache.cayenne.query.QueryCacheStrategy

class GetCoursesByClassIds {

    private List<String> classIds
    private College college
    private ObjectContext context

    GetCoursesByClassIds(List<String> classIds, College college, ObjectContext context) {
        this.classIds = classIds
        this.college = college
        this.context = context
    }

    List<Course> getCourses() {
        ObjectSelect.query(CourseClass)
                .where(ExpressionFactory.inDbExp(CourseClass.ID_PK_COLUMN, classIds))
                .and(CourseClass.COLLEGE.eq(college))
                .prefetch(CourseClass.COURSE.joint())
                .cacheStrategy(QueryCacheStrategy.SHARED_CACHE)
                .cacheGroup(CourseClass.class.simpleName)
                .select(context)
                *.course
                .findAll { c -> c.fieldConfigurationScheme }
                .unique()
    }
}
