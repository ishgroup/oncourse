/**
 * Copyright ish group pty ltd. All rights reserved. http://www.ish.com.au
 * No copying or use of this code is allowed without permission in writing from ish.
 */
package ish.oncourse.server.cayenne


import groovy.transform.CompileStatic
import ish.CayenneIshTestCase
import ish.oncourse.server.ICayenneService
import org.apache.cayenne.access.DataContext
import org.junit.jupiter.api.Assertions
import org.junit.jupiter.api.Test

@CompileStatic
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

        Assertions.assertNotNull(su.getDefaultAdministrationCentre(), "Check defaultAdministrationCentre")
        Assertions.assertEquals(1, site.getUsers().size(), "Check users size: ")

        // check prePersist in SystemUser
        newContext.commitChanges()

        SystemUser localSu = newContext.localObject(su)
        Assertions.assertNotNull(localSu.getDefaultAdministrationCentre(), "Check defaultAdministrationCentre")

        Assertions.assertNotNull(su.getDefaultAdministrationCentre(), "Check defaultAdministrationCentre")

        Assertions.assertEquals(1, site.getUsers().size(), "Check users size: ")

    }
}
