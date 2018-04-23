package ish.oncourse.solr

import com.github.javafaker.Faker
import ish.oncourse.solr.model.SCourse
import ish.oncourse.solr.query.SolrQueryBuilder
import org.apache.solr.SolrTestCaseJ4
import org.apache.solr.client.solrj.SolrClient
import org.apache.solr.client.solrj.SolrQuery
import org.apache.solr.client.solrj.embedded.EmbeddedSolrServer
import org.junit.Before
import org.junit.Test

import java.util.concurrent.TimeUnit

class SpatialSolrQueryTest extends SolrTestCaseJ4 {
    static {
        InitSolr.INIT_STATIC_BLOCK()
    }

    private InitSolr initSolr
    private SolrClient solrClient
    private Faker faker = new Faker()

    @Before
    public void before() throws Exception {
        initSolr = InitSolr.coursesCore()
        initSolr.init()
        solrClient = new EmbeddedSolrServer(h.getCore())
    }

    @Test
    void test() {

        String center = "53.899919,27.556928"
        String km10 = "53.859853,27.692726"
        String km20 = "53.739605,27.694885"
        SCourse course10km = createCourse(km10)
        SCourse course20km = createCourse(km20)
        solrClient.addBean(course10km)
        solrClient.addBean(course20km)
        solrClient.commit()

        List<SCourse> result = solrClient.query(new SolrQuery("{!geofilt sfield=course_loc pt=${center} d=${5 / SolrQueryBuilder.KM_IN_DEGREE_VALUE}}")).getBeans(SCourse.class)
        assertTrue(result.isEmpty())

        result = solrClient.query(new SolrQuery("{!geofilt sfield=course_loc pt=${center} d=${10 / SolrQueryBuilder.KM_IN_DEGREE_VALUE}}")).getBeans(SCourse.class)
        assertEquals(1, result.size())
        assertEquals(km10, result.get(0).location.get(0))

        result = solrClient.query(new SolrQuery("{!geofilt sfield=course_loc pt=${center} d=${20 / SolrQueryBuilder.KM_IN_DEGREE_VALUE}}")).getBeans(SCourse.class)
        assertEquals(2, result.size())
        assertEquals(km10, result.get(0).location.get(0))
        assertEquals(km20, result.get(1).location.get(0))

    }

    private SCourse createCourse(String km10) {
        new SCourse().with {
            it.name = faker.book().title()
            it.classCode = [faker.color().name()]
            it.classStart = [faker.date().future(1, TimeUnit.HOURS)]
            it.classEnd = [faker.date().future(2, TimeUnit.HOURS)]
            it.code = faker.color().name()
            it.collegeId = 299
            it.detail = faker.book().author()
            it.id = faker.number().randomNumber()
            it.location = [km10]
            it.postcode = [faker.address().zipCode()]
            it.price = [faker.commerce().price()]
            it.siteId = [faker.number().randomNumber()]
            it.startDate = faker.date().future(1, TimeUnit.HOURS)
            it.suburb = [faker.address().stateAbbr()]
            it
        }
    }

}



