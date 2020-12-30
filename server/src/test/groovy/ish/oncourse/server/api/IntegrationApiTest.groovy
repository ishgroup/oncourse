package ish.oncourse.server.api

import groovy.transform.CompileStatic
import ish.CayenneIshTestCase
import ish.oncourse.server.ICayenneService
import ish.oncourse.server.api.v1.model.IntegrationDTO
import ish.oncourse.server.api.v1.model.IntegrationPropDTO
import ish.oncourse.server.api.v1.service.impl.IntegrationApiImpl
import ish.oncourse.server.cayenne.IntegrationConfiguration
import ish.oncourse.server.integration.myob.MyobIntegration
import org.apache.cayenne.query.ObjectSelect
import static org.junit.Assert.assertEquals
import org.junit.Before
import org.junit.Test

@CompileStatic
class IntegrationApiTest extends CayenneIshTestCase {

    @Before
    void before(){
        wipeTables()
    }
    @Test
    void test() {

        ICayenneService cayenneService = injector.getInstance(ICayenneService)
        IntegrationApiImpl integrationApi = new IntegrationApiImpl()
        integrationApi.cayenneService = cayenneService

        assertEquals(0, integrationApi.get().size())
        List<IntegrationPropDTO> fields = [new IntegrationPropDTO(key: MyobIntegration.MYOB_BASE_URL, value: 'http://myob.com'),
                                           new IntegrationPropDTO(key: MyobIntegration.MYOB_USER, value: 'user'),
                                           new IntegrationPropDTO(key: MyobIntegration.MYOB_PASSWORD, value: 'pass'),
                                           new IntegrationPropDTO(key: MyobIntegration.MYOB_REFRESH_TOKEN, value: 'token'),

        ]
        IntegrationDTO data = new IntegrationDTO(name: 'integration', type: new BigDecimal(6), props: fields)

        integrationApi.create(data)

        assertEquals(1, integrationApi.get().size())

        IntegrationConfiguration persistIntegration = ObjectSelect.query(IntegrationConfiguration).selectFirst(cayenneService.newContext)
        IntegrationDTO restIntegr = integrationApi.get()[0]
        assertEquals(persistIntegration.type, restIntegr.type.toInteger())
        assertEquals(persistIntegration.name, restIntegr.name)
        restIntegr.props.each { prop ->
            assertEquals(persistIntegration.getIntegrationProperty(prop.key).value, prop.value)
        }

        integrationApi.remove(persistIntegration.id.toString())

        assertEquals(0, integrationApi.get().size())
        assertEquals(0, ObjectSelect.query(IntegrationConfiguration).select(cayenneService.newContext).size())

    }
}
