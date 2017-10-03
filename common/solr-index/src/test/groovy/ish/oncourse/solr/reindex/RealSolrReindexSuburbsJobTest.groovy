package ish.oncourse.solr.reindex

import ish.oncourse.solr.functions.suburb.Functions
import org.apache.solr.common.SolrDocumentList
import org.junit.Before
import org.junit.Ignore
import org.junit.Test

import static org.junit.Assert.*

class RealSolrReindexSuburbsJobTest {
    RealSolrReindexTemplate template


    @Before
    void before() {
        template = RealSolrReindexTemplate.valueOf('suburbs-local', Functions.getSolrSuburbs, compareCollections)
    }

    @Ignore
    @Test
    void test() {
        template.test()
    }

    @Ignore
    @Test
    void testFullIntegration() {
        template.testFullIntegration()
    }

    private static compareCollections = { SolrDocumentList c1, SolrDocumentList c2 ->
        assertEquals(c1.size(), c2.size())

        c1.sort { it.id }
        c2.sort { it.id }

        for (int i = 0; i < c1.size(); i++) {
            assertNotNull(c1[i].id)
            assertEquals('id assertion', c1[i].id, c2[i].id)
            assertNotNull(c1[i].doctype)
            assertEquals('doctype assertion', c1[i].doctype, c2[i].doctype)
            assertNotNull(c1[i].loc)
            assertEquals('loc assertion', c1[i].loc, c2[i].loc)
            assertNotNull(c1[i].postcode)
            assertEquals('postcode assertion', c1[i].postcode, c2[i].postcode)
            assertNotNull(c1[i].suburb)
            assertEquals('suburb assertion', c1[i].suburb, c2[i].suburb)
            assertNotNull(c1[i].state)
            assertEquals('state assertion', c1[i].state, c2[i].state)

            assertNotEquals(c1[i]._version_, c2[i]._version_)
        }
    }
}
