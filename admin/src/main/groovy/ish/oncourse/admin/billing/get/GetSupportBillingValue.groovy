package ish.oncourse.admin.billing.get

import ish.oncourse.admin.billing.BillingValue
import ish.oncourse.admin.services.billing.IsPlanBillingMonth
import ish.oncourse.admin.services.billing.StockCodes
import ish.oncourse.model.LicenseFee
import org.apache.cayenne.query.ObjectSelect
import org.codehaus.plexus.util.StringUtils

import java.text.MessageFormat

import static ish.oncourse.admin.billing.LicenseCode.support

/**
 *
 * akoiro - 2/24/16.
 */
class GetSupportBillingValue extends AbstractGetter<BillingValue> {

    @Override
    protected BillingValue innerGet() {
        String description = MessageFormat.format(
                support.descTemplate,
                StringUtils.capitalise(licenseFee.planName),
                context.college.name);

        StockCodes code = StockCodes.valueOf(licenseFee.planName)
        BillingValue billingValue = new BillingValue(code: code.productionCode,
                description: description,
                quantity: 1,
                unitPrice: licenseFee.fee)

        return billingValue
    }

    protected init() {
        licenseFee = ObjectSelect.query(LicenseFee.class).where(LicenseFee.COLLEGE.eq(context.college)
                .andExp(LicenseFee.KEY_CODE.eq(support.dbValue))).selectFirst(context.context)
    }

    def hasValue() {
        return licenseFee != null && IsPlanBillingMonth.valueOf(licenseFee.planName, licenseFee.paidUntil, context.from).is();
    }
}
