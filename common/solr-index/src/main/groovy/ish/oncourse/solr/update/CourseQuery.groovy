package ish.oncourse.solr.update

import ish.oncourse.model.*
import org.apache.cayenne.query.ObjectSelect

import static org.apache.cayenne.query.ObjectSelect.query

/**
 * Created by alex on 2/6/18.
 */
class CourseQuery {    
    static final ObjectSelect<Course> coursesToReindexQuery(Contact contact){
        query(Course).where(Course.IS_WEB_VISIBLE.isTrue()) & 
                Course.COURSE_CLASSES.dot(CourseClass.TUTOR_ROLES).dot(TutorRole.TUTOR).dot(Tutor.CONTACT).eq(contact)
    }

    static final ObjectSelect<Course> coursesToReindexQuery(Tutor tutor){
        query(Course).where(Course.IS_WEB_VISIBLE.isTrue()) & 
                Course.COURSE_CLASSES.dot(CourseClass.TUTOR_ROLES).dot(TutorRole.TUTOR).eq(tutor)
    }

    static final ObjectSelect<Course> coursesToReindexQuery(TutorRole tutorRole){
        query(Course).where(Course.IS_WEB_VISIBLE.isTrue()) & 
                Course.COURSE_CLASSES.dot(CourseClass.TUTOR_ROLES).dot(TutorRole.ID_PK_COLUMN).eq(tutorRole)
    }

    static final ObjectSelect<Course> coursesToReindexQuery(CourseClass courseClass){
        query(Course).where(Course.IS_WEB_VISIBLE.isTrue()) & 
                Course.COURSE_CLASSES.dot(CourseClass.ID_PK_COLUMN).eq(courseClass)
    }
    
    static final ObjectSelect<Course> coursesToReindexQuery(Session session){
        query(Course).where(Course.IS_WEB_VISIBLE.isTrue()) &
                Course.COURSE_CLASSES.dot(CourseClass.SESSIONS).dot(Session.ID_PK_COLUMN).eq(session)
    }

    static final ObjectSelect<Course> coursesToReindexQuery(Site site){
        (query(Course).where(Course.IS_WEB_VISIBLE.isTrue()) &
                Site.IS_WEB_VISIBLE.isTrue()) & 
                Course.COURSE_CLASSES.dot(CourseClass.SESSIONS).dot(Session.ROOM).dot(Room.SITE).eq(site).orExp(Course.COURSE_CLASSES.dot(CourseClass.ROOM).dot(Room.SITE).eq(site))
    }

    static final ObjectSelect<Course> coursesToReindexQuery(Room room){
        query(Course).where(Course.IS_WEB_VISIBLE.isTrue()) & 
                Course.COURSE_CLASSES.dot(CourseClass.SESSIONS).dot(Session.ROOM).eq(room).orExp(Course.COURSE_CLASSES.dot(CourseClass.ROOM).eq(room))
    }
}
