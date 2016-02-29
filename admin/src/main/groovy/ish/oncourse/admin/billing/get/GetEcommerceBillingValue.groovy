package ish.oncourse.admin.billing.get

import ish.math.Country
import ish.oncourse.admin.billing.BillingValue
import ish.oncourse.admin.services.billing.StockCodes
import ish.oncourse.model.LicenseFee
import ish.oncourse.model.WebSite
import org.apache.cayenne.query.ObjectSelect
import org.apache.cayenne.query.SQLSelect

import java.math.RoundingMode
import java.text.DecimalFormat
import java.text.MessageFormat
import java.text.NumberFormat

import static ish.common.types.PaymentSource.SOURCE_WEB
import static ish.oncourse.admin.billing.LicenseCode.ecommerce
import static ish.oncourse.model.auto._LicenseFee.*

/**
 *
 * akoiro - 2/24/16.
 */
class GetEcommerceBillingValue implements Getter<BillingValue> {

    static final String SQL = 'SELECT SUM(totalGst) FROM Invoice ' +
            'where collegeId = #bind($collegeId) AND ' +
            'webSiteId = #bind($webSiteId) AND ' +
            'created >= #bind($from) AND ' +
            'created <= #bind($to) AND ' +
            'source = #bind($source)';

    def BillingContext context
    def WebSite webSite

    def LicenseFee licenseFee
    def BigDecimal value

    @Override
    BillingValue get() {
        init()

        def decimalFormatter = new DecimalFormat();
        decimalFormatter.setRoundingMode(RoundingMode.valueOf(2));

        def moneyFormat = NumberFormat.getCurrencyInstance(Country.AUSTRALIA.locale());
        moneyFormat.setMinimumFractionDigits(2);

        def description = MessageFormat.format(
                (licenseFee.freeTransactions > 0 ? ecommerce.descTemplate : ecommerce.simpleDesc),
                webSite.name,
                decimalFormatter.format(licenseFee.fee.doubleValue() * 100),
                moneyFormat.format(value),
                moneyFormat.format(licenseFee.freeTransactions));

        return new BillingValue(code: StockCodes.ECOMMERCE.productionCode,
                description: description,
                quantity: 1,
                unitPrice: value.subtract(licenseFee.freeTransactions).multiply(licenseFee.fee));
    }

    def init() {
        licenseFee = ObjectSelect.query(LicenseFee.class).where(COLLEGE.eq(context.college)
                .andExp(WEB_SITE.eq(webSite))
                .andExp(KEY_CODE.eq(ecommerce.dbValue)))
                .selectFirst(context.context)


        value = (BigDecimal) SQLSelect.dataRowQuery(SQL).params([
                collegeId: context.college.id,
                webSiteId: webSite.id,
                from     : context.from,
                to       : context.to,
                source   : SOURCE_WEB.databaseValue
        ]).selectFirst(context.context).values().first()
    }
}
