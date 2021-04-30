/**
 * Copyright ish group pty ltd. All rights reserved. http://www.ish.com.au
 * No copying or use of this code is allowed without permission in writing from ish.
 */
package ish.oncourse.server.cayenne

import ish.CayenneIshTestCase
import ish.common.types.MessageStatus
import ish.oncourse.server.ICayenneService
import org.apache.cayenne.ObjectContext
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.Test

import static org.junit.Assert.assertFalse
import static org.junit.Assert.assertTrue

class MessagePersonTest extends CayenneIshTestCase {

	private ICayenneService cayenneService

    @BeforeEach
    void setup() {
		this.cayenneService = injector.getInstance(ICayenneService.class)
    }

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

        assertFalse(checkStatusChangeAvailability(context, MessageStatus.QUEUED, null))
        assertTrue(checkStatusChangeAvailability(context, MessageStatus.QUEUED, MessageStatus.SENT))
        assertTrue(checkStatusChangeAvailability(context, MessageStatus.QUEUED, MessageStatus.FAILED))

        assertFalse(checkStatusChangeAvailability(context, MessageStatus.SENT, null))
        assertFalse(checkStatusChangeAvailability(context, MessageStatus.SENT, MessageStatus.QUEUED))
        assertFalse(checkStatusChangeAvailability(context, MessageStatus.SENT, MessageStatus.FAILED))

        assertFalse(checkStatusChangeAvailability(context, MessageStatus.FAILED, null))
        assertFalse(checkStatusChangeAvailability(context, MessageStatus.FAILED, MessageStatus.QUEUED))
        assertFalse(checkStatusChangeAvailability(context, MessageStatus.FAILED, MessageStatus.SENT))

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
