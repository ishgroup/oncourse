/*
 * Copyright ish group pty ltd 2020.
 *
 * This program is free software: you can redistribute it and/or modify it under the terms of the
 * GNU Affero General Public License version 3 as published by the Free Software Foundation.
 *
 * This program is distributed in the hope that it will be useful, but WITHOUT ANY WARRANTY;
 * without even the implied warranty of MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.
 * See the GNU Affero General Public License for more details.
 */

package ish.oncourse.server.services

import groovy.transform.CompileDynamic
import groovy.transform.CompileStatic
import ish.TestWithDatabase
import ish.DatabaseSetup
import ish.oncourse.server.api.v1.model.IntegrationDTO
import ish.oncourse.server.api.v1.model.IntegrationPropDTO
import ish.oncourse.server.api.v1.service.impl.IntegrationApiImpl
import ish.oncourse.server.cayenne.IntegrationConfiguration
import ish.oncourse.server.cayenne.IntegrationProperty
import ish.oncourse.server.cayenne.SystemUser
import ish.oncourse.server.integration.PluginService
import org.apache.cayenne.query.ObjectSelect
import org.junit.jupiter.api.Assertions
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.Test

import javax.ws.rs.ClientErrorException
import java.lang.reflect.Method

import static ish.oncourse.server.api.v1.function.IntegrationFunctions.getIntegrationByName
import static ish.oncourse.server.api.v1.function.IntegrationFunctions.hasIntegration

@CompileStatic
@DatabaseSetup(value = "ish/oncourse/server/services/pluginServiceTestDataSet.xml")
class PluginServiceTest extends TestWithDatabase {

    private final static int TEST_TYPE_VALUE = 1000
    private final static String TEST_NAME_VALUE = "PluginServiceTest"

    private IntegrationApiImpl integrationApi
    private List<IntegrationPropDTO> defaultProperties
    
    @BeforeEach
    void integrations() {
        integrationApi = new IntegrationApiImpl()
        integrationApi.cayenneService = cayenneService

        defaultProperties = [
                new IntegrationPropDTO(key: TestPluginIntegration.FIRST_PROPERTY, value: "valueFirstProperty"),
                new IntegrationPropDTO(key: TestPluginIntegration.SECOND_PROPERTY, value: "valueSecondProperty"),
                new IntegrationPropDTO(key: TestPluginIntegration.THIRD_PROPERTY, value: "valueThirdProperty")
        ]
    }

    @Test
    void findIntegrationByTypeWithPluginService() {
        Integer type = ObjectSelect.query(IntegrationConfiguration)
                .where(IntegrationConfiguration.NAME.eq("WithoutAnnotatedClass"))
                .column(IntegrationConfiguration.TYPE)
                .selectOne(cayenneService.newContext)

        Assertions.assertNull(PluginService.getPluginClass(type))
    }


    
    @Test
    void callOnStartMethodOfClassWithPluginService() {
        injector.getInstance(PluginService).onStart()

        def users = ObjectSelect.query(SystemUser).orderBy(SystemUser.ID.asc()).select(cayenneService.newContext)
        def user = ObjectSelect.query(SystemUser).orderBy(SystemUser.ID.asc()).selectFirst(cayenneService.newContext)
        Assertions.assertEquals('John', user.firstName)
    }


    
    @Test
    void createNewIntegrationAndWorkWithPropertiesByPluginService() {

        createIntegration(TEST_NAME_VALUE)
        Assertions.assertEquals(Boolean.TRUE, hasIntegration(cayenneService.newContext, TEST_TYPE_VALUE))

        IntegrationConfiguration configuration = getIntegrationByName(cayenneService.newContext, TEST_NAME_VALUE)
        Assertions.assertNotNull(configuration)

        Method methodForGetProps = PluginService.getProps(TEST_TYPE_VALUE)
        Assertions.assertNotNull(methodForGetProps)

        List<IntegrationProperty> integrationProperties = methodForGetProps.invoke(null, configuration) as List<IntegrationProperty>

        Assertions.assertEquals(3, integrationProperties.size())
        Assertions.assertEquals(configuration.integrationProperties.size(), integrationProperties.size())
        Assertions.assertEquals(TestPluginIntegration.FIRST_PROPERTY, integrationProperties[0].keyCode)
        Assertions.assertEquals(TestPluginIntegration.SECOND_PROPERTY, integrationProperties[1].keyCode)
        Assertions.assertEquals(TestPluginIntegration.THIRD_PROPERTY, integrationProperties[2].keyCode)

        Map<String, String> newPropertiesValue = integrationProperties.collectEntries { property ->
            [property.keyCode, property.value.concat("New")]
        } as Map<String, String>

        Method methodForSave = PluginService.onSave(configuration.type)
        Assertions.assertNotNull(methodForSave)

        methodForSave.invoke(null, configuration, newPropertiesValue)
        configuration.context.commitChanges()

        integrationProperties = methodForGetProps.invoke(null, configuration) as List<IntegrationProperty>

        Assertions.assertEquals(3, integrationProperties.size())
        Assertions.assertEquals("valueFirstPropertyNew", integrationProperties[0].value)
        Assertions.assertEquals("valueSecondPropertyNew", integrationProperties[1].value)
        Assertions.assertEquals("valueThirdPropertyNew", integrationProperties[2].value)
    }


    @Test
    @CompileDynamic
    void tryToCreateMoreThanOneConfigurationOfTestClass() {
        if (!hasIntegration(cayenneService.newContext, TEST_TYPE_VALUE)) {
            createIntegration(TEST_NAME_VALUE)
        }
        Assertions.assertEquals(Boolean.TRUE, hasIntegration(cayenneService.newContext, TEST_TYPE_VALUE))
        Assertions.assertEquals(Boolean.TRUE, PluginService.onlyOne(TEST_TYPE_VALUE))

        try {
            createIntegration(TEST_NAME_VALUE.concat("_NEW"))
        } catch (ClientErrorException ex) {
            Assertions.assertEquals("Then only one integration of this type can be created", ex.response.entity.errorMessage)
        }
    }

    @CompileDynamic
    private void createIntegration(String name) {
        IntegrationDTO dto = new IntegrationDTO(type: TEST_TYPE_VALUE, name: name, props: defaultProperties)
        integrationApi.create(dto)
    }

}
