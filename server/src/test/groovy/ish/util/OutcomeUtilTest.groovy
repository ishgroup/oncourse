package ish.util

import groovy.transform.CompileStatic
import ish.oncourse.server.cayenne.Certificate
import ish.oncourse.server.cayenne.CertificateOutcome
import ish.oncourse.server.cayenne.Outcome
import org.junit.jupiter.api.Assertions
import org.junit.jupiter.api.Test

import java.time.LocalDate

import static org.mockito.Mockito.mock
import static org.mockito.Mockito.when

@CompileStatic
class OutcomeUtilTest {

    @Test
    void testEditableStatus() {
        Outcome editableOutcome = mock(Outcome.class)
        CertificateOutcome co1 = mock(CertificateOutcome.class)
        CertificateOutcome co2 = mock(CertificateOutcome.class)
        List editableList = new ArrayList<>(Arrays.asList(co1, co2))
        when(editableOutcome.getCertificateOutcomes()).thenReturn(editableList)

        Certificate certificate1 = mock(Certificate.class)
        when(co1.getCertificate()).thenReturn(certificate1)
        when(certificate1.getPrintedOn()).thenReturn(LocalDate.now())
        when(certificate1.getRevokedOn()).thenReturn(LocalDate.now())

        Certificate certificate2 = mock(Certificate.class)
        when(co2.getCertificate()).thenReturn(certificate2)
        when(certificate2.getPrintedOn()).thenReturn(null)
        when(certificate2.getRevokedOn()).thenReturn(null)

        Assertions.assertTrue(OutcomeUtil.isEditableStatus(editableOutcome))


        Outcome notEditableOutcome = mock(Outcome.class)
        CertificateOutcome co3 = mock(CertificateOutcome.class)
        CertificateOutcome co4 = mock(CertificateOutcome.class)
        List notEditableList = new ArrayList<>(Arrays.asList(co3, co4))
        when(notEditableOutcome.getCertificateOutcomes()).thenReturn(notEditableList)

        Certificate certificate3 = mock(Certificate.class)
        when(co3.getCertificate()).thenReturn(certificate3)
        when(certificate3.getPrintedOn()).thenReturn(LocalDate.now())
        when(certificate3.getRevokedOn()).thenReturn(null)

        Certificate certificate4 = mock(Certificate.class)
        when(co4.getCertificate()).thenReturn(certificate4)
        when(certificate4.getPrintedOn()).thenReturn(null)
        when(certificate4.getRevokedOn()).thenReturn(null)

        Assertions.assertFalse(OutcomeUtil.isEditableStatus(notEditableOutcome))
    }
}
