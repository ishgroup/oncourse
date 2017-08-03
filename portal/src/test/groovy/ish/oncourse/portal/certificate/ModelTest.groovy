package ish.oncourse.portal.certificate

import ish.oncourse.model.Qualification
import ish.oncourse.services.preference.PreferenceController
import org.junit.Test

import static org.junit.Assert.assertEquals
import static org.junit.Assert.assertNotNull
import static org.mockito.Mockito.mock
import static org.mockito.Mockito.when

/**
 * User: akoiro
 * Date: 12/08/2016
 */
class ModelTest {

    @Test
    public void test() {

        def certificate = MockCertificateBuilder.build()

        def preferenceController = mock(PreferenceController)
        when(preferenceController.collegeURL).thenReturn("https://college1.au")
        when(preferenceController.avetmissID).thenReturn("00001")

        def model = Model.valueOf(certificate, preferenceController)

        assertEquals("1234567890", model.number)
        assertEquals("College1", model.collegeName)
        assertEquals("https://college1.au", model.collegeUrl)
        assertEquals("FirstName", model.firstName)
        assertEquals("LastName", model.lastName)
        assertEquals(certificate.issued, model.issued)

        this.assertEquals(certificate.qualification, model)

        assertEquals(Boolean.TRUE, model.nrt)
        assertEquals("00001", model.rto)
        assertEquals(10, model.modules.size())

        def i = 0
        certificate.getCertificateOutcomes().each {it ->
            assertEquals(it.outcome.module.title, model.modules[i].title)
            assertEquals(it.outcome.module.nationalCode, model.modules[i].code)
            i++
        }

    }

    private void assertEquals(Qualification qualification, Model model) {
        assertNotNull(model.qualification)
        assertEquals(qualification.nationalCode, model.qualification.code)
        assertEquals(qualification.title, model.qualification.title)
        assertEquals(qualification.level, model.qualification.level)
        assertEquals(qualification.isAccreditedCourse, model.qualification.type)
    }
}
