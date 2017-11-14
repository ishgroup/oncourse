package ish.oncourse.solr.model

import ish.common.types.CourseEnrolmentType
import ish.oncourse.model.*
import org.apache.cayenne.ObjectContext

/**
 * User: akoiro
 * Date: 10/11/17
 */
class CollegeContext {
    ObjectContext objectContext
    College college

    Map<String, Tag> tags = new HashMap<>()
    Map<String, Course> courses = new HashMap<>()


    Tag tag(String name, boolean webVisible = true) {
        Tag tag = objectContext.newObject(Tag)
        tag.name = name
        tag.isWebVisible = webVisible
        tag.college = objectContext.localObject(college)
        objectContext.commitChanges()
        tags.put(tag.name, tag)
        tag
    }

    void addTag(String parent, String... child) {
        child.each {
            addTag(tags[parent], tags[it])
        }
    }

    void addTag(Tag parent, Tag... child) {
        child.each {
            it.parent = parent
            objectContext.commitChanges()
        }
    }

    Course course(String name) {
        course(name, name.toUpperCase())
    }

    Course course(String name, String code) {
        Course course = objectContext.newObject(Course)
        course.college = objectContext.localObject(college)
        course.name = name
        course.code = code
        course.enrolmentType = CourseEnrolmentType.OPEN_FOR_ENROLMENT
        courses.put(course.code, course)
        objectContext.commitChanges()
        course
    }

    void tagCourse(String courseCode, String tagName) {
        tagCourse(courses[courseCode], tags[tagName])
    }

    void tagCourse(Course course, Tag tag) {
        Taggable taggable = objectContext.newObject(Taggable)
        taggable.college = course.college
        taggable.entityWillowId = course.id
        taggable.entityIdentifier = Course.class.simpleName

        TaggableTag taggableTag = objectContext.newObject(TaggableTag)
        taggableTag.college = course.college
        taggableTag.taggable = taggable
        taggableTag.tag = objectContext.localObject(tag)
        objectContext.commitChanges()
    }

}
