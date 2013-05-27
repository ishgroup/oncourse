package ish.oncourse.util.payment;

import ish.common.types.CreditCardType;
import org.apache.commons.lang.StringUtils;
import org.junit.Test;

import static org.junit.Assert.*;

/**
 * User: vdavidovich
 * Date: 27.05.13
 * Time: 15:35
 */
public class CreditCardParserTest {

	private static final String VISA_DEFAULT_CARD_NUMBER = "4XXX";
	private static final String VISA_POTENTIAL_CARD_NUMBER = "4";
	private static final String MASTERCARD_POTENTIAL_CARD_NUMBER = "5";
	private static final String MASTERCARD_DEFAULT_CARD_NUMBER = "50XXX";
	private static final String AMEX_DEFAULT_CARD_NUMBER_1 = "34XXX";
	private static final String AMEX_DEFAULT_CARD_NUMBER_2 = "37XXX";
	private static final String AMEX_POTENTIAL_CARD_NUMBER_1 = "34";
	private static final String AMEX_POTENTIAL_CARD_NUMBER_2 = "37";

	@Test
	public void test() {
		CreditCardParser cardParser = new CreditCardParser();
		//check VisaMatcher
		assertTrue("Credit cards start with 4 should be detected as visa",
			new VisaMatcher().matches(VISA_DEFAULT_CARD_NUMBER));
		assertTrue("Credit cards start with 4 should be detected as visa",
			new VisaMatcher().matches(VISA_POTENTIAL_CARD_NUMBER));
		assertFalse("Null credit card number should not match the visa credit card type",
			new VisaMatcher().matches(null));
		assertFalse("Empty credit card number should not match the visa credit card type",
			new VisaMatcher().matches(StringUtils.EMPTY));
		assertFalse("Credit cards starts not with 4 should not be detected as visa",
			new VisaMatcher().matches(MASTERCARD_POTENTIAL_CARD_NUMBER));
		assertEquals("Credit cards start with 4 should be detected as visa", CreditCardType.VISA,
			cardParser.parser(VISA_DEFAULT_CARD_NUMBER));
		//check AmexMatcher
		assertTrue("Credit cards start with 34 should be detected as amex", new AmexMatcher().matches(AMEX_DEFAULT_CARD_NUMBER_1));
		assertTrue("Credit cards start with 37 should be detected as amex", new AmexMatcher().matches(AMEX_DEFAULT_CARD_NUMBER_2));
		assertTrue("Credit cards start with 34 should be detected as amex", new AmexMatcher().matches(AMEX_POTENTIAL_CARD_NUMBER_1));
		assertTrue("Credit cards start with 37 should be detected as amex", new AmexMatcher().matches(AMEX_POTENTIAL_CARD_NUMBER_2));
		assertFalse("Null credit card number should not match the amex credit card type",
			new AmexMatcher().matches(null));
		assertFalse("Empty credit card number should not match the amex credit card type",
			new AmexMatcher().matches(StringUtils.EMPTY));
		assertFalse("Credit cards starts not with 34 or 37 should not be detected as amex",
			new AmexMatcher().matches(MASTERCARD_DEFAULT_CARD_NUMBER));
		assertFalse("Credit cards with the length less then 2 characters should not be detected as amex",
			new AmexMatcher().matches(MASTERCARD_POTENTIAL_CARD_NUMBER));
		assertEquals("Credit cards start with 34 should be detected as amex", CreditCardType.AMEX,
			cardParser.parser(AMEX_DEFAULT_CARD_NUMBER_1));
		assertEquals("Credit cards start with 37 should be detected as amex", CreditCardType.AMEX,
			cardParser.parser(AMEX_DEFAULT_CARD_NUMBER_2));
		//check MastercardMatcher
		assertTrue("Null credit card number should match the mastercard credit card type as default",
			new MastercardMatcher().matches(null));
		assertTrue("Empty credit card number should match the mastercard credit card type as default",
			new MastercardMatcher().matches(StringUtils.EMPTY));
		assertTrue("Credit cards starts not with 4 with length < 2 should match the mastercard credit card type as default",
			new MastercardMatcher().matches(MASTERCARD_POTENTIAL_CARD_NUMBER));
		assertFalse("Credit card matched to visa can't also match the mastercard",
			new MastercardMatcher().matches(VISA_DEFAULT_CARD_NUMBER));
		assertFalse("Credit card matched to amex can't also match the mastercard",
			new MastercardMatcher().matches(AMEX_DEFAULT_CARD_NUMBER_1));
		assertEquals("Credit cards not macthes to visa or amex should be detected as mastercard",
			CreditCardType.MASTERCARD, cardParser.parser(MASTERCARD_POTENTIAL_CARD_NUMBER));
		assertEquals("Credit cards not macthes to visa or amex should be detected as mastercard",
			CreditCardType.MASTERCARD, cardParser.parser(MASTERCARD_DEFAULT_CARD_NUMBER));
		assertNotEquals("Credit cards macthes to amex should not be detected as mastercard",
			CreditCardType.MASTERCARD, cardParser.parser(AMEX_DEFAULT_CARD_NUMBER_2));
		assertNotEquals("Credit cards macthes to amex should not be detected as mastercard",
			CreditCardType.MASTERCARD, cardParser.parser(AMEX_DEFAULT_CARD_NUMBER_1));
		assertNotEquals("Credit cards macthes to visa should not be detected as mastercard",
			CreditCardType.MASTERCARD, cardParser.parser(VISA_DEFAULT_CARD_NUMBER));
	}
}
