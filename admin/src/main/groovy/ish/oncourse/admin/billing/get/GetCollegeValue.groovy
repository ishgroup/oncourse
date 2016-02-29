package ish.oncourse.admin.billing.get

import ish.oncourse.admin.billing.BillingValue
import ish.oncourse.admin.billing.CollegeValue
import ish.oncourse.admin.billing.WebSiteValue

/**
 *
 * akoiro - 2/23/16.
 */
class GetCollegeValue implements Getter<CollegeValue> {
    def BillingContext context

    def CollegeValue get() {
        def billingValues = new ArrayList<BillingValue>();
        def webSiteValues = new ArrayList<WebSiteValue>();

        billingValues.add(new GetSupportBillingValue(context: context).get())
        billingValues.add(new GetSMSBillingValue(context: context).get())
        billingValues.add(new GetOfficeCCBillingValue(context: context).get())

        context.college.getCustomFees().each { customFee ->
            billingValues.add(new GetCustomFeeBillingValue(context: context, customFee: customFee).get())
        }

        context.college.getWebSites().each {
            webSite -> webSiteValues.add(new GetWebSiteValue(context: context, webSite: webSite).get())
        }


        return new CollegeValue(college: context.college,
                billingValues: billingValues, webSiteValues: webSiteValues)
    }
}

