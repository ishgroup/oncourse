/*
 * Copyright ish group pty ltd. All rights reserved. http://www.ish.com.au No copying or use of this code is allowed without permission in writing from ish.
 */

package ish.oncourse.server.db

import groovy.transform.CompileStatic
import ish.CayenneIshTestCase
import ish.oncourse.server.cayenne.Account
import ish.util.AccountUtil
import org.junit.jupiter.api.Assertions
import org.junit.jupiter.api.Test

@CompileStatic
class SanityCheckServiceTest extends CayenneIshTestCase {
    
    @Test
    void testAccountDefaults() {
        Assertions.assertNotNull(AccountUtil.getDefaultGSTAccount(cayenneContext, Account.class))
    }
}
