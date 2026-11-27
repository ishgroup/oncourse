/*
 * Copyright ish group pty ltd 2026.
 *
 * This program is free software: you can redistribute it and/or modify it under the terms of the
 * GNU Affero General Public License version 3 as published by the Free Software Foundation.
 *
 * This program is distributed in the hope that it will be useful, but WITHOUT ANY WARRANTY;
 * without even the implied warranty of MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.
 * See the GNU Affero General Public License for more details.
 */

package ish.oncourse.server.security.api

import com.google.inject.Binder
import com.google.inject.Module
import groovy.transform.CompileStatic
import io.bootique.BQRuntime
import io.bootique.jetty.servlet.DefaultServletEnvironment
import ish.BootiqueTestFactory
import ish.common.types.KeyCode
import ish.common.types.Mask
import ish.oncourse.server.cayenne.ACLAccessKey
import ish.oncourse.server.cayenne.ACLRole
import ish.oncourse.server.cayenne.SystemUser
import ish.oncourse.server.services.ISystemUserService
import org.junit.jupiter.api.Assertions
import org.junit.jupiter.api.BeforeAll
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.extension.RegisterExtension

import javax.servlet.ServletContext
import javax.servlet.ServletRequestEvent
import javax.servlet.http.HttpServletRequest
import javax.servlet.http.HttpServletResponse
import javax.ws.rs.HttpMethod

import static org.mockito.ArgumentMatchers.anyInt
import static org.mockito.Mockito.mock
import static org.mockito.Mockito.never
import static org.mockito.Mockito.verify
import static org.mockito.Mockito.when

/**
 * Checks the permission of GET /list/count against the real API annotations: it needs the same view right as GET /list.
 */
@CompileStatic
class ListCountPermissionTest {

    private static final String COUNT_PATH = "/a/v1/list/count"
    private static final String LIST_PATH = "/a/v1/list"
    private static final String NO_VIEW_PERMISSION_MESSAGE = "Sorry, you have no permissions to view this entity. Please contact your administrator"

    private static final CurrentUserService userService = new CurrentUserService()
    private static final DefaultServletEnvironment servletEnvironment = new DefaultServletEnvironment()

    private static IPermissionService permissionService

    @RegisterExtension
    public static BootiqueTestFactory testFactory = new BootiqueTestFactory()

    @BeforeAll
    static void before() {
        BQRuntime runtime = testFactory.app("--config=classpath:listCountPermissionTest.yml")
                .module(PermissionModule.class)
                .module(new Module() {
                    @Override
                    void configure(Binder binder) {
                        binder.bind(ISystemUserService).toInstance(userService)
                        binder.bind(DefaultServletEnvironment).toInstance(servletEnvironment)
                    }
                })
                .createRuntime()

        permissionService = runtime.getInstance(IPermissionService.class)
    }

    @Test
    void testUserWithoutViewRightGets403() {
        userService.currentUser = user(false, KeyCode.INVOICE, Mask.NONE)
        HttpServletResponse response = mock(HttpServletResponse)
        StringWriter body = new StringWriter()
        when(response.getWriter()).thenReturn(new PrintWriter(body))

        Assertions.assertFalse(authorize("entity=Invoice&search=amountOwing%20%3E%200", response))

        verify(response).setStatus(HttpServletResponse.SC_FORBIDDEN)
        Assertions.assertTrue(body.toString().contains(NO_VIEW_PERMISSION_MESSAGE))
    }

    @Test
    void testUserWithViewRightIsAllowed() {
        userService.currentUser = user(false, KeyCode.INVOICE, Mask.VIEW)
        HttpServletResponse response = mock(HttpServletResponse)

        Assertions.assertTrue(authorize("entity=Invoice&search=amountOwing%20%3E%200", response))

        verify(response, never()).setStatus(anyInt())
        verify(response, never()).sendError(anyInt())
    }

    @Test
    void testAdminIsAllowed() {
        userService.currentUser = user(true)

        Assertions.assertTrue(authorize("entity=Invoice", mock(HttpServletResponse)))
    }

    @Test
    void testSameRuleAsListSearch() {
        List<SystemUser> users = [
                user(true),
                user(false),
                user(false, KeyCode.INVOICE, Mask.VIEW),
                user(false, KeyCode.COURSE, Mask.NONE),
        ]
        List<String> entities = ["Invoice", "Course", "Room", "Audit", "NoSuchEntity"]

        users.each { SystemUser user ->
            userService.currentUser = user
            entities.each { String entity ->
                boolean list = permissionService.hasAccess("$LIST_PATH?entity=$entity", HttpMethod.GET).successful
                boolean count = permissionService.hasAccess("$COUNT_PATH?entity=$entity", HttpMethod.GET).successful
                Assertions.assertEquals(list, count, "admin: ${user.isAdmin}, entity: $entity")
            }
        }
        // the comparison above covers both outcomes
        userService.currentUser = user(false, KeyCode.INVOICE, Mask.VIEW)
        Assertions.assertTrue(permissionService.hasAccess("$COUNT_PATH?entity=Invoice", HttpMethod.GET).successful)
        Assertions.assertFalse(permissionService.hasAccess("$COUNT_PATH?entity=Course", HttpMethod.GET).successful)
    }

    private static boolean authorize(String queryString, HttpServletResponse response) {
        HttpServletRequest request = mock(HttpServletRequest)
        when(request.getRequestURI()).thenReturn(COUNT_PATH)
        when(request.getMethod()).thenReturn(HttpMethod.GET)
        when(request.getQueryString()).thenReturn(queryString)

        ServletRequestEvent event = new ServletRequestEvent(mock(ServletContext), request)
        servletEnvironment.requestInitialized(event)
        try {
            return permissionService.authorize(request, response)
        } finally {
            servletEnvironment.requestDestroyed(event)
        }
    }

    private static SystemUser user(boolean admin, KeyCode keyCode = null, Integer mask = null) {
        List<ACLRole> roles = []
        if (keyCode != null) {
            ACLAccessKey key = mock(ACLAccessKey)
            when(key.getKeycode()).thenReturn(keyCode)
            when(key.getMask()).thenReturn(mask)
            ACLRole role = mock(ACLRole)
            when(role.getAccessKeys()).thenReturn([key])
            roles << role
        }
        SystemUser user = mock(SystemUser)
        when(user.getIsAdmin()).thenReturn(admin)
        when(user.getAclRoles()).thenReturn(roles)
        return user
    }

    private static class CurrentUserService implements ISystemUserService {

        SystemUser currentUser

        @Override
        List<SystemUser> getSystemUsersByRole(String name) {
            return null
        }

        @Override
        SystemUser getCurrentUser() {
            return currentUser
        }
    }
}
