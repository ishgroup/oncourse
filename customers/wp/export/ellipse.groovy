records.collectMany { Invoice i -> i.invoiceLines }.each { il ->

  def courseType = "Other"
  switch( il.enrolment?.courseClass?.course?.code) {
    case ~/^HR.*/:
      courseType = "HR"
      break
    case ~/.*^WB.*/:
      courseType = "PTS Admin"
      break
    case ~/.*^AD.*/:
      courseType = "PTS Admin"
      break
    case ~/.*^PTS.*/:
      courseType = "PTS Billable"
      break
    case ~/.*^EL.*/:
      courseType = "PTS Billable"
      break
  }

  def internalFlag = "unknown"
  if (il.enrolment?.student?.contact?.email != null) {
    internalFlag = il.enrolment.student.contact.email.endsWith("westernpower.com.au") ? "internal" : "external"
  }

  def localStartDate = il.enrolment?.courseClass?.startDateTime ? new java.sql.Date(il.enrolment?.courseClass?.startDateTime.getTime()).toLocalDate() : null

  csv << [
      "Invoice Date": il.invoice.invoiceDate?.format("dd/MM/yy"),
      "onCourse Invoice Number": il.invoice.invoiceNumber,
      "Contractor": il.invoice.contact.fullName,
      "Customer Code": il.invoice.contact.customField("Customer Code") ?: "",
      "Student name": il.enrolment?.student?.contact?.fullName,
      "Payroll Number": il.enrolment?.student?.contact?.customField("Employee ID") ?: "",
      "Course type": courseType,
      "Class code": il.enrolment?.courseClass?.course?.code+"-"+il.enrolment?.courseClass?.code,
      "Class start date": il.enrolment?.courseClass?.startDateTime?.format("dd/MM/yy"),
      "Class end date": il.enrolment?.courseClass?.endDateTime?.format("dd/MM/yy"),
      "Course name": il.enrolment?.courseClass?.course?.name,
      "Duration": il.enrolment?.courseClass?.sessions?.collect { it.durationInHours }?.sum() ?: "",
      "Location": il.enrolment?.courseClass?.room?.site?.name,
      "Fee": il.priceTotalExTax,
      "Tax Type": il.tax.taxCode,
      "Internal": internalFlag,
      "Reporting Month": [ il.invoice.invoiceDate, localStartDate ].max().format("MMM yy"),
      "Description": il.description,
      "Title": il.title,
      "Description required for billing": il.invoice.invoiceNumber+" "+il?.enrolment?.student?.contact?.fullName+" "+il.description,
      "Tax": il.taxEach,
      "Purchase Order": il.invoice.customerReference ?: "",
      "Attendance": "",
      "Ellipse Invoice/Journal": "",
      "Date": "",
  ]
}