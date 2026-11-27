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

import groovy.transform.CompileStatic

import java.util.concurrent.TimeUnit

/**
 * Limits how many messages each user sends to the onCourse Assistant in a minute, so that one user can't run up
 * the AI model's costs or crowd out other users.
 */
@CompileStatic
class AssistantRateLimiter {

    static final long WINDOW_MILLIS = TimeUnit.MINUTES.toMillis(1)

    private final int maxMessagesPerMinute
    // guarded by this: when each user's messages of the last minute were sent, oldest first
    private final Map<Long, ArrayDeque<Long>> sent = new HashMap<>()

    /**
     * @param maxMessagesPerMinute messages a user can send in a minute, 0 or less for no limit
     */
    AssistantRateLimiter(int maxMessagesPerMinute) {
        this.maxMessagesPerMinute = maxMessagesPerMinute
    }

    /**
     * Counts a message of the user if they haven't sent the most messages they can in the last minute.
     *
     * @return false if the user can't send a message now
     */
    synchronized boolean tryAcquire(Long userId) {
        if (maxMessagesPerMinute <= 0) {
            return true
        }
        long now = currentTimeMillis()
        // users who sent nothing in the last minute are dropped, so the map holds only recent senders
        sent.values().each { ArrayDeque<Long> times ->
            while (!times.isEmpty() && now - times.peekFirst() >= WINDOW_MILLIS) {
                times.pollFirst()
            }
        }
        sent.values().removeIf { ArrayDeque<Long> times -> times.isEmpty() }

        ArrayDeque<Long> times = sent.computeIfAbsent(userId) { Long it -> new ArrayDeque<Long>() }
        if (times.size() >= maxMessagesPerMinute) {
            return false
        }
        times.addLast(now)
        return true
    }

    protected long currentTimeMillis() {
        return System.currentTimeMillis()
    }
}
