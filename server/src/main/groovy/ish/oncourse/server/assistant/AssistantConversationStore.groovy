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

package ish.oncourse.server.assistant

import com.google.inject.Singleton
import groovy.transform.CompileStatic
import ish.oncourse.server.assistant.model.ModelMessage

import java.util.concurrent.TimeUnit

/**
 * Keeps onCourse Assistant conversations in memory on this server. A conversation belongs to the user who started it.
 * A new conversation is kept only after its first successful exchange. Conversations idle for too long are dropped,
 * and each keeps only its latest messages.
 */
@Singleton
@CompileStatic
class AssistantConversationStore {

    static final int MAX_MESSAGES = 40
    static final int MAX_CONVERSATIONS = 1000
    static final long MAX_IDLE_MILLIS = TimeUnit.HOURS.toMillis(12)

    static class Conversation {

        final String id
        final Long userId
        private final long generation
        private final List<ModelMessage> messages = []
        volatile long lastUsed

        Conversation(String id, Long userId, long generation, long now) {
            this.id = id
            this.userId = userId
            this.generation = generation
            this.lastUsed = now
        }

        /**
         * @return a copy of the messages, oldest first
         */
        synchronized List<ModelMessage> getMessages() {
            return new ArrayList<ModelMessage>(messages)
        }

        /**
         * Adds a user message and the assistant's reply, dropping the oldest exchanges over the limit,
         * so the conversation still starts with a user message.
         */
        synchronized void addExchange(ModelMessage user, ModelMessage assistant) {
            messages.add(user)
            messages.add(assistant)
            while (messages.size() > MAX_MESSAGES) {
                messages.remove(0)
                messages.remove(0)
            }
        }
    }

    // guarded by this, so that the cap check and the insertion are atomic
    private final Map<String, Conversation> conversations = new HashMap<>()
    // guarded by this: how many times each user's conversations were cleared
    private final Map<Long, Long> generations = new HashMap<>()

    /**
     * Returns the user's conversation with this id, or null when there is no id, the conversation is gone
     * or it belongs to someone else.
     */
    synchronized Conversation find(String id, Long userId) {
        long now = currentTimeMillis()
        evictIdle(now)

        Conversation conversation = id ? conversations.get(id) : null
        if (conversation == null || conversation.userId != userId) {
            return null
        }
        conversation.lastUsed = now
        return conversation
    }

    /**
     * Starts a new conversation of the user. It isn't kept until it is saved, so a conversation whose first
     * message failed never takes the place of other conversations.
     */
    synchronized Conversation start(Long userId) {
        return new Conversation(UUID.randomUUID().toString(), userId, generation(userId), currentTimeMillis())
    }

    /**
     * Keeps the conversation after a successful exchange, dropping the least recently used conversations
     * over the limit. A conversation started before the user's conversations were cleared isn't kept,
     * so a reply that arrives after logout doesn't bring it back.
     */
    synchronized void save(Conversation conversation) {
        if (conversation.generation != generation(conversation.userId)) {
            return
        }
        long now = currentTimeMillis()
        evictIdle(now)
        conversation.lastUsed = now
        if (!conversations.containsKey(conversation.id)) {
            evictOldestOverLimit()
            conversations.put(conversation.id, conversation)
        }
    }

    /**
     * Drops all conversations of the user, including the ones still waiting for their first reply.
     */
    synchronized void clearForUser(Long userId) {
        conversations.values().removeIf { Conversation it -> it.userId == userId }
        generations.put(userId, generation(userId) + 1)
    }

    synchronized int size() {
        return conversations.size()
    }

    protected long currentTimeMillis() {
        return System.currentTimeMillis()
    }

    private long generation(Long userId) {
        Long generation = generations.get(userId)
        return generation == null ? 0L : generation
    }

    private void evictIdle(long now) {
        conversations.values().removeIf { Conversation it -> now - it.lastUsed > MAX_IDLE_MILLIS }
    }

    private void evictOldestOverLimit() {
        while (conversations.size() >= MAX_CONVERSATIONS) {
            Conversation oldest = conversations.values().min { Conversation it -> it.lastUsed }
            if (oldest == null) {
                return
            }
            conversations.remove(oldest.id)
        }
    }
}
