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
import java.util.concurrent.atomic.AtomicBoolean

/**
 * Keeps onCourse Assistant conversations in memory on this server. A conversation belongs to the user who started it.
 * A new conversation is kept only after its first successful exchange. Conversations idle for too long are dropped,
 * each user keeps only their latest conversations, and each conversation keeps only its latest messages.
 */
@Singleton
@CompileStatic
class AssistantConversationStore {

    static final int MAX_MESSAGES = 40
    // all messages of a conversation together; the latest exchange is kept even if it is longer
    static final int MAX_CHARACTERS = 60_000
    static final int MAX_CONVERSATIONS_PER_USER = 5
    // a backstop for all users together
    static final int MAX_CONVERSATIONS = 1000
    static final long MAX_IDLE_MILLIS = TimeUnit.HOURS.toMillis(12)

    static class Conversation {

        final String id
        final Long userId
        private final long generation
        private final List<ModelMessage> messages = []
        private final AtomicBoolean sending = new AtomicBoolean()
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
         * Adds a user message and the assistant's reply, dropping the oldest exchanges over the limits,
         * so the conversation still starts with a user message.
         */
        synchronized void addExchange(ModelMessage user, ModelMessage assistant) {
            messages.add(user)
            messages.add(assistant)
            while (messages.size() > MAX_MESSAGES || (messages.size() > 2 && characters() > MAX_CHARACTERS)) {
                messages.remove(0)
                messages.remove(0)
            }
        }

        /**
         * Marks a message of the conversation as waiting for the model's reply.
         *
         * @return false if another message of the conversation is waiting already
         */
        boolean startSending() {
            return sending.compareAndSet(false, true)
        }

        void finishSending() {
            sending.set(false)
        }

        private int characters() {
            int characters = 0
            for (ModelMessage message : messages) {
                characters += message.text.length()
            }
            return characters
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
     * over the limits: the user's own first, so a user never drops other users' conversations unless the server
     * holds too many. A conversation started before the user's conversations were cleared isn't kept,
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
            evictOldestOverLimit(MAX_CONVERSATIONS_PER_USER) { Conversation it -> it.userId == conversation.userId }
            evictOldestOverLimit(MAX_CONVERSATIONS) { Conversation it -> true }
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

    /**
     * @return ids of the users who have kept conversations
     */
    synchronized Set<Long> userIds() {
        return conversations.values().collect { Conversation it -> it.userId } as Set<Long>
    }

    synchronized int size() {
        return conversations.size()
    }

    synchronized int size(Long userId) {
        return conversations.values().count { Conversation it -> it.userId == userId } as int
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

    /**
     * Drops the least recently used of the matching conversations until there is room for one more.
     */
    private void evictOldestOverLimit(int limit, Closure<Boolean> matching) {
        List<Conversation> candidates = conversations.values().findAll(matching).sort { Conversation it -> it.lastUsed }
        for (int i = 0; i <= candidates.size() - limit; i++) {
            conversations.remove(candidates[i].id)
        }
    }
}
