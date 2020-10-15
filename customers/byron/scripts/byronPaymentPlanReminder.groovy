import java.time.LocalDate
import java.time.temporal.ChronoUnit

def run(args) {

    def context = args.context

    def today = LocalDate.now()
    def plusTwoWeek = today.plusDays(14)

    def invoices = ObjectSelect.query(Invoice)
            .where(Invoice.AMOUNT_OWING.gt(Money.ZERO))
            .and(Invoice.DATE_DUE.lte(plusTwoWeek))
            .select(context)

    invoices.findAll { i ->
        plusTwoWeek.isEqual(i.dateDue) || // 14 days before the payment due date
                today.isEqual(i.dateDue) || // day the payment is due
                ((ChronoUnit.DAYS.between(today, i.dateDue) % 14 == 0) && i.overdue.isGreaterThan(Money.ZERO)) // every 14 days of overdue
    }.each { i ->
        email {
            template "Byron Payment reminder"
            bindings invoice: i
            to i.contact
        }
    }
}