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

import java.util.concurrent.CountDownLatch
import java.util.concurrent.ExecutorService
import java.util.concurrent.Executors
import java.util.concurrent.Future
import java.util.concurrent.TimeUnit

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
        AssistantConversationStore.Conversation conversation = saved(1L)

        Assertions.assertSame(conversation, store.find(conversation.id, 1L))
        Assertions.assertEquals(1, store.size())
    }

    @Test
    void unknownIdOrOtherOwnerFindsNothing() {
        AssistantConversationStore.Conversation conversation = saved(1L)

        Assertions.assertNull(store.find(null, 1L))
        Assertions.assertNull(store.find('unknown', 1L))
        Assertions.assertNull(store.find(conversation.id, 2L))
        Assertions.assertSame(conversation, store.find(conversation.id, 1L))
    }

    @Test
    void startedConversationIsKeptOnlyWhenSaved() {
        AssistantConversationStore.Conversation conversation = store.start(1L)

        Assertions.assertEquals(0, store.size())
        Assertions.assertNull(store.find(conversation.id, 1L))

        store.save(conversation)
        store.save(conversation)

        Assertions.assertEquals(1, store.size())
        Assertions.assertSame(conversation, store.find(conversation.id, 1L))
    }

    @Test
    void historyKeepsLatestExchangesStartingWithUser() {
        AssistantConversationStore.Conversation conversation = store.start(1L)

        (1..30).each { int i -> conversation.addExchange(ModelMessage.user("q$i".toString()), ModelMessage.assistant("a$i".toString())) }

        List<ModelMessage> messages = conversation.messages
        Assertions.assertEquals(AssistantConversationStore.MAX_MESSAGES, messages.size())
        Assertions.assertEquals(ModelMessage.user('q11'), messages.first())
        Assertions.assertEquals(ModelMessage.assistant('a30'), messages.last())
    }

    @Test
    void idleConversationsAreDropped() {
        AssistantConversationStore.Conversation idle = saved(1L)
        store.now += AssistantConversationStore.MAX_IDLE_MILLIS + 1

        Assertions.assertNull(store.find(idle.id, 1L))
        Assertions.assertEquals(0, store.size())
    }

    @Test
    void clearForUserDropsOnlyTheirConversations() {
        saved(1L)
        saved(1L)
        AssistantConversationStore.Conversation other = saved(2L)

        store.clearForUser(1L)

        Assertions.assertEquals(1, store.size())
        Assertions.assertSame(other, store.find(other.id, 2L))
    }

    @Test
    void conversationInProgressIsNotKeptAfterClear() {
        AssistantConversationStore.Conversation existing = saved(1L)
        AssistantConversationStore.Conversation started = store.start(1L)

        store.clearForUser(1L)
        // replies to both arrive after the user logged out
        store.save(existing)
        store.save(started)

        Assertions.assertEquals(0, store.size())

        AssistantConversationStore.Conversation afterLogin = saved(1L)
        Assertions.assertSame(afterLogin, store.find(afterLogin.id, 1L))
    }

    @Test
    void historyIsBoundedByCharactersKeepingTheLatestExchange() {
        AssistantConversationStore.Conversation conversation = store.start(1L)
        String half = 'x' * (AssistantConversationStore.MAX_CHARACTERS.intdiv(5) as int)

        (1..3).each { int i -> conversation.addExchange(ModelMessage.user("q$i$half".toString()), ModelMessage.assistant(half)) }

        List<ModelMessage> messages = conversation.messages
        Assertions.assertEquals(4, messages.size())
        Assertions.assertEquals(ModelMessage.user("q2$half".toString()), messages.first())
        int characters = messages.sum(0) { ModelMessage it -> it.text.length() } as int
        Assertions.assertTrue(characters <= AssistantConversationStore.MAX_CHARACTERS)

        String huge = 'y' * AssistantConversationStore.MAX_CHARACTERS
        conversation.addExchange(ModelMessage.user('long'), ModelMessage.assistant(huge))
        Assertions.assertEquals([ModelMessage.user('long'), ModelMessage.assistant(huge)], conversation.messages)
    }

    @Test
    void newConversationDropsTheUsersOwnOldestOverTheirLimit() {
        AssistantConversationStore.Conversation other = saved(2L)
        List<AssistantConversationStore.Conversation> own = (1..AssistantConversationStore.MAX_CONVERSATIONS_PER_USER).collect {
            store.now++
            saved(1L)
        }
        // used recently, so the next oldest goes
        store.now++
        store.find(own[0].id, 1L)

        store.now++
        AssistantConversationStore.Conversation newest = saved(1L)

        Assertions.assertEquals(AssistantConversationStore.MAX_CONVERSATIONS_PER_USER, store.size(1L))
        Assertions.assertNull(store.find(own[1].id, 1L))
        ([own[0], newest] + own.drop(2)).each { Assertions.assertSame(it, store.find(it.id, 1L)) }
        Assertions.assertSame(other, store.find(other.id, 2L))
    }

    @Test
    void numberOfConversationsIsCapped() {
        AssistantConversationStore.Conversation oldest = saved(0L)
        (1..AssistantConversationStore.MAX_CONVERSATIONS).each { int i ->
            store.now++
            saved(i.toLong())
        }

        Assertions.assertEquals(AssistantConversationStore.MAX_CONVERSATIONS, store.size())
        Assertions.assertNull(store.find(oldest.id, 0L))
    }

    @Test
    void startedButUnsavedConversationsEvictNothing() {
        List<AssistantConversationStore.Conversation> kept = (1..AssistantConversationStore.MAX_CONVERSATIONS).collect { int i ->
            store.now++
            saved(i.toLong())
        }

        // first messages that failed
        (1..10).each { store.start(1L) }

        Assertions.assertEquals(AssistantConversationStore.MAX_CONVERSATIONS, store.size())
        kept.each { Assertions.assertSame(it, store.find(it.id, it.userId)) }
    }

    @Test
    void concurrentSavesNeverExceedTheCaps() {
        AssistantConversationStore realClock = new AssistantConversationStore()
        (1..AssistantConversationStore.MAX_CONVERSATIONS - 1).each { int i -> realClock.save(realClock.start(1000L + i)) }

        ExecutorService executor = Executors.newFixedThreadPool(8)
        try {
            CountDownLatch go = new CountDownLatch(1)
            List<Future<?>> saves = (1..200).collect { int i ->
                executor.submit({
                    go.await()
                    realClock.save(realClock.start((i % 5).toLong()))
                } as Runnable) as Future<?>
            }
            go.countDown()
            saves.each { it.get(10, TimeUnit.SECONDS) }
        } finally {
            executor.shutdownNow()
        }

        Assertions.assertEquals(AssistantConversationStore.MAX_CONVERSATIONS, realClock.size())
        (0..4).each { int user -> Assertions.assertTrue(realClock.size(user.toLong()) <= AssistantConversationStore.MAX_CONVERSATIONS_PER_USER) }
    }

    @Test
    void userIdsListsUsersWithKeptConversations() {
        saved(1L)
        saved(2L)
        saved(2L)
        store.start(3L)

        Assertions.assertEquals([1L, 2L] as Set<Long>, store.userIds())

        store.clearForUser(2L)
        Assertions.assertEquals([1L] as Set<Long>, store.userIds())
    }

    @Test
    void onlyOneMessageOfAConversationIsSentAtATime() {
        AssistantConversationStore.Conversation conversation = saved(1L)

        Assertions.assertTrue(conversation.startSending())
        Assertions.assertFalse(conversation.startSending())
        Assertions.assertTrue(saved(1L).startSending())

        conversation.finishSending()
        Assertions.assertTrue(conversation.startSending())
    }

    private AssistantConversationStore.Conversation saved(Long userId) {
        AssistantConversationStore.Conversation conversation = store.start(userId)
        store.save(conversation)
        return conversation
    }
}
