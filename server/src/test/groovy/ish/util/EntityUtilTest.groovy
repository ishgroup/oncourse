/*
 * Copyright ish group pty ltd. All rights reserved. http://www.ish.com.au No copying or use of this code is allowed without permission in writing from ish.
 */

package ish.util

import groovy.transform.CompileStatic
import ish.CayenneIshTestCase
import ish.oncourse.server.ICayenneService
import ish.oncourse.server.cayenne.Country
import ish.oncourse.server.cayenne.Invoice
import ish.oncourse.server.cayenne.InvoiceLine
import ish.oncourse.server.cayenne.Room
import ish.oncourse.server.cayenne.Site
import static junit.framework.TestCase.assertEquals
import static junit.framework.TestCase.assertNotNull
import static junit.framework.TestCase.assertTrue
import org.apache.cayenne.access.DataContext
import org.apache.cayenne.query.SelectQuery
import org.dbunit.dataset.ReplacementDataSet
import org.dbunit.dataset.xml.FlatXmlDataSet
import org.dbunit.dataset.xml.FlatXmlDataSetBuilder
import org.junit.Before
import org.junit.Test

@CompileStatic
class EntityUtilTest extends CayenneIshTestCase {

	@Before
    void setupTest() throws Exception {
		wipeTables()

        InputStream st = EntityUtilTest.class.getClassLoader().getResourceAsStream("ish/util/entityUtilTest.xml")
        FlatXmlDataSet dataSet = new FlatXmlDataSetBuilder().build(st)
        ReplacementDataSet replacementDataSet = new ReplacementDataSet(dataSet)
        replacementDataSet.addReplacementObject("[NULL]", null)
        executeDatabaseOperation(replacementDataSet)
    }


	@Test
    void testEntityClassForName() throws Exception {
		assertEquals(Invoice.class, EntityUtil.entityClassForName("Invoice"))
        assertEquals(InvoiceLine.class, EntityUtil.entityClassForName("Invoice.InvoiceLine"))

        assertEquals(Room.class, EntityUtil.entityClassForName("Room"))
        assertEquals(Room.class, EntityUtil.entityClassForName("Site.Room"))
        assertEquals(Site.class, EntityUtil.entityClassForName("Site"))
        assertEquals(Site.class, EntityUtil.entityClassForName("Room.Site"))
    }

	@Test
    void testGetObjectsByIds() throws Exception {

		List<Long> siteIds = Arrays.asList(1L, 4L)
        List<Long> roomIds = Arrays.asList(1L, 2L, 3L, 4L)

        ICayenneService cServ = (ICayenneService) injector.getInstance(ICayenneService.class)

        List<Site> sites = EntityUtil.getObjectsByIds(cServ.getNewContext(), Site.class, siteIds)
        assertEquals(2, sites.size())

        List<Room> rooms = EntityUtil.getObjectsByIds(cServ.getNewContext(), Room.class, roomIds)
        assertEquals(4, rooms.size())
    }

	@Test
    void testApplyPathTranform1() {
		ICayenneService cServ = (ICayenneService) injector.getInstance(ICayenneService.class)
        DataContext context = cServ.getNewContext()
        List<Site> sites = context.select(SelectQuery.query(Site.class))

        assertNotNull(sites)
        assertTrue(sites.size() > 0)

        List rooms = EntityUtil.applyPathTranform(sites, "Site.rooms")

        assertNotNull(rooms)
        assertEquals(Room.class, rooms.get(0).getClass())

    }

	@Test
    void testApplyPathTranform2() {
		ICayenneService cServ = (ICayenneService) injector.getInstance(ICayenneService.class)
        DataContext context = cServ.getNewContext()
        List<Room> rooms = context.select(SelectQuery.query(Room.class))

        assertNotNull(rooms)
        assertTrue(rooms.size() > 0)

        List sites = EntityUtil.applyPathTranform(rooms, "Room.site")

        assertNotNull(sites)
        assertEquals(Site.class, sites.get(0).getClass())
    }

	@Test
    void testApplyPathTranform3() {
		ICayenneService cServ = (ICayenneService) injector.getInstance(ICayenneService.class)
        DataContext context = cServ.getNewContext()
        List<Country> countries = context.select(SelectQuery.query(Country.class))

        assertNotNull(countries)
        assertTrue(countries.size() > 0)

        List rooms = EntityUtil.applyPathTranform(countries, "Country.sites.rooms")

        assertNotNull(rooms)
        assertEquals(Room.class, rooms.get(0).getClass())
    }

	@Test
    void testApplyPathTranform4() {
		ICayenneService cServ = (ICayenneService) injector.getInstance(ICayenneService.class)
        DataContext context = cServ.getNewContext()
        List<Room> rooms = context.select(SelectQuery.query(Room.class))

        assertNotNull(rooms)
        assertTrue(rooms.size() > 0)

        List coutnries = EntityUtil.applyPathTranform(rooms, "Room.site.country")

        assertNotNull(coutnries)
        assertEquals(Country.class, coutnries.get(0).getClass())
    }
}
