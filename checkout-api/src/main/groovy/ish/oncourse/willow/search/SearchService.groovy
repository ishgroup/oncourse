package ish.oncourse.willow.search

import com.google.inject.Inject
import ish.oncourse.configuration.Configuration
import ish.oncourse.willow.model.common.Item
import ish.oncourse.willow.model.field.Suburb
import org.apache.solr.client.solrj.SolrClient
import org.apache.solr.client.solrj.SolrQuery
import org.apache.solr.client.solrj.impl.CloudSolrClient
import org.slf4j.Logger
import org.slf4j.LoggerFactory

import javax.ws.rs.InternalServerErrorException

import static ish.oncourse.configuration.Configuration.AppProperty.ZK_HOST

class SearchService {

    private static final String SOLR_SYNTAX_CHARACTERS_STRING = '[\\!\\^\\(\\)\\{\\}\\[\\]\\:\"\\?\\+\\~\\*\\|\\&\\;\\\\]'

    private static final int ZK_TIMEOUT = 1000
    
    final static  Logger logger = LoggerFactory.getLogger(SearchService.class)

    CloudSolrClient client

    @Inject
    SearchService() {
        String zkHost = Configuration.getValue(ZK_HOST)

        if (zkHost == null) {
            throw new IllegalStateException('Zookeeper host property undefined')
        }
        client = new CloudSolrClient.Builder().withZkHost(zkHost).build()
        
        client.setZkClientTimeout(ZK_TIMEOUT)
        client.setZkConnectTimeout(ZK_TIMEOUT)
    }


    private List<Item> searchSuburbs(String qualifier, String value) {
        try {

            value = normalizeString(value)
            List<Item> result = []

            SolrQuery q = new SolrQuery()
            q.query = "doctype:suburb AND $qualifier:$value*"

            client.query('suburbs', q).results.each { doc ->
                result << new Item().with { i ->
                    String postcode = doc.getFieldValue('postcode') as String
                    String state = doc.getFieldValue('state') as String
                    String suburb = doc.getFieldValue('suburb') as String
                    i.key = postcode
                    i.value = new Suburb(postcode:postcode, state: state, suburb: suburb)
                    i
                }
            }

            result
            
        } catch (Exception e) {
            logger.error("Failed to search suburbs.", e)
            throw new InternalServerErrorException(e)
        }
    }
    
    List<Item> searchSuburbsByName(String term) {
        searchSuburbs('suburb', term)
    }

    List<Item> searchSuburbsByPostcode(String term) { 
        searchSuburbs('postcode', term)
    }

    static String normalizeString(String original) {
        return original.toLowerCase().trim().replaceAll(SOLR_SYNTAX_CHARACTERS_STRING, ' ')
    }
}