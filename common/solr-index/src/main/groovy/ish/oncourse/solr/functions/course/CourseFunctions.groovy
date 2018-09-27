package ish.oncourse.solr.functions.course

import ish.oncourse.model.*
import ish.oncourse.solr.model.SCourse
import org.apache.cayenne.ObjectContext
import org.apache.cayenne.ResultIterator
import org.apache.cayenne.exp.ExpressionFactory
import org.apache.cayenne.query.ObjectSelect
import org.apache.commons.lang3.StringUtils

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
        query(Tag).where(Tag.TAGGABLE_TAGS.outer().dot(TaggableTag.TAGGABLE).outer().dot(Taggable.ENTITY_IDENTIFIER).eq("Course"))
                .and(Tag.TAGGABLE_TAGS.outer().dot(TaggableTag.TAGGABLE).outer().dot(Taggable.ENTITY_WILLOW_ID).eq(course.id))
    }



    public static final Closure<ResultIterator<Course>> Courses = { ObjectContext context ->
        query(Course).where(Course.IS_WEB_VISIBLE.isTrue()).iterator(context)
    }
    
    public static final Closure<ResultIterator<Course>> CoursesById = { ObjectContext context, Set<Long> ids ->
        query(Course).where(ExpressionFactory.inDbExp(Course.ID_PK_COLUMN, ids)).iterator(context)
    }

    public static final Closure<ResultIterator<Course>> CoursesByWebSite = { ObjectContext context, College college ->
        query(Course).where(Course.COLLEGE.eq(college)).and(Course.IS_WEB_VISIBLE.isTrue()).iterator(context)
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

    public static final Closure<String[]> SiteKeys = { Course course ->
        return course.college.webSites.findAll { availableByRootTag(course, it) }.collect {it.siteKey}.toArray(new String[0])
    }



    static availableByRootTag(Course course, WebSite webSites) {
        Tag rootTag = getTagByFullPath(webSites.coursesRootTagName, webSites.college)
        if (!rootTag) {
            return true
        }

        
        List<Tag> tags = query(Tag).where(Tag.TAGGABLE_TAGS.dot(TaggableTag.TAGGABLE).dot(Taggable.ENTITY_IDENTIFIER).eq(Course.simpleName))
                .and(Tag.TAGGABLE_TAGS.dot(TaggableTag.TAGGABLE).dot(Taggable.ENTITY_WILLOW_ID).eq(course.getId())).select(course.objectContext)

        return tags.any {tag -> rootTag.id == tag.id || rootTag.isParentOf(tag)}
        
    }


    private static Tag getTagByFullPath(String path, College college) {
        if (path == null) {
            return null
        }
        if (path.startsWith('/')) {
            path = path.replaceFirst('/', '')
        }
        if (path.endsWith('/')) {
            path = path.substring(0, path.length() - 1)
        }
        if (path == '') {
            return null
        }
        
        String[] tagNames = path.split('/')

        for (int j = 0; j < tagNames.length; j++) {
            try {
                tagNames[j] = URLDecoder.decode(tagNames[j], 'UTF-8')
            } catch (UnsupportedEncodingException e) {
                e.printStackTrace()
            }
        }
        Tag rootTag = null
        Tag subjectsTag = query(Tag).where(Tag.COLLEGE.eq(college))
                .and(Tag.IS_WEB_VISIBLE.isTrue())
                .and(Tag.NAME.eq(Tag.SUBJECTS_TAG_NAME))
                .selectFirst(college.objectContext)
        
        int i = 0
        if (tagNames[0].equalsIgnoreCase(Tag.SUBJECTS_TAG_NAME)) {
            rootTag = subjectsTag
            i = 1
        }
        if (!rootTag) {
            if (subjectsTag != null && subjectsTag.hasChildWithName(tagNames[0])) {
                rootTag = subjectsTag
            } else {
                rootTag = query(Tag).where(Tag.COLLEGE.eq(college))
                        .and(Tag.IS_WEB_VISIBLE.isTrue())
                        .and(Tag.NAME.eq(tagNames[0]))
                        .and(Tag.IS_TAG_GROUP.isTrue())
                        .selectFirst(college.objectContext)
                //don't need to process tag group if we have it
                i = 1
            }
        }

        if (!rootTag) {
            return null
        }
        for (; i < tagNames.length; i++) {
            Tag tag = rootTag.getChildWithName(tagNames[i])
            if (tag == null) {
                return null
            } else {
                rootTag = tag
            }

        }

        return rootTag
    }
}
