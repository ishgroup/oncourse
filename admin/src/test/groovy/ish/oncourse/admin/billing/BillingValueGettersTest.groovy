package ish.oncourse.admin.billing

import ish.oncourse.admin.billing.get.*
import ish.oncourse.admin.services.AbstractTest
import ish.oncourse.model.College
import ish.oncourse.model.CustomFee
import ish.oncourse.model.WebSite
import org.apache.cayenne.query.SelectById
import org.dbunit.dataset.ReplacementDataSet
import org.junit.Test

import static ish.oncourse.admin.billing.LicenseCode.ccOffice
import static ish.oncourse.admin.billing.LicenseCode.sms
import static ish.oncourse.admin.services.billing.StockCodes.*
import static org.apache.commons.lang3.time.DateUtils.addDays
import static org.apache.commons.lang3.time.DateUtils.addMonths
import static org.junit.Assert.assertEquals

/**
 *
 * akoiro - 2/24/16.
 */
class BillingValueGettersTest extends AbstractTest {
    private Date from = addMonths(new Date(), -1);
    private Date to = addMonths(from, 1);

    @Test
    def void test() {
        testGetSMSBillingValue()
        testGetSupportBillingValue()
        testGetOfficeCCBillingValue()
        testGetCustomFeeBillingValue()
        testGetHostingBillingValue()
        testGetWebCCBillingValue()
        testGetEcommerceBillingValue()
    }

    def void testGetSMSBillingValue() {
        def BillingContext billingContext = getBillingContext(1001)

        BillingValue value = new GetSMSBillingValue(context: billingContext).get()

        assertEquals(new BillingValue(code: SMS.productionCode,
                description: sms.simpleDesc,
                quantity: 1,
                unitPrice: 1.000), value);
    }

    def void testGetSupportBillingValue() {
        BillingContext billingContext = getBillingContext(1001)

        BillingValue value = new GetSupportBillingValue(context: billingContext).get()

        assertEquals(new BillingValue(code: enterprise.productionCode,
                description: "Enterprise plan for College1: 1 month",
                quantity: 1,
                unitPrice: 1.000), value);
    }

    def void testGetOfficeCCBillingValue() {
        BillingContext billingContext = getBillingContext(1001)

        BillingValue value = new GetOfficeCCBillingValue(context: billingContext).get()
        assertEquals(new BillingValue(code: OFFICE_CC.productionCode,
                description: ccOffice.simpleDesc,
                quantity: 0,
                unitPrice: 1.000), value)

        billingContext = getBillingContext(1002)

        value = new GetOfficeCCBillingValue(context: billingContext).get()
        assertEquals(new BillingValue(code: OFFICE_CC.productionCode,
                description: "Office credit card transaction fee (0 less 1 free transactions)",
                quantity: 0,
                unitPrice: 1.000), value)
    }

    def void testGetCustomFeeBillingValue() {
        BillingContext billingContext = getBillingContext(1001)
        CustomFee customFee = SelectById.query(CustomFee, 1001).selectOne(billingContext.getContext())

        BillingValue value = new GetCustomFeeBillingValue(context: billingContext, customFee: customFee).get()
        assertEquals(new BillingValue(code: "CustomFee1001",
                description: "CustomFee1001",
                quantity: 1,
                unitPrice: 1), value)

    }

    def void testGetHostingBillingValue() {
        BillingContext billingContext = getBillingContext(1001)
        WebSite webSite = SelectById.query(WebSite, 1001).selectOne(billingContext.context)

        BillingValue value = new GetHostingBillingValue(context: billingContext, webSite: webSite).get()
        assertEquals(new BillingValue(code: platinum.productionCode,
                description: "Platinum web plan for WebSite1: 1 month",
                quantity: 1,
                unitPrice: 1.000), value);
    }

    def void testGetWebCCBillingValue() {
        BillingContext billingContext = getBillingContext(1001)
        WebSite webSite = SelectById.query(WebSite, 1001).selectOne(billingContext.context)

        BillingValue value = new GetWebCCBillingValue(context: billingContext, webSite: webSite).get()
        assertEquals(new BillingValue(code: WEB_CC.productionCode,
                description: "WebSite1 online credit card transaction fee",
                quantity: 1,
                unitPrice: 0.650), value);


        billingContext = getBillingContext(1001)
        webSite = SelectById.query(WebSite, 1002).selectOne(billingContext.context)

        value = new GetWebCCBillingValue(context: billingContext, webSite: webSite).get()
        assertEquals(new BillingValue(code: WEB_CC.productionCode,
                description: "WebSite2 online credit card transaction fee",
                quantity: 2,
                unitPrice: 1.100), value);

    }

    def void testGetEcommerceBillingValue() {
        BillingContext billingContext = getBillingContext(1001)
        WebSite webSite = SelectById.query(WebSite, 1001).selectOne(billingContext.context)
        BillingValue value = new GetEcommerceBillingValue(context: billingContext, webSite: webSite).get()

        assertEquals(new BillingValue(code: ECOMMERCE.productionCode,
                description: "WebSite1 eCommerce fee at 6% of \$100.00",
                quantity: 1,
                unitPrice: 6.00000), value);

    }


    private BillingContext getBillingContext(Long collegeId) {
        def context = cayenneService.newContext()
        def BillingContext billingContext = new BillingContext(context: context,
                college: SelectById.query(College, collegeId).selectOne(context),
                from: from,
                to: to)
        billingContext
    }

    @Override
    protected void configDataSet(ReplacementDataSet rDataSet) {
        rDataSet.addReplacementObject("[IN_PERIOD]", addDays(from, 5));

    }

    @Override
    protected InputStream getDataSource() {
        BillingValueGettersTest.getResourceAsStream("BillingValueGettersTest.xml")
    }
}
