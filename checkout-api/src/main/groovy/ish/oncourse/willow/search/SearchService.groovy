package ish.oncourse.willow.search

import com.google.inject.Inject
import ish.oncourse.configuration.Configuration
import ish.oncourse.solr.BuildSolrClient
import ish.oncourse.willow.model.common.Item
import ish.oncourse.willow.model.field.Suburb
import org.apache.solr.client.solrj.SolrClient
import org.apache.solr.client.solrj.SolrQuery
import org.apache.solr.client.solrj.response.QueryResponse
import org.slf4j.Logger
import org.slf4j.LoggerFactory

import javax.ws.rs.InternalServerErrorException

class SearchService {

    private static
    final String SOLR_SYNTAX_CHARACTERS_STRING = '[\\!\\^\\(\\)\\{\\}\\[\\]\\:\"\\?\\+\\~\\*\\|\\&\\;\\\\]'


    final static Logger logger = LoggerFactory.getLogger(SearchService.class)

    private SolrClient client

    SearchService(Properties properties) {
        this.client = BuildSolrClient.instance(properties).build()
    }

    @Inject
    SearchService() {
        this(Configuration.loadProperties())
    }


    private QueryResponse searchSuburbs(String qualifier, String value) {
        try {
            value = normalizeString(value)

            SolrQuery q = new SolrQuery()
            q.query = "doctype:suburb AND $qualifier:$value*"

            long time = System.currentTimeMillis()
            QueryResponse response = client.query('suburbs', q)
            logger.info("Query finished. Time: '{}' ms", (System.currentTimeMillis() - time))
            response
        } catch (Exception e) {
            logger.error("Failed to search suburbs.", e)
            throw new InternalServerErrorException(e)
        }
    }

    List<Item> searchSuburbsByName(String term) {
        serialize(searchSuburbs('suburb', term))
    }

    List<Item> searchSuburbsByPostcode(String term) {
        serialize(searchSuburbs('postcode', term))
    }

    List<Item> serialize(QueryResponse response) {
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