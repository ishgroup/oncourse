package ish.oncourse.test.context

import ish.oncourse.model.*
import org.apache.cayenne.ObjectContext

/**
 * User: akoiro
 * Date: 10/11/17
 */
class CCollege {
    ObjectContext objectContext
    College college

    Map<String, Tag> tags = new HashMap<>()
    Map<String, CCourse> cCourses = new HashMap<>()


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

    CCourse cCourse(String name){
        cCourse(name, name.toUpperCase())
    }

    CCourse cCourse(String name, String code) {
        CCourse cCourse = CCourse.instance(objectContext, college, name, code)
        cCourses.put(cCourse.course.code, cCourse)
        objectContext.commitChanges()
        cCourse
    }

    void tagCourse(String courseCode, String tagName) {
        tagCourse(cCourses[courseCode].course, tags[tagName])
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
