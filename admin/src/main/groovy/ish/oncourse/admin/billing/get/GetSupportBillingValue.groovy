package ish.oncourse.admin.billing.get

import ish.oncourse.admin.billing.BillingValue
import ish.oncourse.admin.services.billing.StockCodes
import ish.oncourse.model.LicenseFee
import org.apache.cayenne.query.ObjectSelect
import org.codehaus.plexus.util.StringUtils

import java.text.MessageFormat
import java.text.SimpleDateFormat

import static ish.oncourse.admin.billing.LicenseCode.support
import static ish.oncourse.admin.services.billing.Constants.DATE_MONTH_FORMAT

/**
 *
 * akoiro - 2/24/16.
 */
class GetSupportBillingValue implements Getter<BillingValue> {
    def BillingContext context

    @Override
    BillingValue get() {
        LicenseFee licenseFee = ObjectSelect.query(LicenseFee.class).where(LicenseFee.COLLEGE.eq(context.college)
                .andExp(LicenseFee.KEY_CODE.eq(support.dbValue))).selectFirst(context.context)

        String description = MessageFormat.format(
                support.descTemplate,
                StringUtils.capitalise(licenseFee.getPlanName()),
                context.college.getName(),
                new SimpleDateFormat(DATE_MONTH_FORMAT).format(licenseFee.renewalDate));


        StockCodes code = StockCodes.valueOf(licenseFee.getPlanName())
        BillingValue billingValue = new BillingValue(code: code.productionCode,
                description: description,
                quantity: 1,
                unitPrice: licenseFee.fee)

        return billingValue
    }
}
