package ish.oncourse.willow.checkout.payment

import groovy.transform.CompileStatic
import ish.common.types.PaymentSource
import ish.common.types.PaymentStatus
import ish.common.types.PaymentType
import ish.oncourse.model.College
import ish.oncourse.model.PaymentIn
import ish.oncourse.services.paymentexpress.IPaymentGatewayService
import ish.oncourse.willow.model.checkout.payment.PaymentResponse
import ish.oncourse.willow.model.common.CommonError
import org.apache.cayenne.ObjectContext
import org.apache.cayenne.query.ObjectSelect
import org.slf4j.Logger
import org.slf4j.LoggerFactory

import javax.ws.rs.BadRequestException
import javax.ws.rs.core.Response

import static ish.oncourse.willow.model.checkout.payment.PaymentStatus.*

@CompileStatic
class GetPaymentStatus {
    final static  Logger logger = LoggerFactory.getLogger(GetPaymentStatus.class)

    ObjectContext context 
    String sessionId
    College college



    GetPaymentStatus(ObjectContext context, College college, String sessionId) {
        this.context = context
        this.sessionId = sessionId
        this.college = college
    }
    
    
    PaymentResponse get() {
        List<PaymentIn> payments = (((ObjectSelect.query(PaymentIn)
                .where(PaymentIn.SESSION_ID.eq(sessionId))
                & PaymentIn.TYPE.eq(PaymentType.CREDIT_CARD))
                & PaymentIn.SOURCE.eq(PaymentSource.SOURCE_WEB)) 
                & PaymentIn.COLLEGE.eq(college)).select(context)
        
        if (payments.empty) {
            throw new BadRequestException(Response.status(400).entity(new CommonError(message: 'Payment not exist')).build())
        } else if (payments.size() > 1) {
            logger.error("More that one credit card payment with the same sessionId: $sessionId, college id: $college.id")
            throw new IllegalStateException("More that one credit card payment with the same sessionId")
        }
        
        PaymentIn payment = payments[0]
        PaymentResponse response = new PaymentResponse()
        response.sessionId = sessionId
        response.reference = payment.clientReference
        
        
        switch (payment.status) {
            case PaymentStatus.IN_TRANSACTION:
                if (payment.paymentTransactions.empty) {
                    response.status = UNDEFINED
                } else if (payment.paymentTransactions.find { !it.isFinalised }) {
                    response.status = IPaymentGatewayService.UNKNOW_RESULT_PAYMENT_IN == payment.statusNotes ? UNDEFINED : IN_PROGRESS
                }
                break
            case PaymentStatus.SUCCESS:
                response.status = SUCCESSFUL
                break
            case PaymentStatus.FAILED:
            case PaymentStatus.FAILED_CARD_DECLINED:
                response.status = FAIL
                break
            default:
                logger.error("Unexpected payment status, paymentId: ${payment.id}")
                throw new IllegalStateException('Unexpected payment status')
        }

        response
    }

    
}
