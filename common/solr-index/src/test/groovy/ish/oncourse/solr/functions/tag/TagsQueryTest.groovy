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
        List<Tag> allTags = new ArrayList<>()
        CollegeContext collegeContext = dataContext.college("College-Australia/Sydney", "Australia/Sydney")
        allTags.add(collegeContext.tag("Tag1"))
        allTags.add(collegeContext.tag("Tag2"))
        allTags.add(collegeContext.tag("Tag11", false))
        allTags.add(collegeContext.tag("Tag12"))

        collegeContext.addTag("Tag1", "Tag11", "Tag12")
        collegeContext.course("Course1")
        collegeContext.tagCourse("COURSE1", "Tag2")
        collegeContext.tagCourse("COURSE1", "Tag11")
        collegeContext.tagCourse("COURSE1", "Tag12")

        List<Tag> expectedTags = allTags.findAll {t -> t.name == "Tag2" || t.name == "Tag12" }
        List<Tag> actualTags = Functions.TagsQuery.select(serverRuntime.newContext())

        Assert.assertTrue(expectedTags.size() == actualTags.size())
        for (int i = 0; i < expectedTags.size(); i++)
            Assert.assertTrue(expectedTags.get(i).name.equals(actualTags.get(i).name))
    }


    @After
    void after() {
        testContext.close()
    }

}
