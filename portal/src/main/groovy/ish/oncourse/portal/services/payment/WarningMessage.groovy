package ish.oncourse.portal.services.payment

/**
 * User: akoiro
 * Date: 3/07/2016
 */
enum WarningMessage {
    thisInvoiceAlreadyPaid,
    thereIsPaymentInTransaction,
    thereAreNotUnbalancedInvoices,
    invalidCardNumber,
    invalidCardName,
    invalidCardCvv,
    invalidCardDate,
	amountLessThan20Dollars,
	amountLessThanOwing,
	amountMoreThanOwing,
	amountWrong
}
