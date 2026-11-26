/*
 * Copyright ish group pty ltd 2026.
 *
 * This program is free software: you can redistribute it and/or modify it under the terms of the GNU Affero General Public License version 3 as published by the Free Software Foundation.
 *
 *  This program is distributed in the hope that it will be useful, but WITHOUT ANY WARRANTY; without even the implied warranty of MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE. See the GNU Affero General Public License for more details.
 */

package ish.oncourse.server.assistant

import groovy.transform.CompileStatic
import ish.oncourse.server.assistant.model.ModelMessage
import org.junit.jupiter.api.Assertions
import org.junit.jupiter.api.Test

@CompileStatic
class AssistantConversationStoreTest {

    static class ClockedStore extends AssistantConversationStore {
        long now = 1_000_000L

        @Override
        protected long currentTimeMillis() {
            return now
        }
    }

    private ClockedStore store = new ClockedStore()

    @Test
    void conversationIsFoundByIdForItsOwner() {
        AssistantConversationStore.Conversation conversation = store.getOrCreate(null, 1L)

        Assertions.assertSame(conversation, store.getOrCreate(conversation.id, 1L))
        Assertions.assertEquals(1, store.size())
    }

    @Test
    void unknownIdOrOtherOwnerStartsNewConversation() {
        AssistantConversationStore.Conversation conversation = store.getOrCreate(null, 1L)

        Assertions.assertNotEquals(conversation.id, store.getOrCreate('unknown', 1L).id)
        Assertions.assertNotEquals(conversation.id, store.getOrCreate(conversation.id, 2L).id)
        Assertions.assertSame(conversation, store.getOrCreate(conversation.id, 1L))
    }

    @Test
    void historyKeepsLatestExchangesStartingWithUser() {
        AssistantConversationStore.Conversation conversation = store.getOrCreate(null, 1L)

        (1..30).each { int i -> conversation.addExchange(ModelMessage.user("q$i".toString()), ModelMessage.assistant("a$i".toString())) }

        List<ModelMessage> messages = conversation.messages
        Assertions.assertEquals(AssistantConversationStore.MAX_MESSAGES, messages.size())
        Assertions.assertEquals(ModelMessage.user('q11'), messages.first())
        Assertions.assertEquals(ModelMessage.assistant('a30'), messages.last())
    }

    @Test
    void idleConversationsAreDropped() {
        AssistantConversationStore.Conversation idle = store.getOrCreate(null, 1L)
        store.now += AssistantConversationStore.MAX_IDLE_MILLIS + 1

        Assertions.assertNotEquals(idle.id, store.getOrCreate(idle.id, 1L).id)
        Assertions.assertEquals(1, store.size())
    }

    @Test
    void clearForUserDropsOnlyTheirConversations() {
        store.getOrCreate(null, 1L)
        store.getOrCreate(null, 1L)
        AssistantConversationStore.Conversation other = store.getOrCreate(null, 2L)

        store.clearForUser(1L)

        Assertions.assertEquals(1, store.size())
        Assertions.assertSame(other, store.getOrCreate(other.id, 2L))
    }

    @Test
    void numberOfConversationsIsCapped() {
        AssistantConversationStore.Conversation oldest = store.getOrCreate(null, 1L)
        (1..AssistantConversationStore.MAX_CONVERSATIONS).each {
            store.now++
            store.getOrCreate(null, 1L)
        }

        Assertions.assertEquals(AssistantConversationStore.MAX_CONVERSATIONS, store.size())
        Assertions.assertNotEquals(oldest.id, store.getOrCreate(oldest.id, 1L).id)
    }
}
