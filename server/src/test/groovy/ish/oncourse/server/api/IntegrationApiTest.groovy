package ish.oncourse.server.api

import groovy.transform.CompileStatic
import ish.TestWithDatabase
import ish.oncourse.server.api.v1.model.IntegrationDTO
import ish.oncourse.server.api.v1.model.IntegrationPropDTO
import ish.oncourse.server.api.v1.service.impl.IntegrationApiImpl
import ish.oncourse.server.cayenne.IntegrationConfiguration
import org.apache.cayenne.query.ObjectSelect
import org.junit.jupiter.api.Assertions
import org.junit.jupiter.api.Test

@CompileStatic
class IntegrationApiTest extends TestWithDatabase {
    public static final String ALCHEMER_API_TOKEN = "apiToken"
    public static final String ALCHEMER_API_TOKEN_SECRET = "apiTokenSecret"
    public static final String ALCHEMER_SURVEY_ID = "surveyId"

    @Test
    void test() {
        IntegrationApiImpl integrationApi = new IntegrationApiImpl()
        integrationApi.cayenneService = cayenneService

        Assertions.assertEquals(0, integrationApi.get().size())
        List<IntegrationPropDTO> fields = [new IntegrationPropDTO(key: ALCHEMER_API_TOKEN, value: 'user'),
                                           new IntegrationPropDTO(key: ALCHEMER_API_TOKEN_SECRET, value: 'pass'),
                                           new IntegrationPropDTO(key: ALCHEMER_SURVEY_ID, value: 'surveyId'),
        ]
        IntegrationDTO data = new IntegrationDTO(name: 'integration', type: new BigDecimal(4), props: fields)

        integrationApi.create(data)

        Assertions.assertEquals(1, integrationApi.get().size())

        IntegrationConfiguration persistIntegration = ObjectSelect.query(IntegrationConfiguration).selectFirst(cayenneService.newContext)
        IntegrationDTO restIntegr = integrationApi.get()[0]
        Assertions.assertEquals(persistIntegration.type, restIntegr.type.toInteger())
        Assertions.assertEquals(persistIntegration.name, restIntegr.name)
        restIntegr.props.each { prop ->
            Assertions.assertEquals(persistIntegration.getIntegrationProperty(prop.key).value, prop.value)
        }

        integrationApi.remove(persistIntegration.id.toString())

        Assertions.assertEquals(0, integrationApi.get().size())
        Assertions.assertEquals(0, ObjectSelect.query(IntegrationConfiguration).select(cayenneService.newContext).size())

    }
}
