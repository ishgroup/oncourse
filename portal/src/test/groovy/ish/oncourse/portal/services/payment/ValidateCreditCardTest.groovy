package ish.oncourse.portal.services.payment

import org.junit.Assert
import org.junit.Test

class ValidateCreditCardTest {

    @Test
    void test() {
        Request.Card card = new Request.Card()
        ValidationResult vr = new ValidationResult()


        card.date = "12/20"
        ValidateCreditCard validateCreditCard = new ValidateCreditCard(card: card, result: vr)
        validateCreditCard.validateDate()
        Assert.assertTrue(validateCreditCard.result.valid())
    }

}
