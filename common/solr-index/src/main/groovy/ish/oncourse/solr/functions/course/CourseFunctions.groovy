package ish.oncourse.solr.functions.course

import ish.oncourse.model.*
import ish.oncourse.solr.model.SCourse
import org.apache.cayenne.ObjectContext
import org.apache.cayenne.ResultIterator
import org.apache.cayenne.query.ObjectSelect

import static ish.oncourse.model.auto._CourseClass.*
import static org.apache.cayenne.query.ObjectSelect.query

class CourseFunctions {

    static final ObjectSelect<CourseClass> courseClassQuery(CourseContext context) {
        query(CourseClass).where(COURSE.eq(context.course))
                .and(IS_WEB_VISIBLE.eq(true))
                .and(CANCELLED.eq(false))
                .and(END_DATE.gt(context.current).andExp(IS_DISTANT_LEARNING_COURSE.eq(false)).orExp(IS_DISTANT_LEARNING_COURSE.eq(true))
        ).orderBy(START_DATE.asc())
    }

    static final ObjectSelect<Tag> tagsQuery(Course course){
        query(Tag).where(Tag.TAGGABLE_TAGS.dot(TaggableTag.TAGGABLE).dot(Taggable.ENTITY_IDENTIFIER).eq("Course"))
                .and(Tag.TAGGABLE_TAGS.dot(TaggableTag.TAGGABLE).dot(Taggable.ENTITY_WILLOW_ID).eq(course.id))
    }



    public static final Closure<ResultIterator<Course>> Courses = { ObjectContext context ->
        query(Course).iterator(context)
    }


    public static final Closure<ResultIterator<CourseClass>> CourseClasses = { CourseContext context ->
        courseClassQuery(context).iterator(context.context)
    }


    public static final Closure<ResultIterator<Tag>> Tags = { Course course ->
        tagsQuery(course).iterator(course.objectContext)
    }

    public static final Closure<SCourse> BuildSCourse = { Course course ->
        return new SCourse().with {
            it.id = course.id
            it.collegeId = course.college.id
            it.code = course.code
            it.name = course.name
            it.detail = course.detail
            it
        }
    }

}
