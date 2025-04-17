/*
 * Copyright ish group pty ltd 2023.
 *
 * This program is free software: you can redistribute it and/or modify it under the terms of the GNU Affero General Public License version 3 as published by the Free Software Foundation.
 *
 *  This program is distributed in the hope that it will be useful, but WITHOUT ANY WARRANTY; without even the implied warranty of MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE. See the GNU Affero General Public License for more details.
 */

package ish.oncourse.server.api.v1.login

import ish.common.types.IntegrationType
import ish.oncourse.server.integration.okta.OktaIntegration

enum Sso {
    OKTA("okta", IntegrationType.OKTA)

    private String name
    private IntegrationType integrationType

    Sso(String name, IntegrationType integrationType) {
        this.name = name
        this.integrationType = integrationType
    }

    String getName() {
        return name
    }

    IntegrationType getIntegrationType() {
        return integrationType
    }

    static Sso ofType(String name){
        return values().find {it.name == name}
    }

    SsoIntegrationTrait getSsoProvider(Map args){
        switch (this){
            case OKTA:
                return new OktaIntegration(args)
            default:
                throw new IllegalArgumentException("Unsupported SSO type")
        }
    }
}