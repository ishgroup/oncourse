/**
 * Copyright ish group pty ltd. All rights reserved. http://www.ish.com.au
 * No copying or use of this code is allowed without permission in writing from ish.
 */
package ish.oncourse.server.cayenne

import groovy.transform.CompileStatic
import ish.TestWithDatabase
import ish.common.types.MessageStatus
import org.apache.cayenne.ObjectContext
import org.junit.jupiter.api.Assertions
import org.junit.jupiter.api.Test

@CompileStatic
class MessagePersonTest extends TestWithDatabase {

    @Test
    void testStatusConstraints() {

        /**
         * Allowed status changes:<br>
         * <ul>
         * <li>QUEUED -> SENT/FAILED</li>
         * <li>SENT/FAILED -> cannot be modified</li>
         * </ul>
         */

        ObjectContext context = cayenneService.getNewNonReplicatingContext()

        Assertions.assertFalse(checkStatusChangeAvailability(context, MessageStatus.QUEUED, null))
        Assertions.assertTrue(checkStatusChangeAvailability(context, MessageStatus.QUEUED, MessageStatus.SENT))
        Assertions.assertTrue(checkStatusChangeAvailability(context, MessageStatus.QUEUED, MessageStatus.FAILED))

        Assertions.assertFalse(checkStatusChangeAvailability(context, MessageStatus.SENT, null))
        Assertions.assertFalse(checkStatusChangeAvailability(context, MessageStatus.SENT, MessageStatus.QUEUED))
        Assertions.assertFalse(checkStatusChangeAvailability(context, MessageStatus.SENT, MessageStatus.FAILED))

        Assertions.assertFalse(checkStatusChangeAvailability(context, MessageStatus.FAILED, null))
        Assertions.assertFalse(checkStatusChangeAvailability(context, MessageStatus.FAILED, MessageStatus.QUEUED))
        Assertions.assertFalse(checkStatusChangeAvailability(context, MessageStatus.FAILED, MessageStatus.SENT))

    }

    
    private boolean checkStatusChangeAvailability(ObjectContext context, MessageStatus from, MessageStatus to) {
        try {
            MessagePerson mp = context.newObject(MessagePerson.class)

            mp.setStatus(from)
            mp.setStatus(to)

            return true
        } catch (IllegalArgumentException e) {
            return false
        }
    }
}
