/*
 * Copyright ish group pty ltd 2026.
 *
 * This program is free software: you can redistribute it and/or modify it under the terms of the GNU Affero General Public License version 3 as published by the Free Software Foundation.
 *
 *  This program is distributed in the hope that it will be useful, but WITHOUT ANY WARRANTY; without even the implied warranty of MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE. See the GNU Affero General Public License for more details.
 */

package ish.oncourse.server.assistant

import groovy.transform.CompileStatic
import ish.common.types.SystemEventType
import ish.oncourse.common.SystemEvent
import ish.oncourse.server.cayenne.SystemUser
import ish.oncourse.server.integration.EventService
import org.apache.cayenne.ObjectId
import org.junit.jupiter.api.Assertions
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.Test

/**
 * The session store posts USER_LOGGED_OUT when a session is deleted (logout, kick out or expiry).
 */
@CompileStatic
class AssistantLogoutListenerTest {

    private EventService events
    private AssistantConversationStore store
    private AssistantConversationStore.Conversation annConversation
    private AssistantConversationStore.Conversation bobConversation

    @BeforeEach
    void setUp() {
        events = new EventService()
        store = new AssistantConversationStore()
        new AssistantLogoutListener(events, store)

        annConversation = store.start(1L, false)
        store.save(annConversation)
        bobConversation = store.start(2L, false)
        store.save(bobConversation)
    }

    @Test
    void logoutDropsTheUsersConversations() {
        events.postEvent(SystemEvent.valueOf(SystemEventType.USER_LOGGED_OUT, user(1L)))

        Assertions.assertNull(store.find(annConversation.id, 1L))
        Assertions.assertSame(bobConversation, store.find(bobConversation.id, 2L))
    }

    @Test
    void otherEventsKeepConversations() {
        events.postEvent(SystemEvent.valueOf(SystemEventType.USER_LOGGED_IN, user(1L)))
        events.postEvent(SystemEvent.valueOf(SystemEventType.USER_LOGGED_OUT, null))
        events.postEvent(SystemEvent.valueOf(SystemEventType.USER_LOGGED_OUT, new ObjectId('Contact', 'id', 1L)))

        Assertions.assertEquals(2, store.size())
    }

    /**
     * A user as the session store has it, read from the database: the event keeps only its ObjectId.
     */
    private static SystemUser user(Long id) {
        SystemUser user = new SystemUser()
        user.objectId = new ObjectId(SystemUser.simpleName, SystemUser.ID_PK_COLUMN, id)
        return user
    }
}
