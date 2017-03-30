package ish.oncourse.solr.model

import com.fasterxml.jackson.databind.ObjectMapper
import org.junit.Assert
import org.junit.Test

class SolrTagTest {

    @Test
    void test() {

        ObjectMapper mapper = new ObjectMapper()
        SolrTag s = new SolrTag().with {
            id = 12
            collegeId = 44
            name = 'subject'
            it
        }

        String json = mapper.writeValueAsString(s)
        SolrTag r = mapper.readValue(json, SolrTag)
        Assert.assertEquals(s, r)
    }
}
