records = query {
    entity "Invoice"
    query "contact.lastName is \"Smart and Skilled - Department of Industry\" and amountOwing > 0 and dateDue < 01/07/2021"
    context context
  }

// Looks for invoices under the name ‘Smart and Skilled - Department of Industry’ that have no payment plan, have an amount owing > $0, have a due date of before 1st July 2021, and one invoice line linked to one enrolment in a class with an end date.
// When an invoice meets the above conditions, change its due date to the same date as the end of the class.

for (Invoice i:records) {
  if  (i.invoiceLines.size() != 1){
    break
  }
  if (!i.invoiceLines.first().enrolment){
    break
  }
  if (!i.invoiceLines.first().courseClass.endDateTime){
    break
  }
  context.localObject(i).dateDue = i.invoiceLines.first().courseClass.endDateTime.toInstant().atZone(java.time.ZoneId.systemDefault()).toLocalDate()
}
context.commitChanges()
