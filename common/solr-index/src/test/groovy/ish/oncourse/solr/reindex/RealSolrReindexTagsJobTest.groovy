package ish.oncourse.solr.reindex

import ish.oncourse.solr.functions.tag.Functions
import ish.oncourse.solr.model.STag
import org.apache.logging.log4j.LogManager
import org.apache.logging.log4j.Logger
import org.apache.solr.common.SolrDocumentList
import org.junit.Before
import org.junit.Ignore
import org.junit.Test

import static org.junit.Assert.*

class RealSolrReindexTagsJobTest {
    private static final Logger logger = LogManager.getLogger()


    private RealSolrReindexTemplate<STag> template

    @Before
    void before() {
        template = RealSolrReindexTemplate.valueOf('tags-local', Functions.getSolrTags, compareCollections)
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
            assertNotNull(c1[i].collegeId)
            assertEquals('collegeId assertion', c1[i].collegeId, c2[i].collegeId)
            assertNotNull(c1[i].doctype)
            assertEquals('doctype assertion', c1[i].doctype, c2[i].doctype)
            assertNotNull(c1[i].name)
            assertEquals('name assertion', c1[i].name, c2[i].name)

            assertNotEquals(c1[i]._version_, c2[i]._version_)
        }
    }
}
