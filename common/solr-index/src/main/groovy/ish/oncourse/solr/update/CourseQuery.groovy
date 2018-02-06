package ish.oncourse.solr.update

import ish.oncourse.model.*
import org.apache.cayenne.exp.Expression
import org.apache.cayenne.query.ObjectSelect


import static org.apache.cayenne.query.ObjectSelect.query

/**
 * Created by alex on 2/6/18.
 */
class CourseQuery {    
    private static Expression courseWhereExpression() {
        return Course.IS_WEB_VISIBLE.isTrue()
    }

    private static Expression classWhereExpression() {
        return CourseClass.IS_WEB_VISIBLE.eq(true)
                .andExp(CourseClass.CANCELLED.eq(false))
                .andExp(CourseClass.END_DATE.gt(new Date()).andExp(CourseClass.IS_DISTANT_LEARNING_COURSE.eq(false)).orExp(CourseClass.IS_DISTANT_LEARNING_COURSE.eq(true)))
    }

    static final ObjectSelect<Course> coursesToReindexQuery(Contact contact){
        query(Course).where(courseWhereExpression())
                .and(classWhereExpression())
                .and(Course.COURSE_CLASSES.dot(CourseClass.TUTOR_ROLES).dot(TutorRole.TUTOR).dot(Tutor.CONTACT).eq(contact))
    }

    static final ObjectSelect<Course> coursesToReindexQuery(Tutor tutor){
        query(Course).where(courseWhereExpression())
                .and(classWhereExpression())
                .and(Course.COURSE_CLASSES.dot(CourseClass.TUTOR_ROLES).dot(TutorRole.TUTOR).eq(tutor))
    }

    static final ObjectSelect<Course> coursesToReindexQuery(TutorRole tutorRole){
        query(Course).where(courseWhereExpression())
                .and(classWhereExpression())
                .and(Course.COURSE_CLASSES.dot(CourseClass.TUTOR_ROLES).dot(TutorRole.ID_PK_COLUMN).eq(tutorRole.id))
    }
}
