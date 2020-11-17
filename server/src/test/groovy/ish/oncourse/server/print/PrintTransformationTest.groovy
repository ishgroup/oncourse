/*
 * Copyright ish group pty ltd. All rights reserved. http://www.ish.com.au No copying or use of this code is allowed without permission in writing from ish.
 */

package ish.oncourse.server.print

import groovy.transform.CompileStatic
import ish.CayenneIshTestCase
import ish.common.types.PaymentSource
import ish.common.types.PaymentStatus
import ish.math.Money
import ish.oncourse.cayenne.PersistentObjectI
import ish.oncourse.entity.services.SetPaymentMethod
import ish.oncourse.server.ICayenneService
import ish.oncourse.server.cayenne.Account
import ish.oncourse.server.cayenne.AccountTransaction
import ish.oncourse.server.cayenne.Contact
import ish.oncourse.server.cayenne.PaymentIn
import ish.oncourse.server.cayenne.PaymentMethod
import ish.print.AdditionalParameters
import ish.print.PrintTransformationsFactory
import ish.print.transformations.PrintTransformation
import ish.util.PaymentMethodUtil
import static junit.framework.TestCase.assertEquals
import static junit.framework.TestCase.assertTrue
import static junit.framework.TestCase.fail
import org.apache.cayenne.ObjectContext
import org.apache.cayenne.query.SelectById
import org.apache.cayenne.query.SelectQuery
import org.apache.commons.lang3.time.DateUtils
import org.dbunit.dataset.ReplacementDataSet
import org.dbunit.dataset.xml.FlatXmlDataSet
import org.dbunit.dataset.xml.FlatXmlDataSetBuilder
import static org.junit.Assert.assertNotNull
import org.junit.Before
import org.junit.Test

import java.time.LocalDate

@CompileStatic
class PrintTransformationTest extends CayenneIshTestCase {

	static Date before = DateUtils.addDays(new Date(), -5)
    static Date start = DateUtils.addDays(new Date(), -4)
    static Date within = DateUtils.addDays(new Date(), -3)
    static Date end = DateUtils.addDays(new Date(), -2)
    static Date after = DateUtils.addDays(new Date(), -1)

    @Before
    void setupTest() throws Exception {
		wipeTables()
        InputStream st = PrintTransformationTest.class.getClassLoader().getResourceAsStream("ish/oncourse/server/print/printTransformationTest.xml")
        FlatXmlDataSet dataSet = new FlatXmlDataSetBuilder().build(st)

        ReplacementDataSet rDataSet = new ReplacementDataSet(dataSet)

        rDataSet.addReplacementObject("[before]", before)
        rDataSet.addReplacementObject("[within]", within)
        rDataSet.addReplacementObject("[after]", after)

        executeDatabaseOperation(rDataSet)
        super.setup()
    }

	@Test(expected = IllegalArgumentException.class)
    void testMissingIds() {
		ICayenneService service = (ICayenneService) injector.getInstance(ICayenneService.class)
        ObjectContext oc = service.getNewNonReplicatingContext()

        List<Long> ids = []

        Map<String, Object> params = new HashMap<>()
        params.put(AdditionalParameters.LOCALDATERANGE_FROM.toString(), start)
        params.put(AdditionalParameters.LOCALDATERANGE_TO.toString(), end)

        PrintTransformation printTransformation = PrintTransformationsFactory.getPrintTransformationFor("Account", "AccountTransaction", null)
        assertEquals(2000, printTransformation.getBatchSize() + printTransformation.getTransformationFilterParamsCount())
        assertEquals(printTransformation.getTransformationFilterParamsCount(), 3)
        printTransformation.applyTransformation(oc, ids, params)
        fail()
    }

	@Test
    void testMissingParam() {
		ICayenneService service = (ICayenneService) injector.getInstance(ICayenneService.class)
        ObjectContext oc = service.getNewNonReplicatingContext()

        List<Long> ids = [100L, 200L, 300L, 600L]

        Map<String, Object> params = new HashMap<>()
        PrintTransformation printTransformation = PrintTransformationsFactory.getPrintTransformationFor("Account", "AccountTransaction", null)
        assertEquals(2000, printTransformation.getBatchSize() + printTransformation.getTransformationFilterParamsCount())
        assertEquals(printTransformation.getTransformationFilterParamsCount(), 3)
        List<? extends PersistentObjectI> result = printTransformation.applyTransformation(oc, ids, params)
        assertEquals(9, result.size())
        assertTrue(result.get(0) instanceof AccountTransaction)
    }

	@Test
    void testWithTimeLimits() {
		ICayenneService service = (ICayenneService) injector.getInstance(ICayenneService.class)
        ObjectContext oc = service.getNewNonReplicatingContext()

        List<Long> ids = [100L, 200L, 300L, 600L]

        Map<String, Object> params = new HashMap<>()
        params.put(AdditionalParameters.LOCALDATERANGE_FROM.toString(), start)
        params.put(AdditionalParameters.LOCALDATERANGE_TO.toString(), end)

        PrintTransformation printTransformation = PrintTransformationsFactory.getPrintTransformationFor("Account", "AccountTransaction", null)
        assertEquals(2000, printTransformation.getBatchSize() + printTransformation.getTransformationFilterParamsCount())
        assertEquals(printTransformation.getTransformationFilterParamsCount(), 3)
        List<? extends PersistentObjectI> result = printTransformation.applyTransformation(oc, ids, params)
        assertEquals(3, result.size())
        assertTrue(result.get(0) instanceof AccountTransaction)

    }

	@Test
    void testBatchSize() {
		ICayenneService service = (ICayenneService) injector.getInstance(ICayenneService.class)
        ObjectContext oc = service.getNewNonReplicatingContext()

        //create payments
		Contact contact = SelectById.query(Contact.class, 100).selectOne(oc)
        assertNotNull(contact)
        Account account = SelectById.query(Account.class, 300).selectOne(oc)
        assertNotNull(account)

        for (int i = 0 ; i<= 2001 ;i++) {
			PaymentIn paymentIn = oc.newObject(PaymentIn.class)
            paymentIn.setPaymentDate(LocalDate.now())
            paymentIn.setAmount(Money.ZERO)
            paymentIn.setStatus(PaymentStatus.SUCCESS)
            SetPaymentMethod.valueOf(PaymentMethodUtil.getINTERNALPaymentMethods(oc, PaymentMethod.class), paymentIn).set()
            paymentIn.setPayer(contact)
            paymentIn.setSource(PaymentSource.SOURCE_ONCOURSE)
            paymentIn.setAccountIn(account)
        }

		oc.commitChanges()

        List<Long> paymentIds = new ArrayList<>()
        List<PaymentIn> paymentIns = oc.performQuery(SelectQuery.query(PaymentIn.class))
        for (PaymentIn paymentIn : paymentIns) {
			paymentIds.add(paymentIn.getId())
        }
		Map<String, Object> params = new HashMap<>()
        params.put(AdditionalParameters.DATERANGE_FROM.toString(), start)
        params.put(AdditionalParameters.DATERANGE_TO.toString(), new Date())
        PrintTransformation printTransformation = PrintTransformationsFactory.getPrintTransformationFor("PaymentIn", "PaymentInterface", null)
        //check batch size
        assertEquals(2000, printTransformation.getBatchSize() + printTransformation.getTransformationFilterParamsCount())
        assertEquals(printTransformation.getTransformationFilterParamsCount(), 3)
        List<? extends PersistentObjectI> result = printTransformation.applyTransformation(oc, paymentIds, params)
        //check result
		assertEquals(2002, result.size())
    }
}
