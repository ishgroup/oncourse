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
    public static final String MYOB_BASE_URL = "myobBaseUrl"
    public static final String MYOB_USER = "myobUser"
    public static final String MYOB_PASSWORD = "myobPassword"
    public static final String MYOB_FILE_NAME = "myobFileName"
    public static final String MYOB_REFRESH_TOKEN = "myobRefreshToken"

    @Test
    void test() {
        IntegrationApiImpl integrationApi = new IntegrationApiImpl()
        integrationApi.cayenneService = cayenneService

        Assertions.assertEquals(0, integrationApi.get().size())
        List<IntegrationPropDTO> fields = [new IntegrationPropDTO(key: MYOB_BASE_URL, value: 'http://myob.com'),
                                           new IntegrationPropDTO(key: MYOB_USER, value: 'user'),
                                           new IntegrationPropDTO(key: MYOB_PASSWORD, value: 'pass'),
                                           new IntegrationPropDTO(key: MYOB_REFRESH_TOKEN, value: 'token'),
                                           new IntegrationPropDTO(key: MYOB_FILE_NAME, value: 'fileName'),

        ]
        IntegrationDTO data = new IntegrationDTO(name: 'integration', type: new BigDecimal(6), props: fields)

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
