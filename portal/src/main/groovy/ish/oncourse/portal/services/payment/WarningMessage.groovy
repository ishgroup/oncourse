package ish.oncourse.portal.services.payment

/**
 * User: akoiro
 * Date: 3/07/2016
 */
enum WarningMessage {
    someBodyElseAlreadyPaidThisInvoice,
    thereIsProcessedPayment,
    thereAreNotUnbalancedInvoices,
    invalidCardNumber,
    invalidCardName,
    invalidCardCvv,
    invalidCardDate,
}
