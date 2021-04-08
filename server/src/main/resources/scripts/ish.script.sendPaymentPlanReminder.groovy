import java.time.LocalDate
import java.time.Period

def today = LocalDate.now()
def plusWeek = today.plusDays(7)

records = query {
    entity "Invoice"
    query "amountOwing > 0 and dateDue <= ${plusWeek}"
}
records = records.findAll { i ->
    plusWeek.isEqual(i.dateDue) || // 7 days before the payment due date
            today.isEqual(i.dateDue.plusDays(1)) || // day after the payment is due to avoid a $0 payable instance
            ((Period.between(today, i.dateDue).days % 7 == 0) && i.overdue.isGreaterThan(Money.ZERO)) // every 7 days of overdue
}

message {
    template paymentReminderTemplate
    record records
}