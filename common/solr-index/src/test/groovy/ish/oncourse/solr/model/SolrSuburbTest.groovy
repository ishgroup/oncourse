package ish.oncourse.solr.model

import com.fasterxml.jackson.databind.ObjectMapper
import org.junit.Assert
import org.junit.Test

class SolrSuburbTest {
    @Test
    void test() {

        ObjectMapper mapper = new ObjectMapper()
        SolrSuburb s = new SolrSuburb().with {
            id = '1'
            suburb = 'suburb1'
            state = 'state1'
            postcode = 'postcode1'
            loc = 'loc'
            it
        }

        String json = mapper.writeValueAsString(s)
        SolrSuburb r = mapper.readValue(json, SolrSuburb)
        Assert.assertEquals(s, r)
    }
}
