package ish.oncourse.solr

import ish.oncourse.solr.model.SCourseClass
import ish.oncourse.solr.reindex.ReindexClasses
import ish.oncourse.test.context.CWebSite
import org.apache.solr.client.solrj.SolrClient
import org.apache.solr.client.solrj.SolrServerException
import org.apache.solr.client.solrj.embedded.EmbeddedSolrServer
import org.junit.Test

class ReindexClassesTest extends ASolrTest {

    protected void initSolr() throws Exception {
        initSolr = InitSolr.classesCore()
        initSolr.init()
    }


    @Test
    void testReindexCourse() throws IOException, SolrServerException {
        SolrClient solrClient = new EmbeddedSolrServer(h.getCore())
        
        CWebSite cWebSite = cCollege.newWebSite('mamoth')
        
        cCollege.newCourse('course1').withClass('class1').withClass('class2').build()
        cCollege.newCourse('course2').withClass('class1').withClass('class2').build()
        cCollege.newCourse('course3').withClass('class1').withClass('class2').build()


        ReindexClasses job = new ReindexClasses(objectContext, solrClient, cWebSite.webSite)
        job.run()

        List<SCourseClass> actualSClasses = solrClient.query("classes", null)
                .getBeans(SCourseClass.class)
        
        solrClient.close()
    }
}
