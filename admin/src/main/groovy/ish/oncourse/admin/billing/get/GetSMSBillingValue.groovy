package ish.oncourse.admin.billing.get

import ish.oncourse.admin.billing.BillingValue
import ish.oncourse.admin.services.billing.StockCodes
import ish.oncourse.model.LicenseFee
import org.apache.cayenne.query.ObjectSelect
import org.apache.cayenne.query.SQLSelect

import static ish.oncourse.admin.billing.LicenseCode.sms

/**
 *
 * akoiro - 2/23/16.
 */
class GetSMSBillingValue implements Getter<BillingValue> {
    static final String SQL =
            'SELECT count(id) as count FROM MessagePerson ' +
                    'WHERE collegeId = #bind($collegeId) ' +
                    'AND type = 2 ' +
                    'AND timeOfDelivery >= #bind($from) ' +
                    'AND timeOfDelivery <= #bind($to)'

    def BillingContext context


    @Override
    BillingValue get() {

        LicenseFee licenseFee = ObjectSelect.query(LicenseFee.class).where(LicenseFee.COLLEGE.eq(context.college)
                .andExp(LicenseFee.KEY_CODE.eq(sms.dbValue))).selectFirst(context.context)

        def value = (Long) SQLSelect.dataRowQuery(SQL).params([
                collegeId: context.college.id,
                from     : context.from,
                to       : context.to
        ]).selectFirst(context.context).values().first()

        BillingValue result = new BillingValue(code: StockCodes.SMS.productionCode,
                description: sms.simpleDesc,
                quantity: value,
                unitPrice: licenseFee.fee
        )
        return result
    }
}
