/*
 * Copyright ish group pty ltd. All rights reserved. http://www.ish.com.au No copying or use of this code is allowed without permission in writing from ish.
 */

package ish.util

import groovy.transform.CompileStatic
import ish.TestWithDatabase
import ish.DatabaseSetup
import ish.oncourse.server.cayenne.*
import org.apache.cayenne.query.SelectQuery
import org.junit.jupiter.api.Assertions
import org.junit.jupiter.api.Test

@CompileStatic
@DatabaseSetup(readOnly = true, value = "ish/util/entityUtilTest.xml")

class EntityUtilTest extends TestWithDatabase {

    @Test
    void testEntityClassForName() throws Exception {
        Assertions.assertEquals(Invoice.class, EntityUtil.entityClassForName("Invoice"))
        Assertions.assertEquals(InvoiceLine.class, EntityUtil.entityClassForName("Invoice.InvoiceLine"))

        Assertions.assertEquals(Room.class, EntityUtil.entityClassForName("Room"))
        Assertions.assertEquals(Room.class, EntityUtil.entityClassForName("Site.Room"))
        Assertions.assertEquals(Site.class, EntityUtil.entityClassForName("Site"))
        Assertions.assertEquals(Site.class, EntityUtil.entityClassForName("Room.Site"))
    }

    @Test
    void testGetObjectsByIds() throws Exception {
        List<Long> siteIds = Arrays.asList(1L, 4L)
        List<Long> roomIds = Arrays.asList(1L, 2L, 3L, 4L)

        List<Site> sites = EntityUtil.getObjectsByIds(cayenneContext, Site.class, siteIds)
        Assertions.assertEquals(2, sites.size())

        List<Room> rooms = EntityUtil.getObjectsByIds(cayenneContext, Room.class, roomIds)
        Assertions.assertEquals(4, rooms.size())
    }

    @Test
    void testApplyPathTranform1() {
        List<Site> sites = cayenneContext.select(SelectQuery.query(Site.class))

        Assertions.assertNotNull(sites)
        Assertions.assertTrue(sites.size() > 0)

        List rooms = EntityUtil.applyPathTranform(sites, "Site.rooms")

        Assertions.assertNotNull(rooms)
        Assertions.assertEquals(Room.class, rooms.get(0).getClass())
    }

    @Test
    void testApplyPathTranform2() {
        List<Room> rooms = cayenneContext.select(SelectQuery.query(Room.class))

        Assertions.assertNotNull(rooms)
        Assertions.assertTrue(rooms.size() > 0)

        List sites = EntityUtil.applyPathTranform(rooms, "Room.site")

        Assertions.assertNotNull(sites)
        Assertions.assertEquals(Site.class, sites.get(0).getClass())
    }

    @Test
    void testApplyPathTranform3() {
        List<Country> countries = cayenneContext.select(SelectQuery.query(Country.class))

        Assertions.assertNotNull(countries)
        Assertions.assertTrue(countries.size() > 0)

        List rooms = EntityUtil.applyPathTranform(countries, "Country.sites.rooms")

        Assertions.assertNotNull(rooms)
        Assertions.assertEquals(Room.class, rooms.get(0).getClass())
    }

    @Test
    void testApplyPathTranform4() {
        List<Room> rooms = cayenneContext.select(SelectQuery.query(Room.class))

        Assertions.assertNotNull(rooms)
        Assertions.assertTrue(rooms.size() > 0)

        List coutnries = EntityUtil.applyPathTranform(rooms, "Room.site.country")

        Assertions.assertNotNull(coutnries)
        Assertions.assertEquals(Country.class, coutnries.get(0).getClass())
    }
}
