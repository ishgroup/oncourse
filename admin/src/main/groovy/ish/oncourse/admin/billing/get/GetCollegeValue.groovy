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
    def billingValues = new ArrayList<BillingValue>();
    def webSiteValues = new ArrayList<WebSiteValue>();

    def CollegeValue get() {

        add(new GetSupportBillingValue(context: context).get())
        add(new GetSMSBillingValue(context: context).get())
        add(new GetOfficeCCBillingValue(context: context).get())

        context.college.getCustomFees().each { customFee ->
            add(new GetCustomFeeBillingValue(context: context, customFee: customFee).get())
        }

        context.college.getWebSites().each {
            webSite -> webSiteValues.add(new GetWebSiteValue(context: context, webSite: webSite).get())
        }


        return new CollegeValue(college: context.college,
                billingValues: billingValues, webSiteValues: webSiteValues)
    }

    private add(BillingValue value) {
        if (value != null) {
            billingValues.add(value)
        }
    }

}

