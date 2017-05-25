package ish.oncourse.util.payment;

import ish.common.types.CreditCardType;
import org.apache.commons.lang.StringUtils;

/**
 * User: vdavidovich
 * Date: 27.05.13
 * Time: 15:21
 */
public class VisaMatcher implements CreditCardMatcher {
	static final String PREFIX = "4";

	@Override
	public boolean matches(String cardnumber) {
		return StringUtils.trimToNull(cardnumber) != null && cardnumber.length()>=1 && cardnumber.substring(0, 1).equals(PREFIX);
	}

	@Override
	public CreditCardType getType() {
		return CreditCardType.VISA;
	}
}
