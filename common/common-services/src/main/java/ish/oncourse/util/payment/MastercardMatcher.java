package ish.oncourse.util.payment;

import ish.common.types.CreditCardType;

/**
 * User: vdavidovich
 * Date: 27.05.13
 * Time: 15:30
 */
public class MastercardMatcher implements CreditCardMatcher {

	@Override
	public boolean matches(String cardnumber) {
		return !new AmexMatcher().matches(cardnumber) && !new VisaMatcher().matches(cardnumber);
	}

	@Override
	public CreditCardType getType() {
		return CreditCardType.MASTERCARD;
	}
}
