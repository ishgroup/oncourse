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
        return objectContext.newObject(Tag).with {
            it.name = name
            it.isWebVisible = webVisible
            it.college = it.objectContext.localObject(college)
            tags.put(it.name, it)
            objectContext.commitChanges()
            it
        }
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
        objectContext.newObject(Course).with {
            it.college = college
            it.name = name
            it.code = code
            it.enrolmentType = CourseEnrolmentType.OPEN_FOR_ENROLMENT
            courses.put(it.code, it)
            objectContext.commitChanges()
            it
        }
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
