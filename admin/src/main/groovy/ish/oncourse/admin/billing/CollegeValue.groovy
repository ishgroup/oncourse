package ish.oncourse.admin.billing

import ish.oncourse.model.College

/**
 *
 * akoiro - 2/23/16.
 */
class CollegeValue {
    def College college;
    def List<BillingValue> billingValues = new ArrayList<>();
    def List<WebSiteValue> webSiteValues = new ArrayList<>();

    static valueOf(College college, List<BillingValue> billingValues, List<WebSiteValue> webSiteValues) {
        return {
            college:college
            billingValues:Collections.unmodifiableCollection(billingValues)
            webSiteValues:Collections.unmodifiableCollection(webSiteValues)
        } as CollegeValue
    }
}
