package ish.oncourse.services.paymentexpress

import com.paymentexpress.stubs.TransactionDetails
import groovy.transform.CompileStatic

@CompileStatic
class DPSRequest {
    TransactionDetails transactionDetails
    String paymentGatewayAccount
    String paymentGatewayPass
}
