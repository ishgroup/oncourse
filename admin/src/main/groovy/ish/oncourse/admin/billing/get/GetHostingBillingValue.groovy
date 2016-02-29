package ish.oncourse.admin.billing.get

import ish.oncourse.admin.billing.BillingValue
import ish.oncourse.admin.services.billing.StockCodes
import ish.oncourse.model.LicenseFee
import ish.oncourse.model.WebSite
import org.apache.cayenne.query.ObjectSelect
import org.apache.commons.lang3.StringUtils

import java.text.MessageFormat
import java.text.SimpleDateFormat

import static ish.oncourse.admin.billing.LicenseCode.hosting
import static ish.oncourse.admin.services.billing.Constants.DATE_MONTH_FORMAT
import static ish.oncourse.model.auto._LicenseFee.*

/**
 *
 * akoiro - 2/24/16.
 */
class GetHostingBillingValue implements Getter<BillingValue> {
    def BillingContext context
    def WebSite webSite

    @Override
    BillingValue get() {
        def LicenseFee licenseFee = ObjectSelect.query(LicenseFee.class).where(COLLEGE.eq(context.college)
                .andExp(WEB_SITE.eq(webSite))
                .andExp(KEY_CODE.eq(hosting.dbValue)))
                .selectFirst(context.context)

        def description = MessageFormat.format(
                hosting.descTemplate,
                StringUtils.capitalize(licenseFee.getPlanName()),
                webSite.getName(),
                new SimpleDateFormat(DATE_MONTH_FORMAT).format(licenseFee.getRenewalDate()));

        return new BillingValue(code: StockCodes.valueOf(licenseFee.getPlanName()).productionCode,
                description: description,
                quantity: 1,
                unitPrice: licenseFee.fee)
    }
}
