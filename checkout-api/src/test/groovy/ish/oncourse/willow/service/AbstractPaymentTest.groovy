package ish.oncourse.willow.service

import ish.oncourse.willow.model.checkout.Application
import ish.oncourse.willow.model.checkout.CheckoutModelRequest
import ish.oncourse.willow.model.checkout.ContactNode
import ish.oncourse.willow.model.checkout.Enrolment
import ish.oncourse.willow.model.checkout.Voucher
import ish.oncourse.willow.model.checkout.payment.PaymentRequest
import ish.oncourse.willow.model.web.CourseClassPrice
import ish.oncourse.willow.model.web.Discount

abstract class AbstractPaymentTest extends ApiTest {

    protected PaymentRequest buildPaymentRequest() {
        new PaymentRequest().with { paymentRequest ->
            paymentRequest.sessionId = 'paymentRandomSession'
            paymentRequest.creditCardNumber = '5431111111111111'
            paymentRequest.creditCardName = 'john smith'
            paymentRequest.expiryMonth = '11'
            paymentRequest.expiryYear = '2027'
            paymentRequest.creditCardCvv = '321'
            paymentRequest.agreementFlag = true
            paymentRequest.payNow = 200.00
            paymentRequest.checkoutModelRequest = new CheckoutModelRequest().with { modelRequest ->
                modelRequest.contactNodes = [new ContactNode().with { cNode ->
                    cNode.contactId = '1001'
                    cNode.enrolments = [new Enrolment().with { e ->
                        e.classId = '1001'
                        e.contactId = '1001'
                        e.price = new CourseClassPrice().with { price ->
                            price.fee = 110.00
                            price.hasTax = true
                            price.appliedDiscount = new Discount().with { discount ->
                                discount.id = '1003'
                                discount.discountedFee = 66.00
                                discount.discountValue = 44.00
                                discount.title = 'title'
                                discount
                            }
                            price
                        }
                        e.selected = true
                        e
                    }]
                    cNode.applications = [new Application().with { a ->
                        a.contactId = '1001'
                        a.classId = '1002'
                        a.selected = true
                        a
                    }]
                    cNode.vouchers = [new Voucher().with { v ->
                        v.price = 100.00
                        v.value = 100.00
                        v.productId = '7'
                        v.contactId = '1001'
                        v.selected = true
                        v.isEditablePrice = true
                        v
                    }]
                    cNode
                }]
                modelRequest.promotionIds = ['1001']
                modelRequest.payerId = '1001'
                modelRequest
            }
            paymentRequest.payNow = 166.00
            paymentRequest
        }
    }
}
