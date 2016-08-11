package ish.oncourse.portal.certificate

import ish.common.types.QualificationType
import ish.oncourse.model.*
import ish.oncourse.services.preference.PreferenceController
import org.apache.commons.lang3.RandomStringUtils
import org.junit.Test

import static ish.common.types.OutcomeStatus.STATUSES_VALID_FOR_CERTIFICATE
import static org.apache.commons.lang3.RandomUtils.nextInt
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
        def college = mock(College.class)
        when(college.name).thenReturn("College1");

        def preferenceController = mock(PreferenceController)
        when(preferenceController.collegeURL).thenReturn("https://college1.au")
        when(preferenceController.avetmissID).thenReturn("00001")


        def quailification = mock(Qualification.class);
        when(quailification.level).thenReturn(RandomStringUtils.randomAlphanumeric(10))
        when(quailification.title).thenReturn(RandomStringUtils.randomAlphanumeric(10))
        when(quailification.nationalCode).thenReturn(RandomStringUtils.randomAlphanumeric(10))
        when(quailification.isAccreditedCourse).thenReturn(QualificationType.values()[nextInt(0, QualificationType.values().length)])


        def certificate = mock(Certificate)
        when(certificate.certificateNumber).thenReturn(1234567890L)
        when(certificate.studentFirstName).thenReturn("FirstName")
        when(certificate.studentFirstName).thenReturn("FirstName")
        when(certificate.studentLastName).thenReturn("LastName")
        when(certificate.college).thenReturn(college)
        def date = new Date()
        when(certificate.issued).thenReturn(date)
        when(certificate.qualification).thenReturn(quailification)

        def List<CertificateOutcome> cos = new ArrayList<CertificateOutcome>()
        int i = 0
        while(i<10) {
            cos.add(getCertificateOutcome())
            i++
        }
        when(certificate.certificateOutcomes).thenReturn(cos)

        def model = Model.valueOf(certificate, preferenceController)

        assertEquals("1234567890", model.number)
        assertEquals("College1", model.collegeName)
        assertEquals("http://college1.au", model.collegeUrl)
        assertEquals("FirstName", model.firstName)
        assertEquals("LastName", model.lastName)
        assertEquals(date, model.issued)

        assertEquals(quailification, model)

        assertEquals(Boolean.TRUE, model.nrt)
        assertEquals("00001", model.rto)
        assertEquals(10, model.modules.size())

        i = 0
        cos.each {it ->
            assertEquals(it.outcome.module.title, model.modules[i].title)
            assertEquals(it.outcome.module.nationalCode, model.modules[i].code)
            i++
        }

    }

    private void assertEquals(Qualification quailification, Model model) {
        assertNotNull(model.qualification)
        assertEquals(quailification.nationalCode, model.qualification.code)
        assertEquals(quailification.title, model.qualification.title)
        assertEquals(quailification.level, model.qualification.level)
        assertEquals(quailification.isAccreditedCourse, model.qualification.type)
    }

    private CertificateOutcome getCertificateOutcome() {
        def m = mock(Module)
        when(m.title).thenReturn(RandomStringUtils.randomAlphanumeric(10))
        when(m.nationalCode).thenReturn(RandomStringUtils.randomAlphanumeric(5))

        def o  = mock(Outcome)
        when(o.status).thenReturn(STATUSES_VALID_FOR_CERTIFICATE.get(nextInt(0, STATUSES_VALID_FOR_CERTIFICATE.size())))
        when(o.module).thenReturn(m)

        def co = mock(CertificateOutcome)
        when(co.outcome).thenReturn(o)
        return co
    }
}
