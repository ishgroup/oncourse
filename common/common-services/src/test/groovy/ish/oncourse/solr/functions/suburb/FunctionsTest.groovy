package ish.oncourse.solr.functions.suburb

import ish.oncourse.model.PostcodeDb
import ish.oncourse.solr.model.SolrSuburb
import org.junit.Test

import static org.junit.Assert.assertEquals
import static org.mockito.Mockito.mock
import static org.mockito.Mockito.when

class FunctionsTest {

    @Test
    void test_getSolrSuburb() {
        PostcodeDb postcodeDb = mock(PostcodeDb)
        when(postcodeDb.postcode).thenReturn(2017L)
        when(postcodeDb.suburb).thenReturn('Waterloo')
        when(postcodeDb.state).thenReturn('NSW')
        when(postcodeDb.lat).thenReturn(-33.9036972D)
        when(postcodeDb.lon).thenReturn(151.1986751D)

        SolrSuburb suburb = Functions.getSolrSuburb(postcodeDb)
        assertEquals("${postcodeDb.postcode}${postcodeDb.suburb}".toString(), suburb.id)
        assertEquals(postcodeDb.postcode.toString(), suburb.postcode)
        assertEquals(postcodeDb.suburb, suburb.suburb)
        assertEquals(postcodeDb.state, suburb.state)
        assertEquals("${postcodeDb.lat},${postcodeDb.lon}".toString(), suburb.loc)
        assertEquals('suburb', suburb.doctype)
    }
}
