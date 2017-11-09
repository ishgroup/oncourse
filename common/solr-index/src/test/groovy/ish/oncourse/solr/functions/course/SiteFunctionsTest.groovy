package ish.oncourse.solr.functions.course

import com.github.javafaker.Address
import com.github.javafaker.Faker
import com.github.javafaker.Number
import ish.oncourse.model.Site
import ish.oncourse.solr.model.SSite
import org.junit.Assert
import org.junit.Test

import static org.mockito.Mockito.mock
import static org.mockito.Mockito.when

/**
 * User: akoiro
 * Date: 9/11/17
 */
class SiteFunctionsTest {
    private static final Address addressFaker = new Faker().address()
    private static final Number numberFaker = new Faker().number()

    @Test
    void test() {
        Site site = mock(Site)
        when(site.id).thenReturn(numberFaker.numberBetween(1000l, 2000l))
        when(site.state).thenReturn(addressFaker.state())
        when(site.suburb).thenReturn(addressFaker.city())
        when(site.postcode).thenReturn(addressFaker.zipCode())
        when(site.latitude).thenReturn(new BigDecimal(addressFaker.latitude()))
        when(site.longitude).thenReturn(new BigDecimal(addressFaker.longitude()))

        SSite sSite = SiteFunctions.getSSite(site)

        Assert.assertEquals(site.id, sSite.id)
        Assert.assertEquals(site.state, sSite.state)
        Assert.assertEquals(site.suburb, sSite.suburb)
        Assert.assertEquals(site.postcode, sSite.postcode)
        Assert.assertEquals(String.format("%f,%f", site.latitude, site.longitude), sSite.location)
    }
}
