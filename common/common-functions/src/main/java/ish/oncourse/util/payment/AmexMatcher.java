package ish.oncourse.util.payment;

import ish.common.types.CreditCardType;
import org.apache.commons.lang.StringUtils;

/**
 * User: vdavidovich
 * Date: 27.05.13
 * Time: 15:27
 */
public class AmexMatcher implements CreditCardMatcher {
	static final String PREFIX = "34,37,";

	@Override
	public boolean matches(String cardnumber) {
		return StringUtils.trimToNull(cardnumber) != null && cardnumber.length()>=2 && PREFIX.contains((cardnumber.substring(0, 2) + ","));
	}

	@Override
	public CreditCardType getType() {
		return CreditCardType.AMEX;
	}
}
