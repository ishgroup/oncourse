/*
 * Copyright ish group pty ltd. All rights reserved. http://www.ish.com.au No copying or use of this code is allowed without permission in writing from ish.
 */

package ish.oncourse.server.db

import ish.CayenneIshTestCase
import ish.oncourse.server.ICayenneService
import ish.oncourse.server.cayenne.Account
import ish.util.AccountUtil
import static junit.framework.TestCase.assertNotNull
import org.apache.cayenne.access.DataContext
import org.junit.Test

class SanityCheckServiceTest extends CayenneIshTestCase {

	@Test
    void testAccountDefaults() {
		DataContext newContext = injector.getInstance(ICayenneService.class).getNewNonReplicatingContext()
        assertNotNull(AccountUtil.getDefaultGSTAccount(newContext, Account.class))
    }
}
