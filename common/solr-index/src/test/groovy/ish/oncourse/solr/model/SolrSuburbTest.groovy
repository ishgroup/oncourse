package ish.oncourse.solr.model

import com.fasterxml.jackson.databind.ObjectMapper
import org.junit.Assert
import org.junit.Test

class SolrSuburbTest {
    @Test
    void test() {

        ObjectMapper mapper = new ObjectMapper()
        SSuburb s = new SSuburb().with {
            id = '1'
            suburb = 'suburb1'
            state = 'state1'
            postcode = 'postcode1'
            loc = 'loc'
            it
        }

        String json = mapper.writeValueAsString(s)
        SSuburb r = mapper.readValue(json, SSuburb)
        Assert.assertEquals(s, r)
    }
}
