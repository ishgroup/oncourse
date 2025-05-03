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

package ish.oncourse.server.api.v1.function

import ish.common.types.IntegrationType
import ish.oncourse.server.api.v1.model.IntegrationDTO
import ish.oncourse.server.api.v1.model.ValidationErrorDTO
import ish.oncourse.server.cayenne.IntegrationConfiguration
import ish.oncourse.server.integration.PluginService
import org.apache.cayenne.ObjectContext
import org.apache.cayenne.query.ObjectSelect
import org.apache.cayenne.query.SelectById
import org.apache.commons.lang3.StringUtils


class IntegrationFunctions {


    static ValidationErrorDTO validateForCreate(ObjectContext context, IntegrationDTO data) {
        if (!data.type) {
            return new ValidationErrorDTO(data?.id, 'type', "Integration type isn't specified or incorrect")
        }

        def dbType = data.type.getDbType()
        def integrationClass = PluginService.getPluginClass(dbType)
        if(integrationClass == null)
            return new ValidationErrorDTO(null, 'type', "Plugin for this integration type not found")

        if (PluginService.onlyOne(dbType)  && hasIntegration(context, dbType) ) {
            return new ValidationErrorDTO(null, 'type', "Then only one integration of this type can be created")
        }

        if (!data.name) {
            return new ValidationErrorDTO(data?.id, 'name', "Integration name should be specified")
        }

        if (data.name.contains("\"")) {
            return new ValidationErrorDTO(data?.id, 'name', "Integration name cannot contain quotation marks.")
        }

        if (getIntegrationByName(context, data.name)) {
            return new ValidationErrorDTO(data?.id, 'name', "Integration name should be unique")
        }

        return null
    }

    static ValidationErrorDTO validateForUpdate(ObjectContext context, IntegrationDTO data, String id) {
        if (!StringUtils.isNumeric(id)) {
            return new ValidationErrorDTO(id, 'id', "Integration id '$id' is incorrect. It must contain of only numbers")
        }

        if (!data.name) {
            return new ValidationErrorDTO(id, 'name', "Integration name should be specified")
        }

        if (!data.type) {
            return new ValidationErrorDTO(id, 'type', "Integration type isn't specified or incorrect")
        }

        IntegrationConfiguration integration = getIntegrationById(context, id)

        if (!integration) {
            return new ValidationErrorDTO(id, 'id', "Integration $id is not exist")
        }

        IntegrationConfiguration integrationWithSameName = getIntegrationByName(context, data.name)

        if (integrationWithSameName && integrationWithSameName.id != id.toLong()) {
            return new ValidationErrorDTO(id, 'name', "Integration name should be unique" )
        }

        def dbType = data.type.getDbType()

        if (integration.type != dbType) {
            return new ValidationErrorDTO(id, 'type', "Integration type can not be changed")
        }

        return null
    }

    static ValidationErrorDTO validateForDelete(ObjectContext context, String id) {
        if (!StringUtils.isNumeric(id)) {
            return new ValidationErrorDTO(null, 'id', "Integration id '$id' is incorrect. It must contain of only numbers")
        }

        if(!getIntegrationById(context, id)) {
            return new ValidationErrorDTO(id, 'id', "Integration '$id' is not exist")
        }

        return null
    }

    static IntegrationConfiguration getIntegrationByName(ObjectContext context, String name) {
        return ObjectSelect.query(IntegrationConfiguration)
            .where(IntegrationConfiguration.NAME.eq(name))
            .selectOne(context)
    }

    static boolean hasIntegration(ObjectContext context, IntegrationType type) {
        return !ObjectSelect.query(IntegrationConfiguration)
                .where(IntegrationConfiguration.TYPE.eq(type))
                .select(context).empty
    }

    static IntegrationConfiguration getIntegrationById(ObjectContext context, String id) {
        return SelectById.query(IntegrationConfiguration, id)
            .prefetch(IntegrationConfiguration.INTEGRATION_PROPERTIES.joint())
            .selectOne(context)
    }
}
