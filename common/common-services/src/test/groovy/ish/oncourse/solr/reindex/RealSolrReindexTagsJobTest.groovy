package ish.oncourse.solr.reindex

import ish.oncourse.solr.functions.tag.Functions
import org.apache.cayenne.configuration.CayenneRuntime
import org.apache.solr.client.solrj.SolrQuery
import org.apache.solr.client.solrj.impl.CloudSolrClient

import static ish.oncourse.services.Functions.getCayenneRuntime

class RealSolrReindexTagsJobTest {

    void test() {
        CayenneRuntime runtime = cayenneRuntime()
        CloudSolrClient client = new CloudSolrClient("127.0.0.1:2181,127.0.0.1:2182,127.0.0.1:2183")

        Functions.getSolrTags(runtime.newContext()).each {
            client.addBean("tags-local", it)
        }
        client.commit("tags-local")

        println client.query("tags-local", new SolrQuery("*:*")).getResults()
    }
}
