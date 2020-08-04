package ish.oncourse.willow.checkout.windcave

import groovy.transform.CompileStatic
import groovy.transform.TypeCheckingMode
import ish.math.Money

interface IPaymentService  {
    SessionAttributes createSession(String origin, Money amount, String merchantReference, Boolean storeCard)
    SessionAttributes completeTransaction(String transactionId, Money amount, String merchantReference)
    SessionAttributes checkStatus(String sessionId)
    Boolean isSkipAuth()
}