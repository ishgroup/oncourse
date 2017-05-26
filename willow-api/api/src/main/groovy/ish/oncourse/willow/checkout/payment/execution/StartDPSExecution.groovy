package ish.oncourse.willow.checkout.payment.execution

import ish.oncourse.services.paymentexpress.NewPaymentExpressGatewayService
import ish.oncourse.util.payment.PaymentInModel
import ish.oncourse.willow.model.checkout.payment.PaymentRequest
import org.apache.cayenne.ObjectContext

import java.util.concurrent.Callable
import java.util.concurrent.ExecutorService
import java.util.concurrent.Executors
import java.util.concurrent.ScheduledThreadPoolExecutor


class StartDPSExecution {

    PaymentInModel model
    PaymentRequest paymentRequest
    ObjectContext nonReplicatedContext

    StartDPSExecution(PaymentInModel model, PaymentRequest paymentRequest, ObjectContext nonReplicatedContext) {
        this.model = model
        this.paymentRequest = paymentRequest
        this.nonReplicatedContext = nonReplicatedContext
    }
    
    
    void execute() {
        ExecutorService executor = Executors.newSingleThreadExecutor()
        executor.execute(new Runnable() {
            @Override
            void run() {
                new NewPaymentExpressGatewayService(nonReplicatedContext, true).submit(model, new ish.oncourse.services.payment.PaymentRequest().with { r ->
                    r.sessionId = paymentRequest.sessionId
                    r.name = paymentRequest.creditCardName
                    r.number = paymentRequest.creditCardNumber
                    r.cvv = paymentRequest.creditCardCvv
                    r.year = paymentRequest.expiryYear
                    r.month = paymentRequest.expiryMonth
                    r
                })
            }
        })
                
            
        
    }
    
}
