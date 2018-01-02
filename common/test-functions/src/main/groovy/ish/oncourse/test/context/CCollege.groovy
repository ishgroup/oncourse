package ish.oncourse.test.context

import com.github.javafaker.Faker
import ish.oncourse.model.*
import org.apache.cayenne.ObjectContext

/**
 * User: akoiro
 * Date: 10/11/17
 */
class CCollege {
    ObjectContext objectContext
    College college
    Faker faker = new Faker()

    Map<String, Tag> tags = new HashMap<>()
    Map<String, CCourse> cCourses = new HashMap<>()

    List<CWebSite> webSites = new LinkedList<>()

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

    CCourse newCourse(String name){
        newCourse(name, name.toUpperCase())
    }

    CCourse newCourse(String name, String code) {
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


    CWebSite newWebSite() {
        WebSite webSite = objectContext.newObject(WebSite)
        webSite.college = this.college
        webSite.name = faker.company().name()
        webSite.siteKey = webSite.name.substring(0, 3).toLowerCase()
        webSite.created = new Date()
        webSite.modified = new Date()
        objectContext.commitChanges()
        return new CWebSite(webSite: webSite)
    }

    CSite newSite() {
        return CSite.instance(objectContext, this.college)
    }
}
