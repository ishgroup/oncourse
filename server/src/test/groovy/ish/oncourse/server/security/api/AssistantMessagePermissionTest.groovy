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
import ish.oncourse.server.PreferenceController
import ish.oncourse.server.assistant.AssistantConversationStore
import ish.oncourse.server.preference.AssistantPreferencesTest
import ish.oncourse.server.services.ISystemUserService
import org.junit.jupiter.api.Assertions
import org.junit.jupiter.api.BeforeAll
import org.junit.jupiter.api.BeforeEach
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
 * The assistant message API is open to every logged-in user the onCourse Assistant is available to,
 * and the permission filter refuses everyone else with a JSON 403.
 */
@CompileStatic
@TestInstance(TestInstance.Lifecycle.PER_CLASS)
class AssistantMessagePermissionTest {

    private static final String MESSAGE_PATH = '/a/v1/assistant/message'

    private AssistantPreferencesPermissionTest.CurrentUserService userService = new AssistantPreferencesPermissionTest.CurrentUserService()

    private AssistantPreferencesTest.InMemoryPreferenceController preferences = new AssistantPreferencesTest.InMemoryPreferenceController()

    private AssistantConversationStore conversations = new AssistantConversationStore()

    private IPermissionService permissionService

    @BeforeAll
    void createPermissionService() {
        // permissions of the real REST API, as configured in application.yml
        PermissionServiceFactory factory = new PermissionServiceFactory()
        factory.setApiPackage('ish.oncourse.server.api.v1.service')

        ISystemUserService users = userService
        PreferenceController controller = preferences
        AssistantConversationStore store = conversations
        permissionService = factory.createPermissionService(Guice.createInjector(new AbstractModule() {
            @Override
            protected void configure() {
                bind(ISystemUserService).toInstance(users)
                bind(PreferenceController).toInstance(controller)
                bind(AssistantConversationStore).toInstance(store)
            }
        }))
    }

    @BeforeEach
    void resetPreferences() {
        preferences.values.clear()
    }

    @Test
    void adminCanSendWhenAssistantIsEnabled() {
        preferences.setAssistantEnabled(true)
        userService.currentUser.setIsAdmin(true)

        Assertions.assertTrue(permissionService.hasAccess(MESSAGE_PATH, HttpMethod.POST).isSuccessful())
        Assertions.assertTrue(authorize())
    }

    @Test
    void nonAdminCanSendWhenAssistantIsForAllUsers() {
        preferences.setAssistantEnabled(true)
        preferences.setAssistantAdminOnly(false)
        userService.currentUser.setIsAdmin(false)

        Assertions.assertTrue(permissionService.hasAccess(MESSAGE_PATH, HttpMethod.POST).isSuccessful())
        Assertions.assertTrue(authorize())
    }

    @Test
    void nonAdminCannotSendWhenAssistantIsForAdminsOnly() {
        preferences.setAssistantEnabled(true)
        userService.currentUser.setIsAdmin(false)

        Assertions.assertFalse(permissionService.hasAccess(MESSAGE_PATH, HttpMethod.POST).isSuccessful())
        Assertions.assertFalse(authorize())
    }

    @Test
    void nobodyCanSendWhenAssistantIsDisabled() {
        preferences.setAssistantAdminOnly(false)

        [true, false].each { boolean isAdmin ->
            userService.currentUser.setIsAdmin(isAdmin)

            Assertions.assertFalse(permissionService.hasAccess(MESSAGE_PATH, HttpMethod.POST).isSuccessful())
            Assertions.assertFalse(authorize())
        }
    }

    @Test
    void refusalDropsTheUsersConversations() {
        preferences.setAssistantEnabled(true)
        userService.currentUser.setIsAdmin(true)
        userService.currentUser.setId(7L)
        AssistantConversationStore.Conversation own = conversations.start(7L)
        conversations.save(own)
        AssistantConversationStore.Conversation other = conversations.start(8L)
        conversations.save(other)

        Assertions.assertTrue(permissionService.hasAccess(MESSAGE_PATH, HttpMethod.POST).isSuccessful())
        Assertions.assertSame(own, conversations.find(own.id, 7L))

        // the Admin toggle was turned off
        userService.currentUser.setIsAdmin(false)
        Assertions.assertFalse(permissionService.hasAccess(MESSAGE_PATH, HttpMethod.POST).isSuccessful())
        Assertions.assertNull(conversations.find(own.id, 7L))
        Assertions.assertSame(other, conversations.find(other.id, 8L))
    }

    private boolean authorize() {
        HttpServletRequest request = mock(HttpServletRequest)
        when(request.getRequestURI()).thenReturn(MESSAGE_PATH)
        when(request.getMethod()).thenReturn(HttpMethod.POST)
        HttpServletResponse response = mock(HttpServletResponse)
        StringWriter body = new StringWriter()
        when(response.getWriter()).thenReturn(new PrintWriter(body))

        boolean authorized = permissionService.authorize(request, response)

        if (authorized) {
            verify(response, never()).setStatus(HttpServletResponse.SC_FORBIDDEN)
            Assertions.assertEquals('', body.toString())
        } else {
            // a JSON error the client shows, rather than the Jetty 403 page
            verify(response).setStatus(HttpServletResponse.SC_FORBIDDEN)
            verify(response).setContentType('application/json')
            Assertions.assertTrue(body.toString().contains('The onCourse Assistant is not available to you'), body.toString())
        }
        return authorized
    }
}
