package ish.oncourse.admin.billing.get

import ish.oncourse.admin.billing.BillingValue
import ish.oncourse.admin.services.billing.StockCodes
import ish.oncourse.model.LicenseFee
import org.apache.cayenne.query.ObjectSelect
import org.apache.cayenne.query.SQLSelect

import java.text.MessageFormat

import static ish.common.types.PaymentSource.SOURCE_ONCOURSE
import static ish.oncourse.admin.billing.LicenseCode.ccOffice
import static java.lang.Math.max

/**
 *
 * akoiro - 2/24/16.
 */
class GetOfficeCCBillingValue extends AbstractGetter<BillingValue> {
    static final String SQL = 'SELECT count(id) as count FROM PaymentIn WHERE collegeId = #bind($collegeId) ' +
            'AND created >= #bind($from) ' +
            'AND created <= #bind($to) ' +
            'AND source = #bind($source) ' +
            'AND type = 2 AND (status = 3 OR status = 6)'

    def Integer transactions

    @Override
    protected BillingValue innerGet() {
        def freeTransactions = licenseFee.freeTransactions ? licenseFee.freeTransactions : 0

        def description = freeTransactions > 0 ?
                MessageFormat.format(ccOffice.descTemplate,
                        transactions, freeTransactions) : ccOffice.simpleDesc

        BillingValue billingValue = new BillingValue(code: StockCodes.OFFICE_CC.productionCode,
                description: description,
                quantity: max(0, transactions - freeTransactions),
                unitPrice: licenseFee.fee)

        return billingValue;
    }

    @Override
    protected init() {
        licenseFee = ObjectSelect.query(LicenseFee.class).where(LicenseFee.COLLEGE.eq(context.college)
                .andExp(LicenseFee.KEY_CODE.eq(ccOffice.dbValue))).selectFirst(context.context)

        transactions = (Integer) SQLSelect.dataRowQuery(SQL).params([
                collegeId: context.college.id,
                from     : context.from,
                to       : context.to,
                source   : SOURCE_ONCOURSE.databaseValue
        ]).selectFirst(context.context).values().first()
    }
}

