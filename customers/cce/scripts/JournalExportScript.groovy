/*
 * Copyright ish group pty ltd. All rights reserved. https://www.ish.com.au
 * No copying or use of this code is allowed without permission in writing from ish.
 */

import ish.math.Money
import ish.common.types.AccountTransactionType
import ish.common.types.PaymentType
import java.time.format.DateTimeFormatter
import groovy.time.TimeCategory

def context

/*

Output transaction data in PeopleSoft format.

- Any account codes with full stops are split on the full stop and the additional characters put to the analysis code
- Transactions are grouped in special and slightly complicated ways to avoid problems with PeopleSoft importing what it thinks are duplicates
- GST values need to be attached to the correct transaction lines rather than being separate as in onCourse

*/

class Globals {

    static DEBUG = false

    static today = new Date()
    static DateTimeFormatter dateFormat = DateTimeFormatter.ofPattern("ddMMyyyy")
    static moneyFormat = new DecimalFormat("###########0.00")
    static LINE_SEPARATOR = System.getProperty('line.separator')
    static JOURNAL_SOURCE = 'CE1'
    static BUSINESS_UNIT = 'UNSYD'
    static CURRENCY_CODE = 'AUD'
    static STORE_NUMBER = '3563267'
    static STORE_NUMBER_EFTPOS = '3571849'
    static AGENCY_NUMBER = '90001'
    static GST_ACCOUNT = "1350A000100000"
    static BANK_ACCOUNT = "0050A000100000"
    static EFT_ACCOUNT = "12324330111111"
    static PREPAID_FEES = "32224330111111"
}

class OutputLine {

    AccountTransaction transaction
    Account account
    String analysis
    BigDecimal amount
    String description
    def reference
    BigDecimal taxAmount // this should be null if GST isn't a valid concept for this transaction
    String reportingDate //stored as string to avoid issues with LocalDate and Date differences
    String groupByHash // group the output by this

    def setReportingDate(d) {
        if (d == null) {
            this.reportingDate = Globals.today.format('ddMMyyyy')
        } else if (d instanceof java.time.LocalDate) {
            this.reportingDate = d.format(Globals.dateFormat)
        } else if (d instanceof String) {
            this.reportingDate = d
        } else { // Date
            this.reportingDate = d.format('ddMMyyyy')
        }
    }

    def setAmount(amt) {
        if (amt instanceof Money) {
            this.amount = amt.toBigDecimal()
        } else {
            this.amount = amt
        }
    }
    def setTaxAmount(amt) {
        if (amt instanceof Money) {
            this.taxAmount = amt.toBigDecimal()
        } else {
            this.taxAmount = amt
        }
    }

    String toString() {
        def b = new StringBuffer()
        b.append('D')
        b.append(Globals.JOURNAL_SOURCE )
        b.append(this.account.accountCode.padLeft(14).take(14) )
        b.append(Globals.moneyFormat.format(this.amount).padLeft(16).take(16) )
        b.append(this.description.replaceAll("[\n\r\t]", "").padRight(30).take(30) )
        b.append(this.analysis.padRight(10).take(10))
        b.append(String.valueOf(this.reference).padRight(10).take(10) )

        def taxCode = ''
        def vatTransactionType = ''
        if (taxAmount != null) {
            taxCode = (taxAmount == 0) ? 'GSTZER' : 'GSTSTD'
            vatTransactionType = (taxAmount == 0) ? 'FREE' : 'SALE'
        }

        b.append(taxCode.padRight(8).take(8) )
        b.append( (this.taxAmount ? Globals.moneyFormat.format(this.taxAmount) : '0.00').padLeft(16).take(16) )
        b.append(vatTransactionType.padRight(4).take(4) )

        b.append(Globals.BUSINESS_UNIT)
        b.append(Globals.CURRENCY_CODE)
        b.append(this.reportingDate)

        if (Globals.DEBUG) {
            b.append( "  " + this.transaction?.relatedInvoiceLine?.id )
            b.append( "  " + this.transaction?.account.type )
            b.append( "  " + this.transaction?.account.description )
        }

        b.append(Globals.LINE_SEPARATOR)

        return b.toString()
    }

    OutputLine plus(OutputLine other) {
        // add up two lines
        if (this.account.accountCode.take(14) != other.account.accountCode.take(14)) {
            throw RuntimeException("Tried to merge two transaction with different accounts.")
        }
        def line = new OutputLine()
        line.groupByHash = this.groupByHash
        line.account = this.account
        line.analysis = this.analysis
        line.amount = this.amount + other.amount
        line.description = this.description
        line.reference = this.reference
        line.taxAmount = (this.taxAmount == null || other.taxAmount == null) ? null : this.taxAmount + other.taxAmount
        line.reportingDate = this.reportingDate

        return line
    }
}


def run(args) {

    def dir = new File('finance-export')
    context = args.context

    if (!dir.exists()) {
        dir.mkdir()
    }

    def accountingDate = Globals.today.format('ddMMyyyy')

    def fileName = "finance-export/${Globals.JOURNAL_SOURCE}_${accountingDate}_001_${Globals.BUSINESS_UNIT}_${Globals.CURRENCY_CODE}"
    def outputLines = [:]

    /**
        Sends output file to specified emails when true
    */
    def debug = false
    def debugString = ""

    getAccountTransactions().each() { transaction ->

        line = new OutputLine()
        line.transaction = transaction
        line.account = transaction.getAccount()

        if(line.account.accountCode.split('\\.').size() > 1) {
            line.analysis = line.account.accountCode.split('\\.')[1]
        } else {
            line.analysis = ''
        }
        line.groupByHash = transaction.id // by default no grouping

        if (isCredit(transaction)) {
            line.amount = transaction.getAmount().negate()
        } else {
            line.amount = transaction.getAmount()
        }

        if (AccountTransactionType.INVOICE_LINE == transaction.getTableName()) {
            writeInvoiceLine(line)

        } else if (AccountTransactionType.PAYMENT_IN_LINE == transaction.getTableName()) {
            def paymentLine = AccountTransaction.getPaymentInLineForTransaction(context, line.transaction)
            writePaymentLine(line, paymentLine, paymentLine.paymentIn, paymentLine.paymentIn.payer)

        } else if (AccountTransactionType.PAYMENT_OUT_LINE == transaction.getTableName()) {
            def paymentLine = AccountTransaction.getPaymentOutLineForTransaction(context, line.transaction)
            writePaymentLine(line, paymentLine, paymentLine.paymentOut, paymentLine.paymentOut.payee)

        } else {
            writeOtherLine(line)
        }

        def oldLine = outputLines.get(line.groupByHash)
        if (oldLine && !Globals.DEBUG) {
            outputLines.put(line.groupByHash, oldLine + line)
        } else {
            outputLines.put(line.groupByHash, line)
        }

    }

    // Collect a list of GST transactions so we can link them to income/liability transactions as appropriate
    def linkedToInvoices = outputLines.values().findAll{ line -> line.transaction && AccountTransactionType.INVOICE_LINE == line.transaction.getTableName() }
            .groupBy{ line -> line.transaction.relatedInvoiceLine }

    // now we have a map of output lines grouped by invoiceLine
    linkedToInvoices.each{ invoiceLine, lines ->
        def gstLine = lines.find{ l -> l.transaction.account.accountCode.startsWith(Globals.GST_ACCOUNT)}

        if (gstLine && gstLine.amount != 0) {
            def liabilityLines = lines.findAll{ l -> l.transaction.account.isIncome() || l.transaction.account.isLiability() } - gstLine
            if (Globals.DEBUG) {
                liabilityLines.each { it.description = "*L*" + it.description}
            }

            if (liabilityLines.size() == 0) {
                // this is very unusual and casued by an invoice line with GST != 0 but $0 net amount. OS-39220
                // the PeopleSoft file will not be balanced for GST, but nothing we can do
            } else {
                // sort them by finding the ones closest to 10 times the GST
                lineToAttach = liabilityLines.sort { (it.amount - (gstLine.amount * 10)).abs() }.first()
                lineToAttach.taxAmount = gstLine.amount
            }

        } else if (invoiceLine.totalTax == 0){

            // Invoice lines with no GST need special treatment to know where to attach the GST code
            def firstLiability = ObjectSelect.query(AccountTransaction)
                    .where(AccountTransaction.TABLE_NAME.eq( "I" ))
                    .and(AccountTransaction.FOREIGN_RECORD_ID.eq( invoiceLine.id ))
                    .and(AccountTransaction.ACCOUNT.dot(Account.TYPE).eq( AccountType.LIABILITY ))
                    .orderBy(AccountTransaction.CREATED_ON.asc())
                    .selectFirst(context)

            if (!firstLiability) {
                // if we don't find a liability then this in a manual invoice not linked to an enrolment
                // so let's find the biggest income transaction and use that instead
                firstLiability = ObjectSelect.query(AccountTransaction)
                        .where(AccountTransaction.TABLE_NAME.eq( "I" ))
                        .and(AccountTransaction.FOREIGN_RECORD_ID.eq( invoiceLine.id ))
                        .and(AccountTransaction.ACCOUNT.dot(Account.TYPE).eq( AccountType.INCOME ))
                        .orderBy(AccountTransaction.AMOUNT.desc())
                        .selectFirst(context)
            }

            // see if this transaction is in the export for today
            def exportLine = lines.find { it.transaction.equals(firstLiability) }

            if ( exportLine ) {
                exportLine.taxAmount = BigDecimal.ZERO
            }
        }
    }


    // find all blank lines and remove them
    outputLines = outputLines.findAll { entry ->
        (entry.value.taxAmount && entry.value.taxAmount != 0.0)  || entry.value.amount != 0.0
    }

    writer = new File(fileName).newWriter()
    writer.write(writeHeader(outputLines))

    outputLines.values().each { line ->
        writer.write(line.toString())
        debugString += line.toString() + " \n"
    }
    writer.close()

    // Send the data to the remote server for upload to PeopleSoft
    // def rsync = "/usr/local/bin/rsync -av /usr/local/onCourse/finance-export/ finance@172.20.4.24:/home/finance/cce"
    // rsync.execute()

    email {
        to "ivaneliya.dimitrova@sydney.edu.au", "natalia.borisova@sydney.edu.au"
        from "info@cce.sydney.edu.au", "Centre for Continuing Education"
        subject "CCE onCourse Export"
        content debugString
        attachment "CE1_001_UNSYD_AUD", "text/plain",  new File(fileName).text
    }

    if (debug) {
        email {
            to "support@ish.com.au"
            cc "tracy.pham@sydney.edu.au","natalia.borisova@sydney.edu.au","psu.finance@sydney.edu.au"
            from "operations@cce.sydney.edu.au"
            subject "CCE onCourse Export for PeopleSoft"
            content debugString
        }
    }
}

def getAccountTransactions() {

    def end_date = Globals.today
    end_date.set(hourOfDay: 18, minute: 0, second: 0)

    def list = ObjectSelect.query(AccountTransaction)
            .where(AccountTransaction.CREATED_ON.gte( end_date - 1))
            .and(AccountTransaction.CREATED_ON.lt(end_date))
            .orderBy(AccountTransaction.CREATED_ON.asc())
            .select(args.context)

    return list
}

def isCredit(t) {
    return (t.getAccount().isIncome() || t.getAccount().isLiability() || t.getAccount().isEquity())
}

def writeInvoiceLine(line) {

    def invoiceLine = line.transaction.relatedInvoiceLine

    line.description = invoiceLine.description ?: invoiceLine.title ?: ""
    line.reference = invoiceLine.invoice.invoiceNumber
    line.reportingDate = Globals.today
}

def writePaymentLine(line, paymentLine, payment, payer) {

    line.reference = paymentLine.invoice.invoiceNumber
    line.taxAmount = null
    line.reportingDate = payment.dateBanked

    settlementDate = payment.dateBanked ?: Globals.today
    def paymentType = payment.paymentMethod.type

    if (paymentType == PaymentType.CONTRA) {
        line.description = "Contra payment" + payment.createdOn.format('ddMMyy')
        line.reference = "9999"
        line.groupByHash = "contra" + line.transaction.account.accountCode

    } else if (paymentType == PaymentType.INTERNAL) {
        line.description = "Zero payment" + payment.createdOn.format('ddMMyy')
        line.reference = "9999"
        line.groupByHash = "zero" + line.transaction.account.accountCode

    } else if (paymentType == PaymentType.REVERSE) {
        line.description = "Reverse payment" + payment.createdOn.format('ddMMyy')
        line.reference = "9999"
        line.groupByHash = "reverse" + line.transaction.account.accountCode

    } else if (! line.transaction.account.accountCode.startsWith(Globals.BANK_ACCOUNT)) { // debtor transaction
        line.description = payment.paymentMethod.name + " " + payer.fullName

    } else if (payment.creditCardType == CreditCardType.AMEX) {
        line.description = "USX" + Globals.STORE_NUMBER + "-USX Banking" + settlementDate.format('ddMMyy')
        line.reference = "USX" + settlementDate.format('ddMMyy')
        line.groupByHash = line.description

    } else if (payment.creditCardType) {
        line.description = "USD" + Globals.STORE_NUMBER + "-USD Banking" + settlementDate.format('ddMMyy')
        line.reference = "USD" + settlementDate.format('ddMMyy')
        line.groupByHash = line.description

    } else if (paymentType == PaymentType.CHEQUE || paymentType == PaymentType.CASH) {
        line.description = "90001" + "-Banking" + settlementDate.format('ddMMyy')
        line.reference = "90001"
        line.groupByHash = "cash_or_cheque"

    } else if (payment.paymentMethod.name == "EFTPOS") {
        line.description = "USD" + Globals.STORE_NUMBER_EFTPOS + "-USD Banking" + settlementDate.format('ddMMyy')
        line.reference = "USD" + Globals.STORE_NUMBER_EFTPOS
        line.groupByHash = "eftpos"

    } else {
        line.description = "90001" + "-Banking" + settlementDate.format('ddMMyy')
        line.reference = "90001"
        line.groupByHash = "cash_or_cheque"
    }
}

def writeOtherLine(line) {
    line.description = ""
    line.reference = line.transaction.id
    line.reportingDate = line.transaction.transactionDate
}

String writeHeader(outputLines) {
    def accountingDate = Globals.today.format('ddMMyyyy')
    def totalDebit = outputLines.values().collect{(it.amount > 0) ? it.amount : 0}.sum()
    def totalCredit = outputLines.values().collect{(it.amount < 0) ? it.amount : 0}.sum()

    def header = new StringBuffer()
    header.append('H')
    header.append(Globals.JOURNAL_SOURCE)
    header.append(accountingDate)
    header.append(''.padLeft(4) )
    header.append(accountingDate)
    header.append(String.valueOf(outputLines.size()).padLeft(5).take(5) )
    header.append(Globals.moneyFormat.format(totalDebit).padLeft(16).take(16) )
    header.append(Globals.moneyFormat.format(totalCredit).padLeft(16).take(16) )
    header.append(Globals.BUSINESS_UNIT)
    header.append(Globals.LINE_SEPARATOR)
    return header.toString()
}
