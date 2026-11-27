/*
 * Copyright ish group pty ltd 2026.
 *
 * This program is free software: you can redistribute it and/or modify it under the terms of the GNU Affero General Public License version 3 as published by the Free Software Foundation.
 *
 *  This program is distributed in the hope that it will be useful, but WITHOUT ANY WARRANTY; without even the implied warranty of MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE. See the GNU Affero General Public License for more details.
 */

package ish.oncourse.server.assistant

import groovy.transform.CompileStatic
import org.junit.jupiter.api.Assertions
import org.junit.jupiter.api.Test

@CompileStatic
class AssistantRateLimiterTest {

    static class ClockedLimiter extends AssistantRateLimiter {
        long now = 1_000_000L

        ClockedLimiter(int maxMessagesPerMinute) {
            super(maxMessagesPerMinute)
        }

        @Override
        protected long currentTimeMillis() {
            return now
        }
    }

    @Test
    void userCanSendTheLimitInAMinute() {
        ClockedLimiter limiter = new ClockedLimiter(3)

        (1..3).each {
            Assertions.assertTrue(limiter.tryAcquire(1L))
            limiter.now += 1000
        }
        Assertions.assertFalse(limiter.tryAcquire(1L))
        // refused messages aren't counted
        Assertions.assertFalse(limiter.tryAcquire(1L))
        Assertions.assertTrue(limiter.tryAcquire(2L))

        // a minute after the first message
        limiter.now = 1_000_000L + AssistantRateLimiter.WINDOW_MILLIS
        Assertions.assertTrue(limiter.tryAcquire(1L))
        Assertions.assertFalse(limiter.tryAcquire(1L))
    }

    @Test
    void zeroIsNoLimit() {
        AssistantRateLimiter limiter = new AssistantRateLimiter(0)

        (1..100).each { Assertions.assertTrue(limiter.tryAcquire(1L)) }
    }
}
