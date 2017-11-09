package ish.oncourse.solr.model

import com.fasterxml.jackson.databind.ObjectMapper
import org.junit.Assert
import org.junit.Test

class SolrTagTest {

    @Test
    void test() {

        ObjectMapper mapper = new ObjectMapper()
        STag s = new STag().with {
            id = 12
            collegeId = 44
            name = 'subject'
            it
        }

        String json = mapper.writeValueAsString(s)
        STag r = mapper.readValue(json, STag)
        Assert.assertEquals(s, r)
    }
}
