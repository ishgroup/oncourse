package ish.oncourse.admin.billing.get

import ish.oncourse.model.LicenseFee

/**
 *
 * akoiro - 2/29/16.
 */
abstract class AbstractGetter<T> implements Getter<T> {
    protected BillingContext context
    protected LicenseFee licenseFee


    protected abstract T innerGet()

    protected abstract init()

    @Override
    T get() {
        init()
        if (!hasValue())
            return null

        return innerGet()
    }

    def hasValue() {
        return licenseFee != null
    }
}
