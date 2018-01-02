package ish.oncourse.solr.functions.tag

import ish.oncourse.model.Tag
import ish.oncourse.test.TestContext
import ish.oncourse.test.context.CCollege
import ish.oncourse.test.context.DataContext
import org.junit.After
import org.junit.Before
import org.junit.Test

import static org.junit.Assert.*

/**
 * User: akoiro
 * Date: 10/11/17
 */
class TagsQueryTest {
    private TestContext testContext

    private DataContext dataContext


    @Before
    void before() {

        testContext = new TestContext()
        testContext.open()
        dataContext = new DataContext(objectContext: testContext.serverRuntime.newContext())
    }

    @Test
    void test() {
        CCollege collegeContext = dataContext.newCollege()

        collegeContext.tag("Tag1")
        collegeContext.tag("Tag12")
        collegeContext.tag("Tag11Invisible", false)
        collegeContext.tag("Tag2")

        collegeContext.addTag("Tag1", "Tag11Invisible", "Tag12")
        collegeContext.cCourse("Course1")
        collegeContext.tagCourse("COURSE1", "Tag2")
        collegeContext.tagCourse("COURSE1", "Tag11Invisible")
        collegeContext.tagCourse("COURSE1", "Tag12")

        List<Tag> actualTags = Functions.TagsQuery.select(testContext.getRuntime().newContext())

        assertEquals(2, actualTags.size())
        assertNotNull(actualTags.find {tag -> (tag.name == "Tag2") })
        assertNotNull(actualTags.find {tag -> (tag.name == "Tag12") })
        assertNull("only VISIBLE tags can be selected", actualTags.find {tag -> (tag.name == "Tag11Invisible") })
        assertNull("only tags, related to course, can be selected", actualTags.find {tag -> (tag.name == "Tag1") })
    }


    @After
    void after() {
        testContext.close()
    }
}
