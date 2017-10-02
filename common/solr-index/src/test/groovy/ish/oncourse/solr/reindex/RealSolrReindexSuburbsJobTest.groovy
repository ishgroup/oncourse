package ish.oncourse.solr.reindex

import ish.oncourse.solr.functions.suburb.Functions
import org.apache.cayenne.configuration.CayenneRuntime
import org.apache.solr.client.solrj.SolrQuery
import org.apache.solr.client.solrj.impl.CloudSolrClient
import org.apache.solr.common.SolrDocumentList

import static Functions.cayenneRuntime
import static org.junit.Assert.*

class RealSolrReindexSuburbsJobTest {

    private static final String zkHost = '127.0.0.1:2181,127.0.0.1:2182'
    private static final String collectionName = 'suburbs-local'
    private static final String allRecordsQuery = '*:*'
    private static final Integer maxRows = 100000
    private static final String solrFullImportURL = "http://localhost:7001/solr/${collectionName}/dataimport?command=full-import".toString()
    private static final Long sleepingTime = 5000L

    private CloudSolrClient client
    private CayenneRuntime runtime
    
    void test() {
        CayenneRuntime runtime = cayenneRuntime()
        CloudSolrClient client = new CloudSolrClient(zkHost)

        Functions.getSolrSuburbs(runtime.newContext()).each {
            client.addBean(collectionName, it)
        }
        client.commit(collectionName)

        client.query(collectionName, new SolrQuery(allRecordsQuery)).getResults().each { r ->
            println r
        }
    }
    
    void testFullIntegration() {
        client = new CloudSolrClient(zkHost)
        runtime = cayenneRuntime()

        cleanCollection()

        doFullImport()

        SolrDocumentList baseCollection = getCollection()

        cleanCollection()

        doReindex()

        SolrDocumentList reindexedCollection = getCollection()

        compareCollections(baseCollection, reindexedCollection)
    }


    private SolrDocumentList getCollection() {
        client.query(collectionName, new SolrQuery(allRecordsQuery).setRows(maxRows)).getResults()
    }

    private void cleanCollection() {
        client.deleteByQuery(collectionName, allRecordsQuery)
        client.commit(collectionName)

        assertTrue(getCollection().empty)
    }

    private void doFullImport() {
        solrFullImportURL.toURL().text

        sleep(sleepingTime)

        assertFalse(getCollection().empty)
    }


    private void doReindex() {
        Functions.getSolrSuburbs(runtime.newContext()).each {
            client.addBean(collectionName, it)
        }

        client.commit(collectionName)

        assertFalse(getCollection().empty)
    }

    private void compareCollections(SolrDocumentList c1, SolrDocumentList c2) {
        assertEquals(c1.size(), c2.size())
        
        c1.sort { it.id }
        c2.sort { it.id }
        
        for(int i = 0; i < c1.size(); i++) {
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
