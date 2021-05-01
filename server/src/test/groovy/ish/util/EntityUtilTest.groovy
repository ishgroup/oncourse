/*
 * Copyright ish group pty ltd. All rights reserved. http://www.ish.com.au No copying or use of this code is allowed without permission in writing from ish.
 */

package ish.util

import groovy.transform.CompileStatic
import ish.CayenneIshTestCase
import ish.oncourse.server.ICayenneService
import ish.oncourse.server.cayenne.*
import org.apache.cayenne.access.DataContext
import org.apache.cayenne.query.SelectQuery
import org.dbunit.dataset.ReplacementDataSet
import org.dbunit.dataset.xml.FlatXmlDataSet
import org.dbunit.dataset.xml.FlatXmlDataSetBuilder
import org.junit.jupiter.api.Assertions
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.Test


@CompileStatic
class EntityUtilTest extends CayenneIshTestCase {

    @BeforeEach
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

        ICayenneService cServ = (ICayenneService) injector.getInstance(ICayenneService.class)

        List<Site> sites = EntityUtil.getObjectsByIds(cServ.getNewContext(), Site.class, siteIds)
        Assertions.assertEquals(2, sites.size())

        List<Room> rooms = EntityUtil.getObjectsByIds(cServ.getNewContext(), Room.class, roomIds)
        Assertions.assertEquals(4, rooms.size())
    }

    @Test
    void testApplyPathTranform1() {
        ICayenneService cServ = (ICayenneService) injector.getInstance(ICayenneService.class)
        DataContext context = cServ.getNewContext()
        List<Site> sites = context.select(SelectQuery.query(Site.class))

        Assertions.assertNotNull(sites)
        Assertions.assertTrue(sites.size() > 0)

        List rooms = EntityUtil.applyPathTranform(sites, "Site.rooms")

        Assertions.assertNotNull(rooms)
        Assertions.assertEquals(Room.class, rooms.get(0).getClass())

    }

    @Test
    void testApplyPathTranform2() {
        ICayenneService cServ = (ICayenneService) injector.getInstance(ICayenneService.class)
        DataContext context = cServ.getNewContext()
        List<Room> rooms = context.select(SelectQuery.query(Room.class))

        Assertions.assertNotNull(rooms)
        Assertions.assertTrue(rooms.size() > 0)

        List sites = EntityUtil.applyPathTranform(rooms, "Room.site")

        Assertions.assertNotNull(sites)
        Assertions.assertEquals(Site.class, sites.get(0).getClass())
    }

    @Test
    void testApplyPathTranform3() {
        ICayenneService cServ = (ICayenneService) injector.getInstance(ICayenneService.class)
        DataContext context = cServ.getNewContext()
        List<Country> countries = context.select(SelectQuery.query(Country.class))

        Assertions.assertNotNull(countries)
        Assertions.assertTrue(countries.size() > 0)

        List rooms = EntityUtil.applyPathTranform(countries, "Country.sites.rooms")

        Assertions.assertNotNull(rooms)
        Assertions.assertEquals(Room.class, rooms.get(0).getClass())
    }

    @Test
    void testApplyPathTranform4() {
        ICayenneService cServ = (ICayenneService) injector.getInstance(ICayenneService.class)
        DataContext context = cServ.getNewContext()
        List<Room> rooms = context.select(SelectQuery.query(Room.class))

        Assertions.assertNotNull(rooms)
        Assertions.assertTrue(rooms.size() > 0)

        List coutnries = EntityUtil.applyPathTranform(rooms, "Room.site.country")

        Assertions.assertNotNull(coutnries)
        Assertions.assertEquals(Country.class, coutnries.get(0).getClass())
    }
}
