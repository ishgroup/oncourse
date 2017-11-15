package ish.oncourse.solr.functions.tag

import ish.oncourse.model.Tag
import ish.oncourse.solr.model.CollegeContext
import ish.oncourse.solr.model.DataContext
import ish.oncourse.test.TestContext
import org.apache.cayenne.configuration.server.ServerRuntime
import org.junit.After
import org.junit.Assert
import org.junit.Before
import org.junit.Test

import static ish.oncourse.test.functions.Functions.createRuntime
import static org.junit.Assert.*

/**
 * User: akoiro
 * Date: 10/11/17
 */
class TagsQueryTest {
    private TestContext testContext
    private ServerRuntime serverRuntime

    private DataContext dataContext


    @Before
    void before() {

        testContext = new TestContext()
        testContext.open()
        serverRuntime = createRuntime()
        dataContext = new DataContext(objectContext: serverRuntime.newContext())
    }

    @Test
    void test() {
        CollegeContext collegeContext = dataContext.college("College-Australia/Sydney", "Australia/Sydney")

        collegeContext.tag("Tag1")
        collegeContext.tag("Tag12")
        collegeContext.tag("Tag11Invisible", false)
        collegeContext.tag("Tag2")

        collegeContext.addTag("Tag1", "Tag11Invisible", "Tag12")
        collegeContext.course("Course1")
        collegeContext.tagCourse("COURSE1", "Tag2")
        collegeContext.tagCourse("COURSE1", "Tag11Invisible")
        collegeContext.tagCourse("COURSE1", "Tag12")

        List<Tag> actualTags = Functions.TagsQuery.select(serverRuntime.newContext())

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
