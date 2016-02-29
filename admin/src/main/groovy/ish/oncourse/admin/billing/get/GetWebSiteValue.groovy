package ish.oncourse.admin.billing.get

import ish.oncourse.admin.billing.BillingValue
import ish.oncourse.admin.billing.WebSiteValue
import ish.oncourse.model.WebSite

/**
 *
 * akoiro - 2/24/16.
 */
class GetWebSiteValue implements Getter<WebSiteValue>
{
    def BillingContext context
    def WebSite webSite

    @Override
    WebSiteValue get() {

        def values = new ArrayList<BillingValue>()

        values.add(new GetHostingBillingValue(context: context).get())
        values.add(new GetWebCCBillingValue(context: context).get())
        values.add(new GetEcommerceBillingValue(context: context).get())

        return new WebSiteValue()
    }
}
