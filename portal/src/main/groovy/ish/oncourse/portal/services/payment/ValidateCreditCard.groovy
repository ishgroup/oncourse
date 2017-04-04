package ish.oncourse.portal.services.payment

import ish.common.types.CreditCardType
import ish.oncourse.util.payment.CreditCardParser
import org.apache.commons.lang3.StringUtils
import org.apache.commons.lang3.time.DateUtils
import org.apache.logging.log4j.LogManager
import org.apache.logging.log4j.Logger

import static ish.common.util.ExternalValidation.validateCreditCardNumber

/**
 * User: akoiro
 * Date: 3/07/2016
 */
class ValidateCreditCard {
    private static final Logger logger = LogManager.getLogger();

    Request.Card card
    ValidationResult result

    void validate() {
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
        }
    }

    void validateDate() {
        if (StringUtils.isBlank(card.date)) {
            result.warning = WarningMessage.invalidCardDate
            return
        }

        try {
            Date today = new Date()
            use(DateUtils) {
                Date date =  card.date.parseDate("MM/yy").addMonths(1).truncate(Calendar.DAY_OF_MONTH)
                if (date.before(today)) {
                    result.warning = WarningMessage.invalidCardDate
                }
            }
        } catch (e) {
            logger.debug(e)
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

    boolean validateCvv() {

        if (StringUtils.isBlank(card.cvv)) {
            result.warning = WarningMessage.invalidCardCvv
            return
        }

        if (!card.cvv.matches("\\d{1,4}")) {
            result.warning = WarningMessage.invalidCardCvv
        }
    }
}
