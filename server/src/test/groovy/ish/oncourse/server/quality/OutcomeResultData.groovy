package ish.oncourse.server.quality

import ish.oncourse.server.cayenne.CourseClass
import ish.oncourse.server.cayenne.Enrolment
import ish.oncourse.server.cayenne.Module
import ish.oncourse.server.cayenne.Outcome
import ish.oncourse.types.Severity
import ish.quality.QualityResult
import ish.util.LocalDateUtils

import java.time.LocalDate

import static org.mockito.Mockito.mock
import static org.mockito.Mockito.when

/**
 * Created by akoiro on 17/03/2016.
 */
class OutcomeResultData {

    Closure<ArrayList<Enrolment>> testData = { ->
        return [enrolment(1, LocalDate.now().minusDays(30)),
                enrolment(2, LocalDate.now().minusDays(31)),
                enrolment(3, LocalDate.now().minusDays(27)),
                enrolment(4, LocalDate.now().minusDays(8)),
                enrolment(5, LocalDate.now().minusDays(6)),
                enrolment(6, LocalDate.now().minusDays(1)),
        ]

    }

    Closure<Enrolment> enrolment = { long id, LocalDate endDate ->
        def module = mock(Module)

        def outcome = mock(Outcome)
        when(outcome.module).thenReturn(module)
        when(outcome.endDate) thenReturn(endDate)

        def outcomes = Collections.singletonList(outcome)

        def courseClass = mock(CourseClass)
        when(courseClass.endDateTime).thenReturn(LocalDateUtils.valueToDate(endDate))

        def enrolment = mock(Enrolment)
        when(enrolment.id).thenReturn(id)
        when(enrolment.outcomes).thenReturn(outcomes)
        when(enrolment.courseClass).thenReturn(courseClass)
        when(enrolment.entityName).thenReturn(Enrolment.class.simpleName)

        return enrolment
    }

    Object assetResults(Collection<QualityResult> result) {
        Assertions.assertEquals(3, result.size())

        def qr28 = result.getAt(0)

        Assertions.assertEquals('Enrolment', qr28.entity)
        Assertions.assertEquals(2, qr28.records.size())
        Assertions.assertEquals(Severity.ERROR.level, qr28.severity)
        Assertions.assertNotNull(qr28.records.find { it == 1L })
        Assertions.assertNotNull(qr28.records.find { it == 2L })

        def qr7 = result.getAt(1)
        Assertions.assertEquals('Enrolment', qr7.entity)
        Assertions.assertEquals(Severity.WARNING.level, qr7.severity)
        Assertions.assertEquals(2, qr7.records.size())
        Assertions.assertNotNull(qr7.records.find { it == 3L })
        Assertions.assertNotNull(qr7.records.find { it == 4L })


        def qr0 = result.getAt(2)
        Assertions.assertEquals('Enrolment', qr0.entity)
        Assertions.assertEquals(Severity.ADVICE.level, qr0.severity)
        Assertions.assertEquals(2, qr0.records.size())
        Assertions.assertNotNull(qr0.records.find { it == 5L })
        Assertions.assertNotNull(qr0.records.find { it == 6L })

    }


}
