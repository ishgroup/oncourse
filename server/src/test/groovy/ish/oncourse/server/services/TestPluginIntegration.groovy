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

import groovy.transform.CompileStatic
import ish.oncourse.server.cayenne.IntegrationConfiguration
import ish.oncourse.server.cayenne.IntegrationProperty
import ish.oncourse.server.integration.*
import org.apache.cayenne.ObjectContext

@CompileStatic
@Plugin(type = 1000, oneOnly = true)
class TestPluginIntegration implements PluginTrait {

    public static final String FIRST_PROPERTY = "firstProperty"
    public static final String SECOND_PROPERTY = "secondProperty"
    public static final String THIRD_PROPERTY = "thirdProperty"

    TestPluginIntegration(Map args) {
        loadConfig(args)
    }

    @OnStart
    void onStart() {
        def user = systemUserService.currentUser
        ObjectContext context = user.context
        user.firstName = 'John'
        context.commitChanges()
    }

    @OnSave
    static void onSave(IntegrationConfiguration configuration, Map<String, String> props) {
        configuration.setProperty(FIRST_PROPERTY, props[FIRST_PROPERTY])
        configuration.setProperty(SECOND_PROPERTY, props[SECOND_PROPERTY])
        configuration.setProperty(THIRD_PROPERTY, props[THIRD_PROPERTY])
    }

    @GetProps
    static List<IntegrationProperty> getProps(IntegrationConfiguration configuration) {
        return [configuration.getIntegrationProperty(FIRST_PROPERTY),
                configuration.getIntegrationProperty(SECOND_PROPERTY),
                configuration.getIntegrationProperty(THIRD_PROPERTY)
        ]
    }


}
