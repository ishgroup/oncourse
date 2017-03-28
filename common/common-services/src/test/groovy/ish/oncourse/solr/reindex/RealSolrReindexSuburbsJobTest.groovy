package ish.oncourse.solr.reindex

import ish.oncourse.solr.functions.suburb.Functions
import org.apache.cayenne.configuration.CayenneRuntime
import org.apache.solr.client.solrj.SolrQuery
import org.apache.solr.client.solrj.impl.CloudSolrClient

import static ish.oncourse.services.Functions.cayenneRuntime

class RealSolrReindexSuburbsJobTest {

    void test() {
        CayenneRuntime runtime = cayenneRuntime()
        CloudSolrClient client = new CloudSolrClient("127.0.0.1:2181,127.0.0.1:2182,127.0.0.1:2183")

        Functions.getSolrSuburbs(runtime.newContext()).each {
            client.addBean("suburbs-local", it)
        }
        client.commit("suburbs-local")

        println client.query("suburbs-local", new SolrQuery("*:*")).getResults()
    }
}
