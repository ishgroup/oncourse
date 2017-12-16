package ish.oncourse.portal.certificate

import ish.oncourse.model.Certificate
import ish.oncourse.services.persistence.ICayenneService
import ish.oncourse.services.preference.PreferenceController
import ish.oncourse.services.preference.PreferenceControllerFactory
import org.apache.cayenne.ObjectContext
import org.apache.cayenne.query.Select
import org.junit.Test
import org.mockito.Matchers

import static org.junit.Assert.assertEquals
import static org.mockito.Mockito.mock
import static org.mockito.Mockito.when

/**
 * User: akoiro
 * Date: 12/08/2016
 */
class ModelBuilderTest {

    @Test
    public void test() {
        Certificate certificate = MockCertificateBuilder.build();
        ObjectContext context = mock(ObjectContext)
        when(context.selectFirst(Matchers.any(Select))).thenReturn(certificate)

        ICayenneService service = mock(ICayenneService)
        when(service.newContext()).thenReturn(context)
        when(service.newContext()).thenReturn(context)

        PreferenceController preferenceController = mock(PreferenceController)
        PreferenceControllerFactory factory = mock(PreferenceControllerFactory)
        when(factory.getPreferenceController(certificate.getCollege())).thenReturn(preferenceController)
        String code = "111111111111"
        ModelBuilder builder = ModelBuilder.valueOf(code, service, factory)
        ModelBuilder.Result result = builder.build()

        assertEquals(ModelBuilder.Result.successFull, result)

        code = ""
        builder = ModelBuilder.valueOf(code, service, factory)
        result = builder.build()
        assertEquals(ModelBuilder.Result.emptyCode, result)


        code = null
        builder = ModelBuilder.valueOf(code, service, factory)
        result = builder.build()
        assertEquals(ModelBuilder.Result.emptyCode, result)

    }
}
