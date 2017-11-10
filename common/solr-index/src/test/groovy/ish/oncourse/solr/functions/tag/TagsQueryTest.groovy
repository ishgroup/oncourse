package ish.oncourse.solr.functions.tag

import ish.oncourse.solr.model.CollegeContext
import ish.oncourse.solr.model.DataContext
import ish.oncourse.test.TestContext
import org.apache.cayenne.configuration.server.ServerRuntime
import org.junit.After
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
        CollegeContext collegeContext = dataContext.college("College-Australia/Sydney", "Australia/Sydney")
        collegeContext.tag("Tag1")
        collegeContext.tag("Tag11")
        collegeContext.tag("Tag12")
        collegeContext.addTag("Tag1", "Tag11", "Tag12")
        collegeContext.course("Course1")
        collegeContext.tagCourse("COURSE1", "Tag12")
    }

    @Test
    void test() {
        Functions.TagsQuery.iterator(serverRuntime.newContext()).each { println(it) }
    }


    @After
    void after() {
        testContext.close()
    }

}
