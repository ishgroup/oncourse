package ish.oncourse.admin.billing.get

import ish.oncourse.admin.billing.BillingValue
import ish.oncourse.model.CustomFee

/**
 *
 * akoiro - 2/24/16.
 */
class GetCustomFeeBillingValue extends AbstractGetter<BillingValue> {
    def CustomFee customFee

    @Override
    protected BillingValue innerGet() {
        return new BillingValue(code: customFee.code,
                description: customFee.name,
                quantity: 1,
                unitPrice: customFee.fee,
                paidUntil: customFee.paidUntil)
    }

    @Override
    protected init() {
    }

    @Override
    def hasValue() {
        return customFee.name != null;
    }
}
