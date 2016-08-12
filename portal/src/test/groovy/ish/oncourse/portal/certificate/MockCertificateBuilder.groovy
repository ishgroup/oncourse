package ish.oncourse.portal.certificate

import ish.common.types.QualificationType
import ish.oncourse.model.*
import org.apache.commons.lang3.RandomStringUtils

import static ish.common.types.OutcomeStatus.STATUSES_VALID_FOR_CERTIFICATE
import static org.apache.commons.lang3.RandomUtils.nextInt
import static org.mockito.Mockito.mock
import static org.mockito.Mockito.when

/**
 * User: akoiro
 * Date: 12/08/2016
 */
class MockCertificateBuilder {
    public static Certificate build() {
        def college = mock(College.class)
        when(college.name).thenReturn("College1");

        def qualification = mock(Qualification.class);
        when(qualification.level).thenReturn(RandomStringUtils.randomAlphanumeric(10))
        when(qualification.title).thenReturn(RandomStringUtils.randomAlphanumeric(10))
        when(qualification.nationalCode).thenReturn(RandomStringUtils.randomAlphanumeric(10))
        when(qualification.isAccreditedCourse).thenReturn(QualificationType.values()[nextInt(0, QualificationType.values().length)])


        def certificate = mock(Certificate)
        when(certificate.certificateNumber).thenReturn(1234567890L)
        when(certificate.studentFirstName).thenReturn("FirstName")
        when(certificate.studentFirstName).thenReturn("FirstName")
        when(certificate.studentLastName).thenReturn("LastName")
        when(certificate.college).thenReturn(college)
        def date = new Date()
        when(certificate.issued).thenReturn(date)
        when(certificate.qualification).thenReturn(qualification)

        def List<CertificateOutcome> cos = new ArrayList<CertificateOutcome>()
        int i = 0
        while(i<10) {
            cos.add(getCertificateOutcome())
            i++
        }
        when(certificate.certificateOutcomes).thenReturn(cos)
        return certificate
    }

    private static CertificateOutcome getCertificateOutcome() {
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
