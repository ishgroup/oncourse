package ish.oncourse.admin.billing.get

import ish.oncourse.admin.billing.BillingValue
import ish.oncourse.model.CustomFee

/**
 *
 * akoiro - 2/24/16.
 */
class GetCustomFeeBillingValue implements Getter<BillingValue> {
    def BillingContext context
    def CustomFee customFee

    @Override
    BillingValue get() {
        return new BillingValue(code: customFee.code,
        description: customFee.name,
        quantity: 1,
        unitPrice: customFee.fee)
    }
}
