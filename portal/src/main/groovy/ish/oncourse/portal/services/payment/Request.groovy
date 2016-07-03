package ish.oncourse.portal.services.payment

import groovy.transform.AutoClone

/**
 * User: akoiro
 * Date: 24/06/2016
 */
@AutoClone
class Request {
    def Long paymentInId
    def Long invoiceId
    def Action action
    def Card card = new Card()

    @AutoClone
    public static class Card {
        def String name;
        def String number;
        def String cvv;
        def String date;
        def Double amount
    }
}
