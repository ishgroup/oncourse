package ish.oncourse.willow.search

import com.google.inject.Inject
import org.apache.solr.client.solrj.SolrClient

class SearchService {


    SolrClient client

    @Inject
    SearchService() {

    }
}