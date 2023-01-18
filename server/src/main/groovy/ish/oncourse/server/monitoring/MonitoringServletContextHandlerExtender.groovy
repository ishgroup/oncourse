/*
 * Copyright ish group pty ltd 2022.
 *
 * This program is free software: you can redistribute it and/or modify it under the terms of the GNU Affero General Public License version 3 as published by the Free Software Foundation.
 *
 *  This program is distributed in the hope that it will be useful, but WITHOUT ANY WARRANTY; without even the implied warranty of MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE. See the GNU Affero General Public License for more details.
 */

package ish.oncourse.server.monitoring

import io.bootique.jetty.server.ServletContextHandlerExtender
import ish.oncourse.server.monitoring.servlet.MonitoringServlet
import org.eclipse.jetty.security.ConstraintMapping
import org.eclipse.jetty.security.ConstraintSecurityHandler
import org.eclipse.jetty.security.HashLoginService
import org.eclipse.jetty.security.SecurityHandler
import org.eclipse.jetty.security.UserStore
import org.eclipse.jetty.security.authentication.BasicAuthenticator
import org.eclipse.jetty.servlet.ServletContextHandler
import org.eclipse.jetty.util.security.Constraint
import org.eclipse.jetty.util.security.Credential

class MonitoringServletContextHandlerExtender implements ServletContextHandlerExtender {

    private final MonitoringService monitoringService

    MonitoringServletContextHandlerExtender(MonitoringService monitoringService) {
        this.monitoringService = monitoringService
    }

    @Override
    void onHandlerInstalled(ServletContextHandler handler) {
        if (monitoringService.isEnable()) {
            handler.setSecurityHandler(createBasicAuth(MonitoringServlet.MONITORING_PATH))
        }
    }

    private SecurityHandler createBasicAuth(String monitoringPath) {
        HashLoginService loginService = new HashLoginService()
        UserStore userStore = new UserStore()
        userStore.addUser(monitoringService.userName, Credential.getCredential(monitoringService.password), null)

        loginService.setUserStore(userStore)
        loginService.setName("Monitoring")

        Constraint constraint = new Constraint()
        constraint.setName(Constraint.__BASIC_AUTH)
//        ** role means being authenticated is sufficient, not necessary to check roles
        constraint.setRoles("**")
        constraint.setAuthenticate(true)

        ConstraintMapping constraintMapping = new ConstraintMapping()
        constraintMapping.setConstraint(constraint)
        constraintMapping.setPathSpec(monitoringPath)

        ConstraintSecurityHandler constraintSecurityHandler = new ConstraintSecurityHandler()
        constraintSecurityHandler.setAuthenticator(new BasicAuthenticator())
        constraintSecurityHandler.setRealmName("Monitoring")
        constraintSecurityHandler.addConstraintMapping(constraintMapping)
        constraintSecurityHandler.setLoginService(loginService)

        return constraintSecurityHandler
    }
}
