package ish.oncourse.services.paymentexpress

import groovy.transform.CompileStatic
import ish.oncourse.model.PaymentOut
import ish.oncourse.model.PaymentOutTransaction
import ish.oncourse.model.PaymentTransaction
import ish.oncourse.services.payment.PaymentRequest
import ish.oncourse.util.payment.PaymentInModel
import org.apache.cayenne.ObjectContext

@CompileStatic
class NewPaymentExpressGatewayService implements INewPaymentGatewayService {

    private final ObjectContext nonReplicatingContext
    private boolean abandonOnFail = false

    NewPaymentExpressGatewayService(ObjectContext nonReplicatingContext) {
        this(nonReplicatingContext, false)
    }

    NewPaymentExpressGatewayService(ObjectContext nonReplicatingContext, boolean abandonOnFail) {
        this.nonReplicatingContext = nonReplicatingContext
        this.abandonOnFail = abandonOnFail
    }

    def submit(PaymentInModel model, String billingId) {
        DPSRequest request = DPSRequestBuilder.valueOf(model.paymentIn, billingId).build()
        submit(model, request)
    }

    def submit(PaymentInModel model, PaymentRequest paymentRequest) {
        DPSRequest request = DPSRequestBuilder.valueOf(model.paymentIn, paymentRequest).build()
        submit(model, request)
    }

    def submit(PaymentInModel model) {
        DPSRequest request = DPSRequestBuilder.valueOf(model.paymentIn, (String) null).build()
        submit(model, request)
    }

    def submit(PaymentInModel model, DPSRequest request) {

        ObjectContext transactionContext = nonReplicatingContext
        PaymentTransaction currentTransaction = transactionContext.newObject(PaymentTransaction.class)
        currentTransaction.setPayment(transactionContext.localObject(model.paymentIn))
        transactionContext.commitChanges()

        DPSResponse response = createGatewayHelper().submitRequest(request)

        ProcessDPSResponse.valueOf(model, response, abandonOnFail).process()
        ProcessPaymentTransaction.valueOf(currentTransaction, response).process()
    }

    def submit(PaymentOut paymentOut) {

        ObjectContext transactionContext = nonReplicatingContext
        PaymentOutTransaction currentTransaction = transactionContext.newObject(PaymentOutTransaction.class)
        currentTransaction.setCreated(new Date())
        currentTransaction.setModified(new Date())
        currentTransaction.setPaymentOut(transactionContext.localObject(paymentOut))

        DPSRequest request = DPSRequestBuilder.valueOf(paymentOut).build()
        DPSResponse response = createGatewayHelper().submitRequest(request)

        ProcessDPSResponse.valueOf(paymentOut, response).process()
        ProcessPaymentTransaction.valueOf(currentTransaction, response).process()

    }

    protected GatewayHelper createGatewayHelper() {
        return new GatewayHelper()
    }

}
