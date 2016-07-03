package ish.oncourse.portal.services.payment

/**
 * User: akoiro
 * Date: 3/07/2016
 */
enum WarningMessage {
    someBodyElseAlreadyPaidThisInvoice,
    thereIsProcessedPayment,
    invoiceAlreadyPayed,
    thereAreNotUnbalancedInvoices,
    invalidCardNumber,
    invalidCardName,
    invalidCardCvv,
    invalidCardDate,
}
