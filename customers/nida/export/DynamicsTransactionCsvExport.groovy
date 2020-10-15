import ish.oncourse.server.export.CsvBuilder
import org.supercsv.io.CsvListWriter
import org.supercsv.prefs.CsvPreference
import org.supercsv.quote.ColumnQuoteMode

csv = new CsvBuilder(output)
csv.csvListWriter = new CsvListWriter(output, new CsvPreference.Builder(csv.quote, csv.delimiter, csv.endOfLine).useQuoteMode(new ColumnQuoteMode(14)).build())

def currentDate = new Date()

records.each { AccountTransaction at ->
        def invoiceLine = at.getInvoiceLineForTransaction(at.context, at)
        def paymentInLine = at.getPaymentInLineForTransaction(at.context, at)
        def paymentOutLine = at.getPaymentOutLineForTransaction(at.context, at)
        
        def cc = invoiceLine?.enrolment ? invoiceLine?.enrolment?.courseClass : invoiceLine?.courseClass

        csv << [
                "Id" : at.id,
                "Company Code"       : "01",
                "Batch Number"       : currentDate.format('ddMMyyyy').reverse(),
                "Batch Description"  : currentDate.format('dd/MM/yyyy') + ' onCourse export',
                "Document Number"    : '123456',
                "Document Detail"    : '',
                "Document Date"      : currentDate.format('dd/MM/yyyy'),
                "Posting Date"       : currentDate.format('dd/MM/yyyy'),
                "Line Company"       : '01',
                "Account Number"     : at.account.accountCode.split('/').getAt(0),
                "Cash Analysis Code" : '',
                "Quantity"           : '1',
                "Amount"             : AccountType.CREDIT_TYPES.contains(at.account.type) ? at.amount.toBigDecimal().negate() : at.amount.toBigDecimal(),
                "Narration"          : ['onCourse', 
                                        at.source, 
                                        at.invoiceNumber,
                                        at.contactName, 
                                        at.invoiceDescription ? "(${at.invoiceDescription.trim()})" : null, 
                                        at.paymentType, 
                                        'processed on',
                                        at.transactionDate.format('dd/MM/yyyy hh.mma').replace('AM', 'a.m.').replace('PM', 'p.m.')
                                       ].findAll().join(' '),
                "Rate"               : '1',
                "Multiply Rate"      : '0',
                "State"              : cc?.room?.site?.state,
                "Course Code"        : cc?.course?.code,
                "Class Code"         : cc?.code,
                "Settlement date"    : paymentInLine ? 
                                               paymentInLine.paymentIn?.dateBanked?.format('dd/MM/yyyy') : paymentOutLine ?
                                                       paymentOutLine.paymentOut?.dateBanked?.format('dd/MM/yyyy') : ''
        ]
}