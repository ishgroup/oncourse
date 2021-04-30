/**
 * Copyright ish group pty ltd. All rights reserved. http://www.ish.com.au
 * No copying or use of this code is allowed without permission in writing from ish.
 */
package ish.oncourse.server.cayenne

import ish.CayenneIshTestCase
import ish.oncourse.server.ICayenneService
import org.apache.cayenne.access.DataContext
import org.junit.jupiter.api.Test

import static org.junit.Assert.assertEquals
import static org.junit.Assert.assertNotNull

/**
 */
class SiteTest extends CayenneIshTestCase {

	@Test
    void testLifecycleCallbacks() {

		DataContext newContext = injector.getInstance(ICayenneService.class).getNewNonReplicatingContext()

        Site site = newContext.newObject(Site.class)
        site.setName("name")
        site.setIsShownOnWeb(false)
        site.setIsAdministrationCentre(true)
        site.setLocalTimezone("Australia/Sydney")
        site.setIsVirtual(false)

        newContext.commitChanges()

        SystemUser su = newContext.newObject(SystemUser.class)
        su.setLogin("login")
        su.setPassword("password")
        su.setFirstName("firstName")
        su.setEmail("admin@email.com")
        su.setLastName("lastName")
        site.addToUsers(su)

        assertNotNull("Check defaultAdministrationCentre", su.getDefaultAdministrationCentre())
        assertEquals("Check users size: ", 1, site.getUsers().size())

        // check prePersist in SystemUser
		newContext.commitChanges()

        SystemUser localSu = newContext.localObject(su)
        assertNotNull("Check defaultAdministrationCentre", localSu.getDefaultAdministrationCentre())

        assertNotNull("Check defaultAdministrationCentre", su.getDefaultAdministrationCentre())

        assertEquals("Check users size: ", 1, site.getUsers().size())

    }
}
