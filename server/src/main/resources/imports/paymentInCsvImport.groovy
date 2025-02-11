import ish.oncourse.entity.services.SetPaymentMethod
import ish.oncourse.server.imports.CsvParser
import ish.util.SetBankingMethod

import java.time.format.DateTimeFormatter

logger = LogManager.getLogger(getClass())

logger.info('PaymentIn CSV import started')

int i = 0
CsvParser reader = new CsvParser(new InputStreamReader(new ByteArrayInputStream(importFile)))

reader.eachLine { line  ->
    i++

    SystemUser currentUser = getSystemUsersByRole.getCurrentUser()

    //get line values
    String rawInvoiceValues = line.'payment.invoice'
    List<Long> invoiceNumbers = rawInvoiceValues.contains(',') ? rawInvoiceValues.split(',')*.toLong() : [rawInvoiceValues?.toLong()]
    LocalDate dateBanked = line.'payment.dateBanked' ? LocalDate.parse(line.'payment.dateBanked', DateTimeFormatter.ofPattern('d/M/yy')) : null
    Money amount = line.'payment.amount' ? new Money(line.'payment.amount'.replace(',', '').trim()) : null
    String paymentMethodName = line.'paymentIn.paymentMethod.name'
    String chequeBank = line.'payment.chequeBank'
    String chequeBranch = line.'payment.chequeBranch'
    String chequeDrawer = line.'payment.chequeDrawer'

    logger.info("${invoiceNumbers}, ${dateBanked}, ${amount}, ${paymentMethodName}, ${chequeBank}, ${chequeBranch}, ${chequeDrawer}")

    //validate the line

    if (invoiceNumbers.empty || !invoiceNumbers.findAll { it -> it == null }.empty) {
        throwException("validation error - Row [${i}]: Invoice number can not be blank.")
    }

    if (dateBanked == null) {
        throwException("validation error - Row [${i}]: Date banked can not be blank.")
    }

    if (amount == null) {
        throwException("validation error - Row [${i}]: Amount can not be blank.")
    }

    if (paymentMethodName == null) {
        throwException("validation error - Row [${i}]: Payment method name can not be blank.")
    }

    List<Invoice> relatedInvoices = invoiceNumbers.collect { invoiceNumber ->
        ObjectSelect.query(Invoice)
                .where(Invoice.INVOICE_NUMBER.eq(invoiceNumber))
                .selectOne(context)
    }

    if (!relatedInvoices.findAll { ri -> ri == null}.empty) {
        throwException("validation error - Row [${i}]: One of Invoice numbers ${invoiceNumbers} does not exist in onCourse.")
    }

    Invoice badInvoice = relatedInvoices.find { relatedInvoice -> relatedInvoice.amountOwing.isNegative() }
    if (badInvoice) {
        throwException("validation error - Row [${i}]: Invoice [${badInvoice.invoiceNumber}] has negative amount [${amount}].")
    }

    Money allAmountOwing = relatedInvoices*.amountOwing.inject { Money current, Money val -> current.add(val) }
    if (allAmountOwing.isLessThan(amount)) {
        throwException("validation error - Row [${i}]: Amount [${amount}] can not be greater than invoice amount owing [${allAmountOwing}].")
    }

    LocalDate transactionLockedDate = transactionLockedService.transactionLocked

    if ((dateBanked <=> transactionLockedDate) < 1) {
        throwException("validation error - Row [${i}]: Payment In can not be processed in for a period that has been finalised.")
    }

    if (dateBanked.isAfter(LocalDate.now())) {
        throwException("validation error - Row [${i}]: Payment In can not be processed with a date in the future.")
    }

    if (amount.isZero() || amount.isLessThan(Money.ZERO())) {
        throwException("validation error - Row [${i}]: Amount can not be equal to or less than \$0.00.")
    }

    PaymentMethod paymentMethod = ObjectSelect.query(PaymentMethod)
            .where(PaymentMethod.NAME.eq(paymentMethodName))
            .selectOne(context)

    if (paymentMethod == null) {
        throwException("validation error - Row [${i}]: No payment method of this type: [${paymentMethodName}] exists.")
    }

    if ([PaymentType.CREDIT_CARD, PaymentType.CONTRA, PaymentType.INTERNAL, PaymentType.VOUCHER].contains(paymentMethod.type)) {
        throwException("validation error - Row [${i}]: Payment Method of this type: [${paymentMethod.type}] not allowed during import. Process this payment manually.")
    }

    if (PaymentType.CHEQUE == paymentMethod.type && (chequeBank == null || chequeBranch == null || chequeDrawer == null)) {
        throwException("validation error - Row [${i}]: Cheque information provided is incorrect or incomplete. [${chequeBank}, ${chequeBranch}, ${chequeDrawer}]")
    }

    //process the line

    context.newObject(PaymentIn).with { PaymentIn paymentIn ->
        paymentIn.source = PaymentSource.SOURCE_ONCOURSE
        paymentIn.paymentDate = dateBanked
        paymentIn.administrationCentre = context.localObject(currentUser.defaultAdministrationCentre)
        paymentIn.amount = amount
        paymentIn.confirmationStatus = ConfirmationStatus.DO_NOT_SEND
        paymentIn.createdBy = context.localObject(currentUser)
        paymentIn.payer = relatedInvoices[0].contact
        paymentIn.status = PaymentStatus.SUCCESS
        SetPaymentMethod.valueOf(paymentMethod, paymentIn).set()
        SetBankingMethod.valueOf(paymentIn, dateBanked).set()

        if (PaymentType.CHEQUE == paymentMethod.type) {
            paymentIn.chequeBank = chequeBank
            paymentIn.chequeBranch = chequeBranch
            paymentIn.chequeDrawer = chequeDrawer
        }

        Money currentAmount = amount

        relatedInvoices.each { relatedInvoice ->
            context.newObject(PaymentInLine).with { paymentInLine ->
                paymentInLine.payment = paymentIn
                paymentInLine.invoice = relatedInvoice
                paymentInLine.account = relatedInvoice.debtorsAccount

                Money pilAmount = currentAmount.isGreaterThan(relatedInvoice.amountOwing) ? relatedInvoice.amountOwing : currentAmount
                currentAmount = currentAmount.subtract(pilAmount)

                paymentInLine.amount = pilAmount

            }
        }
    }
}

context.commitChanges()

logger.info('PaymentIn CSV import finished')

def throwException(String message) {
    logger.error(message)
    throw new Exception(message)
}
