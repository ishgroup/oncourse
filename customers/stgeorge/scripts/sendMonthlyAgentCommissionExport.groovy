import ish.common.types.PaymentStatus
import ish.math.Money
import ish.oncourse.server.cayenne.PaymentIn
import ish.oncourse.server.cayenne.PaymentInLine
import ish.oncourse.server.cayenne.PaymentMethod
import org.apache.cayenne.ObjectContext
import org.apache.cayenne.query.ObjectSelect
import org.apache.commons.lang3.time.DateUtils

def run(args) {

    ObjectContext context = args.context

    Date upper = new Date()
    upper.set(dayOfMonth: 1, hourOfDay: 0, minute: 0, second: 0)
    Date lower = DateUtils.addMonths(upper, -1)

    List<PaymentInLine> paymentInLines = ObjectSelect.query(PaymentInLine)
            .and(PaymentInLine.PAYMENT_IN.dot(PaymentIn.STATUS).eq(PaymentStatus.SUCCESS),
            PaymentInLine.AMOUNT.gt(Money.ZERO),
            PaymentInLine.PAYMENT_IN.dot(PaymentIn.AMOUNT).gt(Money.ZERO)
                    .orExp(PaymentInLine.PAYMENT_IN.dot(PaymentIn.PAYMENT_METHOD).dot(PaymentMethod.TYPE).eq(PaymentType.CONTRA)),
            PaymentInLine.PAYMENT_IN.dot(PaymentIn.REVERSED_BY).outer().dot(PaymentIn.ID).isNull(),
            PaymentInLine.PAYMENT_IN.dot(PaymentIn.PAYMENT_METHOD).dot(PaymentMethod.NAME).nin("glep transfer", "payment via agent"),
            PaymentInLine.PAYMENT_IN.dot(PaymentIn.CREATED_ON).between(lower, upper))
            .orderBy(PaymentInLine.PAYMENT_IN.dot(PaymentIn.CREATED_ON).asc())
            .select(context)

    String fileName = lower.format("'CricosAgentCommissionExport_'MMMM'.csv'")

    email {
        from preference.email.from
        subject fileName
        to "international@sgscc.edu.au"
        content "Cricos Agent Commission Export from ${lower} to ${upper}"
        attachment fileName, 'text/csv', export {
            template 'sgscc.onCourse.paymentCricosAgentCommission.csv'
            records paymentInLines
        }
    }
}