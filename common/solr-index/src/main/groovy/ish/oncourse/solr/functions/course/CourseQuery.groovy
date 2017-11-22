package ish.oncourse.solr.functions.course

import ish.oncourse.model.Course
import ish.oncourse.model.CourseClass
import ish.oncourse.model.Room
import ish.oncourse.model.Session
import ish.oncourse.model.Site
import ish.oncourse.model.Tag
import ish.oncourse.model.Taggable
import ish.oncourse.model.TaggableTag
import ish.oncourse.model.Tutor
import ish.oncourse.model.TutorRole
import org.apache.cayenne.exp.ExpressionFactory
import org.apache.cayenne.query.ObjectSelect

import static org.apache.cayenne.query.ObjectSelect.query

/**
 * Created by alex on 11/20/17.
 */
class CourseQuery {
    static final ObjectSelect<Course> byTutor(Tutor tutor) {
        query(Course).where(Course.COURSE_CLASSES.dot(CourseClass.TUTOR_ROLES).dot(TutorRole.TUTOR).eq(tutor))
    }

    static final ObjectSelect<Course> bySessionRoom(Room room) {
        query(Course).where(Course.COURSE_CLASSES.dot(CourseClass.SESSIONS).dot(Session.ROOM).eq(room))
    }

    static final ObjectSelect<Course> byCourseClassRoom(Room room) {
        query(Course).where(Course.COURSE_CLASSES.dot(CourseClass.ROOM).eq(room))
    }

    static final ObjectSelect<Course> bySessionSite(Site site) {
        query(Course).where(Course.COURSE_CLASSES.dot(CourseClass.SESSIONS).dot(Session.ROOM).dot(Room.SITE).eq(site))
    }

    static final ObjectSelect<Course> byCourseClassSite(Site site) {
        query(Course).where(Course.COURSE_CLASSES.dot(CourseClass.ROOM).dot(Room.SITE).eq(site))
    }

    static final ObjectSelect<Taggable> courseTaggableByTag(Tag tag) {
        query(Taggable).where(Taggable.ENTITY_IDENTIFIER.eq("Course"))
                .and(Taggable.TAGGABLE_TAGS.dot(TaggableTag.TAG).eq(tag))
    }

    static final ObjectSelect<Course> byTaggable(List<Taggable> taggables){
        List<Long> ids = taggables.collect {t -> t.entityWillowId}
        query(Course).where(ExpressionFactory.inDbExp(Course.ID_PK_COLUMN, ids))
    }
}
