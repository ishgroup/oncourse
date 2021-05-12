/*
 * Copyright ish group pty ltd. All rights reserved. http://www.ish.com.au No copying or use of this code is allowed without permission in writing from ish.
 */

package ish.oncourse.server.print

import groovy.transform.CompileStatic
import ish.CayenneIshTestCase
import ish.DatabaseSetup
import ish.common.types.PaymentSource
import ish.common.types.PaymentStatus
import ish.math.Money
import ish.oncourse.cayenne.PersistentObjectI
import ish.oncourse.entity.services.SetPaymentMethod
import ish.oncourse.server.cayenne.*
import ish.print.AdditionalParameters
import ish.print.PrintTransformationsFactory
import ish.print.transformations.PrintTransformation
import ish.util.PaymentMethodUtil
import org.apache.cayenne.query.SelectById
import org.apache.cayenne.query.SelectQuery
import org.apache.commons.lang3.time.DateUtils
import org.dbunit.dataset.ReplacementDataSet
import org.junit.jupiter.api.Assertions
import org.junit.jupiter.api.Test

import java.time.LocalDate

@CompileStatic
@DatabaseSetup(value = "ish/oncourse/server/print/printTransformationTest.xml")
class PrintTransformationTest extends CayenneIshTestCase {

    static Date before = DateUtils.addDays(new Date(), -5)
    static Date start = DateUtils.addDays(new Date(), -4)
    static Date within = DateUtils.addDays(new Date(), -3)
    static Date end = DateUtils.addDays(new Date(), -2)
    static Date after = DateUtils.addDays(new Date(), -1)

    @Override
    protected void dataSourceReplaceValues(ReplacementDataSet rDataSet) {
        rDataSet.addReplacementObject("[before]", before)
        rDataSet.addReplacementObject("[within]", within)
        rDataSet.addReplacementObject("[after]", after)
    }
    
    @Test
    void testMissingIds() {
        List<Long> ids = []

        Map<String, Object> params = new HashMap<>()
        params.put(AdditionalParameters.LOCALDATERANGE_FROM.toString(), start)
        params.put(AdditionalParameters.LOCALDATERANGE_TO.toString(), end)

        PrintTransformation printTransformation = PrintTransformationsFactory.getPrintTransformationFor("Account", "AccountTransaction", null)
        Assertions.assertEquals(2000, printTransformation.getBatchSize() + printTransformation.getTransformationFilterParamsCount())
        Assertions.assertEquals(printTransformation.getTransformationFilterParamsCount(), 3)

        Assertions.assertThrows(IllegalArgumentException, { ->
            printTransformation.applyTransformation(cayenneContext, ids, params)
        })
    }

    @Test
    void testMissingParam() {
        List<Long> ids = [100L, 200L, 300L, 600L]

        Map<String, Object> params = new HashMap<>()
        PrintTransformation printTransformation = PrintTransformationsFactory.getPrintTransformationFor("Account", "AccountTransaction", null)
        Assertions.assertEquals(2000, printTransformation.getBatchSize() + printTransformation.getTransformationFilterParamsCount())
        Assertions.assertEquals(printTransformation.getTransformationFilterParamsCount(), 3)
        List<? extends PersistentObjectI> result = printTransformation.applyTransformation(cayenneContext, ids, params)
        Assertions.assertEquals(9, result.size())
        Assertions.assertTrue(result.get(0) instanceof AccountTransaction)
    }
    
    @Test
    void testWithTimeLimits() {
        List<Long> ids = [100L, 200L, 300L, 600L]

        Map<String, Object> params = new HashMap<>()
        params.put(AdditionalParameters.LOCALDATERANGE_FROM.toString(), start)
        params.put(AdditionalParameters.LOCALDATERANGE_TO.toString(), end)

        PrintTransformation printTransformation = PrintTransformationsFactory.getPrintTransformationFor("Account", "AccountTransaction", null)
        Assertions.assertEquals(2000, printTransformation.getBatchSize() + printTransformation.getTransformationFilterParamsCount())
        Assertions.assertEquals(printTransformation.getTransformationFilterParamsCount(), 3)
        List<? extends PersistentObjectI> result = printTransformation.applyTransformation(cayenneContext, ids, params)
        Assertions.assertEquals(3, result.size())
        Assertions.assertTrue(result.get(0) instanceof AccountTransaction)

    }

    @Test
    void testBatchSize() {
        //create payments
        Contact contact = SelectById.query(Contact.class, 100).selectOne(cayenneContext)
        Assertions.assertNotNull(contact)
        Account account = SelectById.query(Account.class, 300).selectOne(cayenneContext)
        Assertions.assertNotNull(account)

        for (int i = 0; i <= 2001; i++) {
            PaymentIn paymentIn = cayenneContext.newObject(PaymentIn.class)
            paymentIn.setPaymentDate(LocalDate.now())
            paymentIn.setAmount(Money.ZERO)
            paymentIn.setStatus(PaymentStatus.SUCCESS)
            SetPaymentMethod.valueOf(PaymentMethodUtil.getINTERNALPaymentMethods(cayenneContext, PaymentMethod.class), paymentIn).set()
            paymentIn.setPayer(contact)
            paymentIn.setSource(PaymentSource.SOURCE_ONCOURSE)
            paymentIn.setAccountIn(account)
        }

        cayenneContext.commitChanges()

        List<Long> paymentIds = new ArrayList<>()
        List<PaymentIn> paymentIns = cayenneContext.performQuery(SelectQuery.query(PaymentIn.class))
        for (PaymentIn paymentIn : paymentIns) {
            paymentIds.add(paymentIn.getId())
        }
        Map<String, Object> params = new HashMap<>()
        params.put(AdditionalParameters.DATERANGE_FROM.toString(), start)
        params.put(AdditionalParameters.DATERANGE_TO.toString(), new Date())
        PrintTransformation printTransformation = PrintTransformationsFactory.getPrintTransformationFor("PaymentIn", "PaymentInterface", null)
        //check batch size
        Assertions.assertEquals(2000, printTransformation.getBatchSize() + printTransformation.getTransformationFilterParamsCount())
        Assertions.assertEquals(printTransformation.getTransformationFilterParamsCount(), 3)
        List<? extends PersistentObjectI> result = printTransformation.applyTransformation(cayenneContext, paymentIds, params)
        //check result
        Assertions.assertEquals(2002, result.size())
    }
}
