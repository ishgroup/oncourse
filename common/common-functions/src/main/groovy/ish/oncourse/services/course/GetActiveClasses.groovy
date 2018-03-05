package ish.oncourse.services.course

import ish.oncourse.model.Course
import ish.oncourse.model.CourseClass
import ish.oncourse.services.courseclass.CheckClassAge
import ish.oncourse.services.courseclass.ClassAge
import org.apache.cayenne.query.ObjectSelect
import org.apache.cayenne.query.QueryCacheStrategy

import java.util.concurrent.Callable

class GetActiveClasses {

    private Course course
    private ClassAge classAge

    private GetActiveClasses() {}

    static GetActiveClasses valueOf(Course course, Callable<ClassAge> classAge) {
        GetActiveClasses getter = new GetActiveClasses()
        getter.course = course
        getter.classAge = classAge.call()
        return getter
    }

    List<CourseClass> get() {
        CheckClassAge checkClassAge = new CheckClassAge().classAge(classAge)


        List<CourseClass> currentClasses = ObjectSelect.query(CourseClass).where(CourseClass.COURSE.eq(course))
                .and(CourseClass.IS_WEB_VISIBLE.isTrue())
                .and(CourseClass.CANCELLED.isFalse())
                .cacheGroup(CourseClass.simpleName)
                .cacheStrategy(QueryCacheStrategy.LOCAL_CACHE)       
                .select(course.objectContext)
        
        return currentClasses.findAll { checkClassAge.courseClass(it).check() }
    }
}
