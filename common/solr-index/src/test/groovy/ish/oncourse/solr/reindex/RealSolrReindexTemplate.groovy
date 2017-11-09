package ish.oncourse.solr.reindex

import io.reactivex.Observable
import io.reactivex.disposables.Disposable
import ish.oncourse.test.MariaDB
import ish.oncourse.test.functions.Functions
import org.apache.cayenne.configuration.CayenneRuntime
import org.apache.logging.log4j.LogManager
import org.apache.logging.log4j.Logger
import org.apache.solr.client.solrj.SolrQuery
import org.apache.solr.client.solrj.impl.CloudSolrClient
import org.apache.solr.common.SolrDocumentList

import javax.sql.DataSource

import static ish.oncourse.test.functions.Functions.createRuntime
import static org.junit.Assert.assertFalse
import static org.junit.Assert.assertTrue

/**
 * User: akoiro
 * Date: 2/10/17
 */
class RealSolrReindexTemplate<E> {
    private static final Logger logger = LogManager.getLogger()

    String solrFullImportURL
    String collectionName


    String zkHost = '127.0.0.1:2181,127.0.0.1:2182'
    Long sleepingTime = 1 * 60 * 1000
    Integer maxRows = 100000
    String allRecordsQuery = '*:*'

    CloudSolrClient client
    CayenneRuntime runtime

    Closure compareCollections
    Closure<Observable<E>> getEntities


    void test() {
        Functions.TimeLog timing = new Functions.TimeLog()
        Disposable disposable = getEntities(runtime.newContext()).subscribe({
            client.addBean(collectionName, it)
        })

        while (!disposable.isDisposed()) {
            Thread.sleep(100)
        }
        timing.log(logger, "Processing time:")

        client.commit(collectionName)
        timing.log(logger, "Committing time:")

        def result = client.query(collectionName,
                new SolrQuery(allRecordsQuery).setRows(maxRows))
                .getResults()
        timing.log(logger, "Query time:")

        println "All records count : ${result.size()}"
    }

    void testFullIntegration() {
        cleanCollection()

        doFullImport()

        SolrDocumentList baseCollection = getCollection()

        cleanCollection()

        doReindex()

        SolrDocumentList reindexedCollection = getCollection()

        compareCollections.call(baseCollection, reindexedCollection)
    }


    private SolrDocumentList getCollection() {
        client.query(collectionName,
                new SolrQuery(allRecordsQuery).setRows(maxRows)).getResults()
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
        getEntities.subscribe({
            client.addBean(collectionName, it)
        })
        client.commit(collectionName)
        assertFalse(getCollection().empty)
    }


    static <E> RealSolrReindexTemplate<E> valueOf(String collectionName,
                                                  Closure<Observable<E>> getEntities,
                                                  Closure compareCollections) {
        RealSolrReindexTemplate conf = new RealSolrReindexTemplate()
        conf.collectionName = collectionName
        conf.solrFullImportURL = "http://localhost:7001/solr/${collectionName}/dataimport?command=full-import".toString()
        conf.client = new CloudSolrClient(conf.zkHost)
        conf.compareCollections = compareCollections
        conf.getEntities = getEntities
        DataSource ds = Functions.createDS(MariaDB.valueOf())
        Functions.bindDS(ds)
        conf.runtime = createRuntime()
        return conf
    }


}
