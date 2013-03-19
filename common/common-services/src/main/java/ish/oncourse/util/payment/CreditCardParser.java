package ish.oncourse.util.payment;

import ish.common.types.CreditCardType;

import java.util.ArrayList;
import java.util.List;

public class CreditCardParser {

	private List<Matcher> matchers = new ArrayList<Matcher>();

	public CreditCardParser() {
		matchers.add(new VisaMatcher());
		matchers.add(new AmexMatcher());
		matchers.add(new MastercardMatcher());
	}


	public CreditCardType parser(String card) {
		for (Matcher matcher : matchers) {
			if (matcher.matches(card))
				return matcher.getType();
		}
		return null;
	}


	static interface Matcher {
		public boolean matches(String card);

		public CreditCardType getType();
	}

	private static class VisaMatcher implements Matcher {
		private static final String PREFIX = "4";

		public boolean matches(String card) {
			return card.substring(0, 1).equals(PREFIX);
		}

		public CreditCardType getType() {
			return CreditCardType.VISA;
		}
	}

	private static class AmexMatcher implements Matcher {
		private static final String PREFIX = "34,37,";

		public boolean matches(String card) {
			String prefix2 = card.substring(0, 2) + ",";
			return PREFIX.contains(prefix2);
		}

		public CreditCardType getType() {
			return CreditCardType.AMEX;
		}

	}

	private static class MastercardMatcher implements Matcher {
		public boolean matches(String card) {
			String prefix2 = card.substring(0, 2) + ",";
			return !(AmexMatcher.PREFIX.contains(prefix2) || VisaMatcher.PREFIX.contains(prefix2));
		}

		public CreditCardType getType() {
			return CreditCardType.MASTERCARD;
		}

	}
}
