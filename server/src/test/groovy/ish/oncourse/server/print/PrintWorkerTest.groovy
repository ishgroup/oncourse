/*
 * Copyright ish group pty ltd. All rights reserved. http://www.ish.com.au No copying or use of this code is allowed without permission in writing from ish.
 */

package ish.oncourse.server.print

import groovy.transform.CompileStatic
import ish.CayenneIshTestCase
import ish.DatabaseSetup
import ish.oncourse.cayenne.PersistentObjectI
import ish.oncourse.server.cayenne.Room
import ish.oncourse.server.cayenne.Site
import ish.oncourse.server.document.DocumentService
import ish.print.PrintRequest
import ish.print.PrintTransformationsFactory
import ish.print.transformations.PrintTransformation
import ish.print.transformations.PrintTransformationField
import org.junit.jupiter.api.Assertions
import org.junit.jupiter.api.Test

@CompileStatic
@DatabaseSetup(value = "ish/util/entityUtilTest.xml")
class PrintWorkerTest extends CayenneIshTestCase {

    @Test
    void testGetRecords() throws Exception {
        def documentService = injector.getInstance(DocumentService.class)

        List<Long> siteIds = Arrays.asList(1L, 2L, 3L, 4L)

        Map<String, List<Long>> ids = new HashMap<>()
        ids.put("Site", siteIds)


        PrintRequest request = new PrintRequest()
        request.setEntity("Site")
        request.setReportCode("test")
        request.setIds(ids)

        PrintWorker pw = new PrintWorker(request, cayenneService, documentService)

        List<PersistentObjectI> records = pw.transformRecords(ids.get("Site"), null, null)

        for (PersistentObjectI po : records) {
            Assertions.assertTrue(po instanceof Site)
        }
    }

    
    @Test
    void testGetRecordsWithTraverse() throws Exception {
        def documentService = injector.getInstance(DocumentService.class)

        List<Long> siteIds = Arrays.asList(1L, 2L, 3L, 4L)

        Map<String, List<Long>> ids = new HashMap<>()
        ids.put("Site", siteIds)


        PrintRequest request = new PrintRequest()
        request.setEntity("Room")
        request.setReportCode("test")
        request.setIds(ids)

        PrintTransformation trans = PrintTransformationsFactory.getPrintTransformationFor("Site", "Room", null)

        PrintWorker pw = new PrintWorker(request, cayenneService, documentService)
        Assertions.assertEquals(2000, trans.getBatchSize() + trans.getTransformationFilterParamsCount())
        Assertions.assertEquals(trans.getTransformationFilterParamsCount(), 1)
        List<PersistentObjectI> records = pw.transformRecords(ids.get("Site"), trans, null)


        for (PersistentObjectI po : records) {
            Assertions.assertTrue(po instanceof Room)
        }
    }

    
    @Test
    void testGetRecordsWithFilter() throws Exception {
        def documentService = injector.getInstance(DocumentService.class)

        List<Long> siteIds = Arrays.asList(1L, 2L, 3L, 4L)

        Map<String, List<Long>> ids = new HashMap<>()
        ids.put("Site", siteIds)

        PrintRequest request = new PrintRequest()
        request.setEntity("Site")
        request.setReportCode("test")
        request.setIds(ids)

        PrintTransformation trans = new PrintTransformation()
        trans.setInputEntityName("Site")
        trans.setOutputEntityName("Site")
        PrintTransformationField<Integer> isOn = new PrintTransformationField<>("isOn", "webOn", Integer.class, -1)
        trans.addFieldDefinition(isOn)
        trans.setTransformationFilter('id in $sourceIds and isShownOnWeb = \$' + isOn.getFieldCode())

        request.setValueForKey(isOn.getFieldCode(), 1)

        PrintWorker pw = new PrintWorker(request, cayenneService, documentService)
        Assertions.assertEquals(2000, trans.getBatchSize() + trans.getTransformationFilterParamsCount())
        Assertions.assertEquals(trans.getTransformationFilterParamsCount(), 2)
        List<PersistentObjectI> records = pw.transformRecords(ids.get("Site"), trans, null)


        for (PersistentObjectI po : records) {
            Assertions.assertTrue(po instanceof Site)
            Assertions.assertTrue(po.getValueForKey(Site.IS_SHOWN_ON_WEB.getName()) instanceof Boolean)
            Assertions.assertTrue(((Boolean) po.getValueForKey(Site.IS_SHOWN_ON_WEB.getName())))
        }
    }

    
    @Test
    void testGetRecordsWithTraverseAndFilter() throws Exception {
        def documentService = injector.getInstance(DocumentService.class)

        List<Long> siteIds = Arrays.asList(1L, 2L, 3L, 4L)

        Map<String, List<Long>> ids = new HashMap<>()
        ids.put("Site", siteIds)


        PrintRequest request = new PrintRequest()
        request.setEntity("Room")
        request.setReportCode("test")
        request.setIds(ids)

        PrintTransformation trans = PrintTransformationsFactory.getPrintTransformationFor("Site", "Room", null)
        PrintTransformationField<Integer> maxSeats = new PrintTransformationField<>("maxSeats", "max seats", Integer.class, 50)
        trans.addFieldDefinition(maxSeats)
        trans.setTransformationFilter('site.id in $sourceIds and seatedCapacity < \$' + maxSeats.getFieldCode())

        request.setValueForKey(maxSeats.getFieldCode(), 30)

        PrintWorker pw = new PrintWorker(request, cayenneService, documentService)
        Assertions.assertEquals(2000, trans.getBatchSize() + trans.getTransformationFilterParamsCount())
        Assertions.assertEquals(trans.getTransformationFilterParamsCount(), 2)
        List<PersistentObjectI> records = pw.transformRecords(ids.get("Site"), trans, null)

        for (PersistentObjectI po : records) {
            Assertions.assertTrue(po instanceof Room)
            Assertions.assertTrue(po.getValueForKey(Room.SEATED_CAPACITY.getName()) instanceof Number)
            Assertions.assertTrue(((Number) po.getValueForKey(Room.SEATED_CAPACITY.getName())).intValue() < 30)
        }
    }
}
