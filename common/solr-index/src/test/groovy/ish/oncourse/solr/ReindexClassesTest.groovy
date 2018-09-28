package ish.oncourse.solr

import ish.oncourse.solr.model.SCourseClass
import ish.oncourse.solr.query.SearchParams
import ish.oncourse.solr.query.SolrQueryBuilder
import ish.oncourse.solr.reindex.ReindexClasses
import ish.oncourse.test.context.CWebSite
import org.apache.solr.client.solrj.SolrClient
import org.apache.solr.client.solrj.SolrQuery
import org.apache.solr.client.solrj.SolrServerException
import org.apache.solr.client.solrj.embedded.EmbeddedSolrServer
import org.apache.solr.common.SolrDocumentList
import org.apache.solr.common.params.DefaultSolrParams
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
        
        cCollege.newCourse('course1').newCourseClassWithSessions('class1', 1).build()
        cCollege.newCourse('course1').newCourseClassWithSessions('class2', 2).build()
        cCollege.newCourse('course1').newCourseClassWithSessions('class3', 3).build()

        cCollege.newCourse('course2').newCourseClassWithSessions('class1', 4).build()
        cCollege.newCourse('course3').newCourseClassWithSessions('class1', 5).build()
        


        ReindexClasses job = new ReindexClasses(objectContext, solrClient, cWebSite.webSite)
        job.run()
        SolrQuery query = new SolrQuery('*:*')
        query.setSort('startDate',SolrQuery.ORDER.asc )
        SolrDocumentList actualSClasses = solrClient.query("classes",  new SolrQuery('*:*')).results
        
        assertEquals(5, actualSClasses.size())
        assertEquals('COURSE1-class1', actualSClasses[0].get('code'))
        assertEquals('COURSE1-class2', actualSClasses[1].get('code'))
        assertEquals('COURSE1-class3', actualSClasses[2].get('code'))
        assertEquals('COURSE2-class1', actualSClasses[3].get('code'))
        assertEquals('COURSE3-class1', actualSClasses[4].get('code'))


        solrClient.close()
    }
}
