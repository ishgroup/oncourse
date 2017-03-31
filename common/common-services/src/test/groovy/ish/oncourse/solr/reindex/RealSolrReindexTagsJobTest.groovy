package ish.oncourse.solr.reindex

import ish.oncourse.solr.functions.tag.Functions
import org.apache.cayenne.configuration.CayenneRuntime
import org.apache.solr.client.solrj.SolrQuery
import org.apache.solr.client.solrj.impl.CloudSolrClient
import org.apache.solr.common.SolrDocumentList
import org.junit.Test


import static ish.oncourse.services.Functions.getCayenneRuntime
import static org.junit.Assert.*

class RealSolrReindexTagsJobTest {
    
    private static final String zkHost = '127.0.0.1:2181,127.0.0.1:2182'
    private static final String collectionName = 'tags-local'
    private static final String allRecordsQuery = '*:*'
    private static final Integer maxRows = 100000
    private static final String solrFullImportURL = "http://localhost:7001/solr/${collectionName}/dataimport?command=full-import".toString()
    private static final Long sleepingTime = 5000L

    private static final String query = 'select distinct t.id, t.name as nm, \'tag\' as type, t.collegeId as collegeID from Tag t inner join TaggableTag tgt on tgt.tagId=t.id inner join Taggable tg on tgt.taggableId = tg.id where t.isWebVisible=1 and tg.entityIdentifier = \'Course\''
    
    private CloudSolrClient client
    private CayenneRuntime runtime

    void test() {
        CayenneRuntime runtime = cayenneRuntime()
        CloudSolrClient client = new CloudSolrClient(zkHost)

        Functions.getSolrTags(runtime.newContext()).each {
            client.addBean(collectionName, it)
        }
        
        client.commit(collectionName)
        
        def result = client.query(collectionName, new SolrQuery(allRecordsQuery).setRows(maxRows)).getResults()
        println "All records count : ${result.size()}"
        result.each { row ->
            println row
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
        Functions.getSolrTags(runtime.newContext()).each {
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
            assertEquals('id assertion', c1[i].id, c2[i].id)
            assertEquals('collegeId assertion', c1[i].collegeId, c2[i].collegeId)
            assertEquals('doctype assertion', c1[i].doctype, c2[i].doctype)
            assertEquals('name assertion', c1[i].name, c2[i].name)
        }
    }
}
