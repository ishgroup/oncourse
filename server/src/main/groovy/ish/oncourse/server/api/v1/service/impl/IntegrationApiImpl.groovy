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

package ish.oncourse.server.api.v1.service.impl

import com.google.inject.Inject
import groovy.transform.CompileStatic
import ish.oncourse.server.ICayenneService
import static ish.oncourse.server.api.v1.function.IntegrationFunctions.getIntegrationById
import static ish.oncourse.server.api.v1.function.IntegrationFunctions.validateForCreate
import static ish.oncourse.server.api.v1.function.IntegrationFunctions.validateForDelete
import static ish.oncourse.server.api.v1.function.IntegrationFunctions.validateForUpdate
import ish.oncourse.server.api.v1.model.IntegrationDTO
import ish.oncourse.server.api.v1.model.IntegrationPropDTO
import ish.oncourse.server.api.v1.model.ValidationErrorDTO
import ish.oncourse.server.api.v1.service.IntegrationApi
import ish.oncourse.server.cayenne.IntegrationConfiguration
import ish.oncourse.server.cayenne.IntegrationProperty
import ish.oncourse.server.integration.PluginService
import ish.oncourse.server.integration.myob.MyobIntegration
import org.apache.cayenne.ObjectContext
import org.apache.cayenne.query.ObjectSelect

import javax.ws.rs.ClientErrorException
import javax.ws.rs.core.Response
import java.lang.reflect.Method
import java.time.ZoneOffset

@CompileStatic
class IntegrationApiImpl implements IntegrationApi {

    public static final String VERIFICATION_CODE = 'verificationCode'

    @Inject
    private ICayenneService cayenneService

    void setCayenneService(ICayenneService cayenneService) {
        this.cayenneService = cayenneService
    }

    @Override
    List<IntegrationDTO> get() {

        ObjectSelect.query(IntegrationConfiguration)
                .prefetch(IntegrationConfiguration.INTEGRATION_PROPERTIES.joint())
                .select(cayenneService.newContext)
                .collect { r ->
            new IntegrationDTO().with { i ->
                i.id = r.id.toString()
                i.created = r.createdOn.toInstant().atZone(ZoneOffset.UTC).toLocalDateTime()
                i.modified = r.modifiedOn.toInstant().atZone(ZoneOffset.UTC).toLocalDateTime()
                i.name = r.name
                i.type = r.type.toBigDecimal()

                Method getProps = PluginService.getProps(r.type)
                List<IntegrationProperty> props
                if (getProps) {
                    props = getProps.invoke(null, r) as List<IntegrationProperty>
                } else {
                    props = r.integrationProperties
                }
                i.props = props.collect {
                    if (it) {
                        new IntegrationPropDTO(key: it.keyCode, value: it.value)
                    }
                }

                i
            }
        }
    }

    @Override
    String getMyobAuthUrl() {
        return MyobIntegration.MYOB_ACCESS_CODE_URL
    }

    @Override
    void update(String id, IntegrationDTO data) {
        ValidationErrorDTO error = validateForUpdate(cayenneService.newContext, data, id)
        if (error) {
            throw new ClientErrorException(
                    Response.status(Response.Status.BAD_REQUEST).entity(error).build())
        }

        IntegrationConfiguration integration = getIntegrationById(cayenneService.newContext, id)
        updateIntegration(integration, data)
        integration.context.commitChanges()
    }

    @Override
    void create(IntegrationDTO data) {
        ValidationErrorDTO error = validateForCreate(cayenneService.newContext, data)
        if (error) {
            throw new ClientErrorException(
                    Response.status(Response.Status.BAD_REQUEST).entity(error).build())
        }

        Integer type = data.type.intValue()
        IntegrationConfiguration integration = createIntegration(type)
        updateIntegration(integration, data)
        integration.context.commitChanges()
    }

    @Override
    void remove(String id) {
        ValidationErrorDTO error = validateForDelete(cayenneService.newContext, id)
        if (error) {
            throw new ClientErrorException(
                    Response.status(Response.Status.BAD_REQUEST).entity(error).build())
        }

        IntegrationConfiguration integration = getIntegrationById(cayenneService.newContext, id)
        if (integration) {
            ObjectContext context = integration.context
            context.deleteObjects(integration.integrationProperties)
            context.deleteObject(integration)
            context.commitChanges()
        }
    }

    private IntegrationConfiguration createIntegration(Integer type) {
        ObjectContext context = cayenneService.newContext
        IntegrationConfiguration integration = context.newObject(IntegrationConfiguration)
        integration.type = type
        return integration
    }

    private void updateIntegration(IntegrationConfiguration configuration, IntegrationDTO data) {
        configuration.name = data.name
        configuration.modifiedOn = new Date()

        Map<String, String> props = data.props.collectEntries {[(it.key): it.value]}
        if (data.verificationCode) {
            props[(VERIFICATION_CODE)] = data.verificationCode
        }

        Method onSave = PluginService.onSave(configuration.type)
        if (onSave) {
            onSave.invoke(null, configuration, props)
        } else {
            data.props.each { f ->
                configuration.setProperty(f.key, f.value)
            }
        }
    }
}
