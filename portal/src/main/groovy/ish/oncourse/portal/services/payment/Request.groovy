package ish.oncourse.portal.services.payment

import groovy.transform.AutoClone

/**
 * User: akoiro
 * Date: 24/06/2016
 */
@AutoClone
class Request {
    Long paymentInId
    Long invoiceId
    Action action
    Card card = new Card()

    @AutoClone
    static class Card {
        String name
        String number
        String cvv
        String date
        Double amount
    }
}
