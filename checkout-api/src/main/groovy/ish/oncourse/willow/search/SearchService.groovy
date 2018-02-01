package ish.oncourse.willow.search

import com.google.inject.Inject
import ish.oncourse.configuration.Configuration
import ish.oncourse.willow.model.common.Item
import ish.oncourse.willow.model.field.Suburb
import org.apache.solr.client.solrj.SolrQuery
import org.apache.solr.client.solrj.impl.CloudSolrClient
import org.apache.solr.client.solrj.response.QueryResponse
import org.slf4j.Logger
import org.slf4j.LoggerFactory

import javax.ws.rs.InternalServerErrorException
import java.util.concurrent.Callable
import java.util.concurrent.ExecutorService
import java.util.concurrent.Executors
import java.util.concurrent.Future
import java.util.concurrent.TimeUnit

import static ish.oncourse.configuration.Configuration.AppProperty.ZK_HOST

class SearchService {

    private static final String SOLR_SYNTAX_CHARACTERS_STRING = '[\\!\\^\\(\\)\\{\\}\\[\\]\\:\"\\?\\+\\~\\*\\|\\&\\;\\\\]'

    private static final int ZK_TIMEOUT = 1000

    final static Logger logger = LoggerFactory.getLogger(SearchService.class)

    private String zkHost

    @Inject
    SearchService() {
        zkHost = Configuration.getValue(ZK_HOST)
        if (zkHost == null) {
            throw new IllegalStateException('Zookeeper host property undefined')
        }
    }


    private QueryResponse searchSuburbs(String qualifier, String value) {
        CloudSolrClient client
        ExecutorService executorService = Executors.newCachedThreadPool()

        try {
            value = normalizeString(value)
            
            SolrQuery q = new SolrQuery()
            q.query = "doctype:suburb AND $qualifier:$value*"

            long time = System.currentTimeMillis()
           

            Future<QueryResponse> searchFuture = executorService.submit({
                client = new CloudSolrClient.Builder().withZkHost(zkHost).build()
                client.setZkClientTimeout(ZK_TIMEOUT)
                client.setZkConnectTimeout(ZK_TIMEOUT)
                return client.query('suburbs', q)
            } as Callable<QueryResponse>)
            QueryResponse response = searchFuture.get(ZK_TIMEOUT, TimeUnit.MILLISECONDS)

            logger.info("Query finished. Time: '{}' ms", (System.currentTimeMillis() - time))
            response

        } catch (Exception e) {
            logger.error("Failed to search suburbs.", e)
            throw new InternalServerErrorException(e)
        } finally {
            executorService.shutdown()
            if (client) {
                client.close()
            }
        }
    }

    List<Item> searchSuburbsByName(String term) {
        serealize(searchSuburbs('suburb', term))
    }

    List<Item> searchSuburbsByPostcode(String term) {
        serealize(searchSuburbs('postcode', term))
    }

    List<Item> serealize(QueryResponse response) {
        List<Item> result = []
        
        response.results.each {
            doc ->
            result << new Item().with { i ->
                String postcode = doc.getFieldValue('postcode') as String
                String state = doc.getFieldValue('state') as String
                String suburb = doc.getFieldValue('suburb') as String
                i.key = postcode
                i.value = new Suburb(postcode: postcode, state: state, suburb: suburb)
                i
            }
        }
        result
    }

    static String normalizeString(String original) {
        return original.toLowerCase().trim().replaceAll(SOLR_SYNTAX_CHARACTERS_STRING, ' ')
    }
}