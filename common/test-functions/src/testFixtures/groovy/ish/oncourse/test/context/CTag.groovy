package ish.oncourse.test.context

import com.github.javafaker.Faker
import ish.oncourse.model.Course
import ish.oncourse.model.Tag
import ish.oncourse.model.Taggable
import ish.oncourse.model.TaggableTag

/**
 * User: akoiro
 * Date: 2/3/18
 */
class CTag {
    private Faker faker = DataContext.faker
    CCollege college

    private Tag tag

    CTag name(String name) {
        tag.name = name
        tag.shortName = name
        return this
    }

    CTag webVisible(boolean visible) {
        tag.setIsWebVisible(visible)
        return this
    }

    CTag parent(CTag tag) {
        this.tag.parent = tag.tag
        return this
    }

    CTag children(CTag... child) {
        child.iterator().forEachRemaining {
            it.parent(this)
        }
        return this
    }

    CTag children(String... childName) {
        Arrays.stream(childName).map { n -> valueOf(college).name(n).parent(this) }.toArray()
        return this
    }

    CTag tagCourse(CCourse course) {
        Taggable taggable = college.objectContext.newObject(Taggable)
        taggable.college = college.college
        taggable.entityWillowId = course.course.id
        taggable.entityIdentifier = Course.class.simpleName

        TaggableTag taggableTag = college.objectContext.newObject(TaggableTag)
        taggableTag.college = college.college
        taggableTag.taggable = taggable
        taggableTag.tag = this.tag
        return this
    }


    CTag commit() {
        college.objectContext.commitChanges()
        return this
    }

    static CTag valueOf(CCollege college, Tag tag) {
        CTag cTag = new CTag()
        cTag.college = college
        cTag.tag = tag
        return cTag
    }

    static CTag valueOf(CCollege college) {
        CTag cTag = new CTag()
        cTag.college = college
        cTag.tag = college.objectContext.newObject(Tag)
        cTag.tag.college = college.college
        cTag.tag.name = cTag.faker.name().username()
        cTag.tag.shortName = cTag.faker.name().username()
        cTag.tag.isWebVisible = true
        cTag.tag.isTagGroup = false
        return cTag
    }

}
