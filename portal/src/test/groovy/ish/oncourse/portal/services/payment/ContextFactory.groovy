package ish.oncourse.portal.services.payment

import groovy.mock.interceptor.MockFor
import ish.common.types.PaymentStatus
import ish.math.Money
import ish.oncourse.model.Invoice
import ish.oncourse.model.PaymentIn
import ish.oncourse.model.PaymentTransaction

/**
 * User: akoiro
 * Date: 2/07/2016
 */
class ContextFactory {
    def Context get(Action action) {
        def context = new MockFor(Context)
        def paymentIn = new MockFor(PaymentIn)
        def invoice = new MockFor(Invoice)
        invoice.with {
            invoice.ignore.getAmountOwing { return new Money(20, 20) }
            invoice.ignore.getDateDue { return new Date() }
            invoice.ignore.getId { return 1L }
        }

        def paymentTransaction = new MockFor(PaymentTransaction)

        switch (action) {
            case Action.init:
                context.with {
                    ignore.getInvoice { return invoice.proxyInstance() }
                    ignore.getNotFinalPaymentIn { return null }
                    ignore.getPaymentIn { return null }
                }
                break
            case Action.make:
                context.with {
                    ignore.getPaymentIn { return null }
                    ignore.getPaymentTransaction { return null }
                    ignore.getInvoice { return invoice.proxyInstance() }
                    ignore.getNotFinalPaymentIn { return null }
                }
                break
            case Action.update:
                paymentIn.with {
                    ignore.getId { return 1L }
                    ignore.getStatus { return PaymentStatus.SUCCESS }
                }
                context.with {
                    ignore.getPaymentTransaction { return paymentTransaction.proxyInstance() }
                    ignore.getPaymentIn { return paymentIn.proxyInstance() }
                    ignore.getInvoice { return invoice.proxyInstance() }
                }
                break
        }
        return context.proxyInstance()
    }
}
