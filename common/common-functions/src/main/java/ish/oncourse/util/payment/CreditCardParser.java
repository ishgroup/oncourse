package ish.oncourse.util.payment;

import ish.common.types.CreditCardType;

import java.util.ArrayList;
import java.util.List;

public class CreditCardParser {

	private List<CreditCardMatcher> matchers = new ArrayList<>();

	public CreditCardParser() {
		matchers.add(new VisaMatcher());
		matchers.add(new AmexMatcher());
		matchers.add(new MastercardMatcher());
	}


	public CreditCardType parser(String card) {
		for (CreditCardMatcher matcher : matchers) {
			if (matcher.matches(card))
				return matcher.getType();
		}
		return null;
	}
}
