/*
 * Copyright ish group pty ltd 2026.
 *
 * This program is free software: you can redistribute it and/or modify it under the terms of the GNU Affero General Public License version 3 as published by the Free Software Foundation.
 *
 *  This program is distributed in the hope that it will be useful, but WITHOUT ANY WARRANTY; without even the implied warranty of MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE. See the GNU Affero General Public License for more details.
 */

package ish.oncourse.server.assistant

import groovy.transform.CompileStatic
import ish.DatabaseSetup
import ish.TestWithDatabase
import ish.oncourse.server.PreferenceController
import ish.oncourse.server.api.v1.model.ValidationErrorDTO
import ish.oncourse.server.assistant.model.AssistantModelException
import ish.oncourse.server.assistant.model.ModelMessage
import ish.oncourse.server.assistant.model.ModelRequest
import ish.oncourse.server.assistant.model.ScriptedModelProvider
import ish.oncourse.server.cayenne.SystemUser
import ish.oncourse.server.services.ISystemUserService
import org.junit.jupiter.api.Assertions
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.function.Executable

import javax.ws.rs.WebApplicationException

import static org.mockito.Mockito.mock
import static org.mockito.Mockito.when

/**
 * A message to the onCourse Assistant goes through the conversation service to the AI model (the scripted fake here),
 * with the assistant settings read from the database.
 */
@CompileStatic
@DatabaseSetup(value = 'ish/oncourse/server/assistant/assistantDataSet.xml')
class AssistantConversationServiceTest extends TestWithDatabase {

    private static final Long ADMIN_ID = 1L
    private static final Long ANN_ID = 2L

    private PreferenceController preferences
    private ISystemUserService users
    private ScriptedModelProvider fake
    private AssistantConversationStore store
    private AssistantConversationService service

    @BeforeEach
    void createService() {
        preferences = injector.getInstance(PreferenceController)
        preferences.setAssistantEnabled(true)
        preferences.setAssistantAdminOnly(true)

        users = mock(ISystemUserService)
        loginAs(ADMIN_ID)

        fake = new ScriptedModelProvider(['Hello from fake'])
        store = new AssistantConversationStore()
        service = new AssistantConversationService(preferences, users, fake, store)
    }

    @Test
    void replyComesFromModel() {
        AssistantConversationService.Reply reply = service.send(null, 'Create a course')

        Assertions.assertEquals('Hello from fake', reply.text)
        Assertions.assertFalse(reply.conversationId.trim().isEmpty())

        Assertions.assertEquals(1, fake.requests.size())
        ModelRequest request = fake.requests[0]
        Assertions.assertEquals([ModelMessage.user('Create a course')], request.messages)
        Assertions.assertEquals(AssistantConversationService.SYSTEM_PROMPT, request.systemPrompt)
        Assertions.assertFalse(request.systemPrompt.trim().isEmpty())
    }

    @Test
    void historyIsKeptInConversation() {
        AssistantConversationService.Reply first = service.send(null, 'Create a course')
        AssistantConversationService.Reply second = service.send(first.conversationId, 'Called Pottery')

        Assertions.assertEquals(first.conversationId, second.conversationId)
        Assertions.assertEquals([ModelMessage.user('Create a course'), ModelMessage.assistant('Hello from fake'),
                                 ModelMessage.user('Called Pottery')], fake.requests[1].messages)
    }

    @Test
    void otherUsersConversationIsNotReused() {
        preferences.setAssistantAdminOnly(false)
        AssistantConversationService.Reply adminReply = service.send(null, 'Create a course')

        loginAs(ANN_ID)
        AssistantConversationService.Reply annReply = service.send(adminReply.conversationId, 'What did I ask?')

        Assertions.assertNotEquals(adminReply.conversationId, annReply.conversationId)
        Assertions.assertEquals([ModelMessage.user('What did I ask?')], fake.requests[1].messages)
    }

    @Test
    void refusesWhenDisabled() {
        preferences.setAssistantEnabled(false)

        assertError(403, AssistantConversationService.NOT_AVAILABLE_MSG) { service.send(null, 'Create a course') }
        Assertions.assertTrue(fake.requests.isEmpty())
    }

    @Test
    void refusesNonAdminWhenAdminOnly() {
        loginAs(ANN_ID)

        assertError(403, AssistantConversationService.NOT_AVAILABLE_MSG) { service.send(null, 'Create a course') }
        Assertions.assertTrue(fake.requests.isEmpty())

        preferences.setAssistantAdminOnly(false)
        Assertions.assertEquals('Hello from fake', service.send(null, 'Create a course').text)
    }

    @Test
    void blankOrTooLongTextIs400() {
        assertError(400, AssistantConversationService.BLANK_TEXT_MSG) { service.send(null, '   ') }
        assertError(400, AssistantConversationService.BLANK_TEXT_MSG) { service.send(null, null) }
        assertError(400, AssistantConversationService.TOO_LONG_TEXT_MSG) { service.send(null, 'a' * 4001) }
        Assertions.assertTrue(fake.requests.isEmpty())

        Assertions.assertEquals('Hello from fake', service.send(null, 'a' * 4000).text)
    }

    @Test
    void modelFailureIs503AndHistoryUnchanged() {
        AssistantConversationService.Reply first = service.send(null, 'Create a course')
        fake.failNext(new AssistantModelException('boom'))

        assertError(503, AssistantConversationService.UNAVAILABLE_MSG) { service.send(first.conversationId, 'Lost message') }

        service.send(first.conversationId, 'Try again')
        Assertions.assertEquals([ModelMessage.user('Create a course'), ModelMessage.assistant('Hello from fake'),
                                 ModelMessage.user('Try again')], fake.requests[2].messages)
    }

    @Test
    void unexpectedProviderFailureIs503() {
        AssistantConversationService.Reply first = service.send(null, 'Create a course')
        fake.failNext(new IllegalStateException('provider bug quoting Lost message'))

        assertError(503, AssistantConversationService.UNAVAILABLE_MSG) { service.send(first.conversationId, 'Lost message') }

        AssistantConversationService.Reply retry = service.send(first.conversationId, 'Try again')
        Assertions.assertEquals(first.conversationId, retry.conversationId)
        Assertions.assertEquals([ModelMessage.user('Create a course'), ModelMessage.assistant('Hello from fake'),
                                 ModelMessage.user('Try again')], fake.requests[2].messages)
    }

    @Test
    void failureIsDescribedWithoutTheUsersText() {
        RuntimeException failure = new RuntimeException('wrapped',
                new AssistantModelException('Bedrock call failed: ValidationException',
                        new IllegalArgumentException('messages.0: Lost message')))

        String description = AssistantConversationService.describeFailure(failure)

        Assertions.assertFalse(description.contains('Lost message'))
        Assertions.assertFalse(description.contains('wrapped'))
        Assertions.assertTrue(description.startsWith('java.lang.RuntimeException caused by ' +
                'ish.oncourse.server.assistant.model.AssistantModelException: Bedrock call failed: ValidationException ' +
                'caused by java.lang.IllegalArgumentException at '), description)
    }

    @Test
    void failedFirstMessageKeepsNoConversation() {
        fake.failNext(new AssistantModelException('boom'))

        assertError(503, AssistantConversationService.UNAVAILABLE_MSG) { service.send(null, 'Lost message') }
        Assertions.assertEquals(0, store.size())

        AssistantConversationService.Reply retry = service.send(null, 'Try again')
        Assertions.assertEquals(1, store.size())
        Assertions.assertEquals([ModelMessage.user('Try again')], fake.requests[1].messages)
        Assertions.assertEquals(retry.conversationId, service.send(retry.conversationId, 'And again').conversationId)
    }

    @Test
    void refusalDropsTheUsersConversations() {
        AssistantConversationService.Reply first = service.send(null, 'Create a course')
        Assertions.assertEquals(1, store.size())

        preferences.setAssistantEnabled(false)
        assertError(403, AssistantConversationService.NOT_AVAILABLE_MSG) { service.send(first.conversationId, 'Hello') }
        Assertions.assertEquals(0, store.size())

        preferences.setAssistantEnabled(true)
        AssistantConversationService.Reply afterEnabled = service.send(first.conversationId, 'Hello')
        Assertions.assertNotEquals(first.conversationId, afterEnabled.conversationId)
        Assertions.assertEquals([ModelMessage.user('Hello')], fake.requests[1].messages)
    }

    private void loginAs(Long id) {
        SystemUser user = getRecordWithId(cayenneContext, SystemUser, id)
        when(users.getCurrentUser()).thenReturn(user)
    }

    private static void assertError(int status, String message, Closure call) {
        WebApplicationException e = Assertions.assertThrows(WebApplicationException, call as Executable)
        Assertions.assertEquals(status, e.response.status)
        Assertions.assertEquals(message, (e.response.entity as ValidationErrorDTO).errorMessage)
    }
}
