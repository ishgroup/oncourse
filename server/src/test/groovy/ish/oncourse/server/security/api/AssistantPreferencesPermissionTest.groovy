/*
 * Copyright ish group pty ltd 2026.
 *
 * This program is free software: you can redistribute it and/or modify it under the terms of the GNU Affero General Public License version 3 as published by the Free Software Foundation.
 *
 *  This program is distributed in the hope that it will be useful, but WITHOUT ANY WARRANTY; without even the implied warranty of MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE. See the GNU Affero General Public License for more details.
 */

package ish.oncourse.server.security.api

import com.google.inject.AbstractModule
import com.google.inject.Guice
import groovy.transform.CompileStatic
import ish.oncourse.server.cayenne.SystemUser
import ish.oncourse.server.services.ISystemUserService
import org.junit.jupiter.api.Assertions
import org.junit.jupiter.api.BeforeAll
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.TestInstance

import javax.servlet.http.HttpServletRequest
import javax.servlet.http.HttpServletResponse
import javax.ws.rs.HttpMethod

import static org.mockito.Mockito.mock
import static org.mockito.Mockito.never
import static org.mockito.Mockito.verify
import static org.mockito.Mockito.when

/**
 * onCourse Assistant settings are college preferences: only admins can read or change them through /preference,
 * while every user reads whether the assistant is available to them through /user/preference.
 */
@CompileStatic
@TestInstance(TestInstance.Lifecycle.PER_CLASS)
class AssistantPreferencesPermissionTest {

    private static final String PREFERENCE_PATH = '/a/v1/preference'
    private static final String USER_PREFERENCE_PATH = '/a/v1/user/preference'

    static class CurrentUserService implements ISystemUserService {

        final SystemUser currentUser = new SystemUser()

        @Override
        List<SystemUser> getSystemUsersByRole(String name) {
            return []
        }
    }

    private CurrentUserService userService = new CurrentUserService()

    private SystemUser currentUser = userService.currentUser

    private IPermissionService permissionService

    @BeforeAll
    void createPermissionService() {
        // permissions of the real REST API, as configured in application.yml
        PermissionServiceFactory factory = new PermissionServiceFactory()
        factory.setApiPackage('ish.oncourse.server.api.v1.service')

        ISystemUserService users = userService
        permissionService = factory.createPermissionService(Guice.createInjector(new AbstractModule() {
            @Override
            protected void configure() {
                bind(ISystemUserService).toInstance(users)
            }
        }))
    }

    @Test
    void nonAdminReadsUserPreferences() {
        currentUser.setIsAdmin(false)

        Assertions.assertTrue(permissionService.hasAccess(USER_PREFERENCE_PATH, HttpMethod.GET).isSuccessful())
        Assertions.assertTrue(authorize(USER_PREFERENCE_PATH, HttpMethod.GET))
    }

    @Test
    void nonAdminCannotReadOrChangePreferences() {
        currentUser.setIsAdmin(false)

        Assertions.assertFalse(permissionService.hasAccess(PREFERENCE_PATH, HttpMethod.GET).isSuccessful())
        Assertions.assertFalse(permissionService.hasAccess(PREFERENCE_PATH, HttpMethod.POST).isSuccessful())
        Assertions.assertFalse(authorize(PREFERENCE_PATH, HttpMethod.GET))
        Assertions.assertFalse(authorize(PREFERENCE_PATH, HttpMethod.POST))
    }

    @Test
    void adminReadsAndChangesPreferences() {
        currentUser.setIsAdmin(true)

        Assertions.assertTrue(authorize(PREFERENCE_PATH, HttpMethod.GET))
        Assertions.assertTrue(authorize(PREFERENCE_PATH, HttpMethod.POST))
    }

    private boolean authorize(String path, String method) {
        HttpServletRequest request = mock(HttpServletRequest)
        when(request.getRequestURI()).thenReturn(path)
        when(request.getMethod()).thenReturn(method)
        HttpServletResponse response = mock(HttpServletResponse)

        boolean authorized = permissionService.authorize(request, response)

        if (authorized) {
            verify(response, never()).sendError(HttpServletResponse.SC_FORBIDDEN)
        } else {
            verify(response).sendError(HttpServletResponse.SC_FORBIDDEN)
        }
        return authorized
    }
}
