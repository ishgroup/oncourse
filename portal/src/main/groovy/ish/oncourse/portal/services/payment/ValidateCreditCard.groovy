package ish.oncourse.portal.services.payment

import ish.common.types.CreditCardType
import ish.oncourse.util.payment.CreditCardParser
import org.apache.commons.lang3.StringUtils

import static ish.common.util.ExternalValidation.validateCreditCardNumber

/**
 * User: akoiro
 * Date: 3/07/2016
 */
class ValidateCreditCard {
    def Request.Card card
    def ValidationResult result

    public void validate() {
        validateName()
        if (result.valid()) {
            validateNumber()
        }

        if (result.valid()) {
            validateCvv()
        }

        if (result.valid()) {
            validateDate()
        }
    }

    private void validateName() {
        if (StringUtils.isBlank(card.name)) {
            result.warning = WarningMessage.invalidCardName
            return
        }
    }

    private void validateDate() {
        if (StringUtils.isBlank(card.date)) {
            result.warning = WarningMessage.invalidCardDate
            return
        }
        String[] dateParts = card.date.split("/");
        if (dateParts.length != 2 || !dateParts[0].matches("\\d{1,2}") && !dateParts[0].matches("\\d{4}")) {
            result.warning = WarningMessage.invalidCardDate
            return
        }
        int ccExpiryMonth = Integer.parseInt(dateParts[0]) - 1;
        int ccExpiryYear = Integer.parseInt(dateParts[1]);
        Calendar today = Calendar.getInstance();
        Calendar cal = Calendar.getInstance();
        cal.set(Calendar.MONTH, ccExpiryMonth);
        cal.set(Calendar.YEAR, ccExpiryYear);

        if (cal.getTime().before(today.getTime())) {
            result.warning = WarningMessage.invalidCardDate
        }
    }

    private void validateNumber() {
        if (StringUtils.isBlank(card.number)) {
            result.warning = WarningMessage.invalidCardNumber
            return
        }

        CreditCardParser parser = new CreditCardParser();
        CreditCardType creditCardType = parser.parser(card.number);
        if (!validateCreditCardNumber(card.number)
                || (creditCardType != null && !validateCreditCardNumber(card.number, creditCardType))) {
            result.warning = WarningMessage.invalidCardNumber
        }
    }

    public boolean validateCvv() {

        if (StringUtils.isBlank(card.cvv)) {
            result.warning = WarningMessage.invalidCardCvv
            return
        }

        if (!card.cvv.matches("\\d{1,4}")) {
            result.warning = WarningMessage.invalidCardCvv
        }
    }
}
