package ish.oncourse.util.payment;

import ish.common.types.CreditCardType;

/**
 * Interface for recognize credit cards using the credit card numbers.
 * User: vdavidovich
 * Date: 27.05.13
 * Time: 15:13
 */
public interface CreditCardMatcher {

	public boolean matches(String card);

	public CreditCardType getType();
}
