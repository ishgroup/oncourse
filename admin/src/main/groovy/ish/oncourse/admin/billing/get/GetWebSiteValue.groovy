package ish.oncourse.admin.billing.get

import ish.oncourse.admin.billing.BillingValue
import ish.oncourse.admin.billing.WebSiteValue
import ish.oncourse.model.WebSite

/**
 *
 * akoiro - 2/24/16.
 */
class GetWebSiteValue implements Getter<WebSiteValue> {
    def BillingContext context
    def WebSite webSite

    def values = new ArrayList<BillingValue>()

    @Override
    WebSiteValue get() {

        add(new GetHostingBillingValue(context: context).get())
        add(new GetWebCCBillingValue(context: context).get())
        add(new GetEcommerceBillingValue(context: context).get())

        return new WebSiteValue(webSite: webSite, values: values)
    }

    private add(BillingValue value) {
        if (value != null) {
            values.add(value)
        }
    }
}
