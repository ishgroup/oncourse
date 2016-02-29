package ish.oncourse.admin.billing

import ish.oncourse.admin.billing.get.BillingContext
import ish.oncourse.admin.billing.get.GetCollegeValue
import ish.oncourse.model.College
import ish.oncourse.model.CustomFee
import ish.oncourse.model.WebSite
import org.apache.cayenne.ObjectContext
import org.junit.Test

import static org.apache.commons.lang3.time.DateUtils.addMonths
import static org.junit.Assert.assertEquals
import static org.mockito.Mockito.mock
import static org.mockito.Mockito.when

/**
 *
 * akoiro - 2/24/16.
 */
class GetCollegeValueTest {

    @Test
    def void test() {
        ObjectContext context = mock(ObjectContext)
        College college = mock(College)
        when(college.getWebSites()).thenReturn([mock(WebSite), mock(WebSite), mock(WebSite)])
        when(college.getCustomFees()).thenReturn([mock(CustomFee), mock(CustomFee), mock(CustomFee)])

        Date from = new Date()
        Date to = addMonths(from, 1)
        def value = new GetCollegeValue(context: new BillingContext(context: context, college: college, from: from, to: to))
        assertEquals(value.context.context, context)
        assertEquals(value.context.college, college)
        assertEquals(value.context.from, from)
        assertEquals(value.context.to, to)

        CollegeValue collegeValue = value.get()

        assertEquals(collegeValue.college, college)
        assertEquals(collegeValue.billingValues.size(), 6)
        assertEquals(collegeValue.billingValues[0].getClass(), BillingValue.class)
        assertEquals(collegeValue.billingValues[1].getClass(), BillingValue.class)
        assertEquals(collegeValue.billingValues[2].getClass(), BillingValue.class)
        assertEquals(collegeValue.billingValues[3].getClass(), BillingValue.class)
        assertEquals(collegeValue.billingValues[4].getClass(), BillingValue.class)
        assertEquals(collegeValue.billingValues[5].getClass(), BillingValue.class)


        assertEquals(collegeValue.webSiteValues.size(), 3)
        assertEquals(collegeValue.webSiteValues[0].getClass(), WebSiteValue.class)
        assertEquals(collegeValue.webSiteValues[1].getClass(), WebSiteValue.class)
        assertEquals(collegeValue.webSiteValues[2].getClass(), WebSiteValue.class)

    }
}
