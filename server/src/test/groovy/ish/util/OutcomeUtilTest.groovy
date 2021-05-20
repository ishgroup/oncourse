package ish.util

import groovy.transform.CompileStatic
import ish.messaging.ICertificate
import ish.messaging.ICertificateOutcome
import ish.messaging.IOutcome
import org.junit.jupiter.api.Assertions
import org.junit.jupiter.api.Test

import java.time.LocalDate

import static org.mockito.Mockito.mock
import static org.mockito.Mockito.when

@CompileStatic
class OutcomeUtilTest {

    @Test
    void testEditableStatus() {
        IOutcome editableOutcome = mock(IOutcome.class)
        ICertificateOutcome co1 = mock(ICertificateOutcome.class)
        ICertificateOutcome co2 = mock(ICertificateOutcome.class)
        List editableList = new ArrayList<>(Arrays.asList(co1, co2))
        when(editableOutcome.getCertificateOutcomes()).thenReturn(editableList)

        ICertificate certificate1 = mock(ICertificate.class)
        when(co1.getCertificate()).thenReturn(certificate1)
        when(certificate1.getPrintedOn()).thenReturn(LocalDate.now())
        when(certificate1.getRevokedOn()).thenReturn(LocalDate.now())

        ICertificate certificate2 = mock(ICertificate.class)
        when(co2.getCertificate()).thenReturn(certificate2)
        when(certificate2.getPrintedOn()).thenReturn(null)
        when(certificate2.getRevokedOn()).thenReturn(null)

        Assertions.assertTrue(OutcomeUtil.isEditableStatus(editableOutcome))


        IOutcome notEditableOutcome = mock(IOutcome.class)
        ICertificateOutcome co3 = mock(ICertificateOutcome.class)
        ICertificateOutcome co4 = mock(ICertificateOutcome.class)
        List notEditableList = new ArrayList<>(Arrays.asList(co3, co4))
        when(notEditableOutcome.getCertificateOutcomes()).thenReturn(notEditableList)

        ICertificate certificate3 = mock(ICertificate.class)
        when(co3.getCertificate()).thenReturn(certificate3)
        when(certificate3.getPrintedOn()).thenReturn(LocalDate.now())
        when(certificate3.getRevokedOn()).thenReturn(null)

        ICertificate certificate4 = mock(ICertificate.class)
        when(co4.getCertificate()).thenReturn(certificate4)
        when(certificate4.getPrintedOn()).thenReturn(null)
        when(certificate4.getRevokedOn()).thenReturn(null)

        Assertions.assertFalse(OutcomeUtil.isEditableStatus(notEditableOutcome))
    }
}
