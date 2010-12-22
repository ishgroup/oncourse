package ish.oncourse.model;

import ish.common.util.ExternalValidation;
import ish.oncourse.model.auto._PaymentIn;

import java.math.BigDecimal;

public class PaymentIn extends _PaymentIn {

	public Long getId() {
		return (getObjectId() != null && !getObjectId().isTemporary()) ? (Long) getObjectId()
				.getIdSnapshot().get(ID_PK_COLUMN) : null;
	}

	@Deprecated
	// FIXME No longer used as payment doesn't store tax - replaced by "amount"
	public BigDecimal totalIncGst() {
		return getAmount();
	}

	public String validateCCNumber() {
		if (getCreditCardNumber() == null || getCreditCardNumber().equals("")) {
			return "The credit card number cannot be blank.";
		}

		if (!ExternalValidation.validateCreditCardNumber(getCreditCardNumber())
				|| (getCreditCardType() != null && !ExternalValidation
						.validateCreditCardNumber(getCreditCardNumber(),
								getCreditCardType()))) {

			return "Invalid credit card number.";
		}

		return null;
	}

	@Deprecated
	// FIXME: Isn't this the "amount"?
	public BigDecimal getTotalAmount() {
		return getAmount();
	}
	
}
