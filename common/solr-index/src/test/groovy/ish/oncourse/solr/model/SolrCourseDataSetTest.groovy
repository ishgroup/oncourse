package ish.oncourse.solr.model

import ish.oncourse.test.LoadDataSet
import ish.oncourse.test.TestContext
import org.junit.After
import org.junit.Before
import org.junit.Test

/**
 * User: akoiro
 * Date: 9/11/17
 */
class SolrCourseDataSetTest {
    private TestContext context = new TestContext()

    @Before
    void before() {
        context.shouldCreateTables(true)
        context.open()
        new LoadDataSet().dataSetFile("ish/oncourse/solr/model/SolrCourseDataSetTest.xml").load(context.getDS())
    }

    @Test
    void test() {

    }


    @After
    void after() {
        context.close()
    }

}
