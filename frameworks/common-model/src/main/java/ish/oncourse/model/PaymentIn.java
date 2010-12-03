package ish.oncourse.model;

import ish.common.util.ExternalValidation;
import ish.oncourse.model.auto._PaymentIn;

import java.math.BigDecimal;
import java.math.BigInteger;

public class PaymentIn extends _PaymentIn {

	public BigDecimal totalIncGst() {
		BigDecimal totalExGst = getTotalExGst();
		BigDecimal totalGst = getTotalGst();
		BigDecimal total = new BigDecimal(BigInteger.ZERO);
		if (totalExGst != null) {
			total = total.add(totalExGst);
		}
		if (totalGst != null) {
			total = total.add(totalGst);
		}
		return total;
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

	public BigDecimal getTotalAmount() {
		return BigDecimal.ZERO;
	}
	
}
