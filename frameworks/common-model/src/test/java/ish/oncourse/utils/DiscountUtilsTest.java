package ish.oncourse.utils;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;
import ish.math.Money;
import ish.oncourse.model.Discount;

import java.math.BigDecimal;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;

import org.junit.BeforeClass;
import org.junit.Test;

/**
 * Test for the {@link DiscountUtils}.
 * 
 * @author ksenia
 * 
 */
public class DiscountUtilsTest {

	/**
	 * Combinable discount with amount=10.
	 */
	private static Discount combDiscountWithAmount;
	/**
	 * Not combinable discount with the rate 20% and zero restrictions.
	 */
	private static Discount singleDiscountWithRate;
	/**
	 * Combinable discount with the rate 20% and maximum 10.
	 */
	private static Discount combDiscountWithRateMax;
	/**
	 * Not combinable discount with the rate 10% and minimum 15.
	 */
	private static Discount singleDiscountWithRateMin;

	private static Money PRICE = new Money("100");

	/**
	 * Initializes discounts entities.
	 */
	@BeforeClass
	public static void init() {
		combDiscountWithAmount = new Discount();
		combDiscountWithAmount.setDiscountAmount(new Money(BigDecimal.TEN));
		combDiscountWithAmount.setCombinationType(true);

		singleDiscountWithRate = new Discount();
		singleDiscountWithRate.setDiscountRate(new BigDecimal(0.2));
		singleDiscountWithRate.setMaximumDiscount(Money.ZERO);
		singleDiscountWithRate.setMinimumDiscount(Money.ZERO);
		singleDiscountWithRate.setCombinationType(false);

		combDiscountWithRateMax = new Discount();
		combDiscountWithRateMax.setDiscountRate(new BigDecimal(0.2));
		combDiscountWithRateMax.setMaximumDiscount(new Money(BigDecimal.TEN));
		combDiscountWithRateMax.setCombinationType(true);

		singleDiscountWithRateMin = new Discount();
		singleDiscountWithRateMin.setDiscountRate(new BigDecimal(0.1));
		singleDiscountWithRateMin.setMinimumDiscount(new Money("15"));
		singleDiscountWithRateMin.setCombinationType(false);
	}

	/**
	 * Test for {@link DiscountUtils#discountValue(Discount, Money)}. Emulates
	 * the situation when the discount with amount is applied to
	 * {@value DiscountUtilsTest#PRICE} price, then to zero price, then to price
	 * that is less than discountAmount.
	 */
	@Test
	public void discountValueSmokeTest() {
		Money discountValue = DiscountUtils.discountValue(combDiscountWithAmount, PRICE);
		assertEquals(new Money("10"), discountValue);

		discountValue = DiscountUtils.discountValue(combDiscountWithAmount, Money.ZERO);
		assertEquals(Money.ZERO, discountValue);

		discountValue = DiscountUtils.discountValue(combDiscountWithAmount, new Money("9"));
		assertEquals(new Money("9"), discountValue);
	}

	/**
	 * Test for {@link DiscountUtils#discountValue(Discount, Money)}. Tests the
	 * behavior when there are different amount restrictions(min,max).
	 */
	@Test
	public void discountValueRateWithRestrictionsTest() {
		// Zero restrictions
		Money discountValue = DiscountUtils.discountValue(singleDiscountWithRate, PRICE);
		assertEquals(new Money("20"), discountValue);
		// Max restricted
		discountValue = DiscountUtils.discountValue(combDiscountWithRateMax, PRICE);
		assertTrue(PRICE.multiply(combDiscountWithRateMax.getDiscountRate()).compareTo(
				combDiscountWithRateMax.getMaximumDiscount()) > 0);
		assertEquals(combDiscountWithRateMax.getMaximumDiscount(), discountValue);
		// Min restricted
		discountValue = DiscountUtils.discountValue(singleDiscountWithRateMin, PRICE);
		assertTrue(PRICE.multiply(singleDiscountWithRateMin.getDiscountRate()).compareTo(
				singleDiscountWithRateMin.getMinimumDiscount()) < 0);
		assertEquals(singleDiscountWithRateMin.getMinimumDiscount(), discountValue);
	}

	/**
	 * Test for {@link DiscountUtils#discountValueForList(List, Money)}.
	 * Emulates the situations when there is a filled list of discounts and
	 * positive price, when there is a filled list of discounts and the price
	 * that is less that discount value, when there is a null list of discounts
	 * and positive price, when there is an empty list of discounts and positive
	 * price and when there is a filled list of discounts and a zero price.
	 */
	@Test
	public void discountValueForListTest() {
		List<Discount> discounts = Arrays.asList(combDiscountWithAmount, singleDiscountWithRate,
				combDiscountWithRateMax, singleDiscountWithRateMin);
		Money discountValueForList = DiscountUtils.discountValueForList(discounts, PRICE);
		assertEquals(new Money("55"), discountValueForList);
		discountValueForList = DiscountUtils.discountValueForList(
				Arrays.asList(combDiscountWithAmount, singleDiscountWithRate), new Money("10"));
		assertEquals(new Money("10"), discountValueForList);
		discountValueForList = DiscountUtils.discountValueForList(null, PRICE);
		assertEquals(Money.ZERO, discountValueForList);
		discountValueForList = DiscountUtils.discountValueForList(Collections.EMPTY_LIST, PRICE);
		assertEquals(Money.ZERO, discountValueForList);
		discountValueForList = DiscountUtils.discountValueForList(discounts, Money.ZERO);
		assertEquals(Money.ZERO, discountValueForList);
	}

	/**
	 * Test for {@link DiscountUtils#chooseBestDiscountsVariant(List)}. Emulates
	 * the situations when the discounts list parameter is null and empty.
	 */
	@Test
	public void chooseBestDiscountsVariantEmptyTest() {
		List<Discount> chosenDiscounts = DiscountUtils.chooseBestDiscountsVariant(null);
		assertTrue(chosenDiscounts.isEmpty());
		chosenDiscounts = DiscountUtils.chooseBestDiscountsVariant(Collections.EMPTY_LIST);
		assertTrue(chosenDiscounts.isEmpty());
	}

	/**
	 * Test for {@link DiscountUtils#chooseBestDiscountsVariant(List)}. Emulates
	 * the situation when the combined result is the best.
	 */
	@Test
	public void chooseBestDiscountsVariantCombResultTest() {
		List<Discount> chosenDiscounts = DiscountUtils.chooseBestDiscountsVariant(Arrays.asList(
				combDiscountWithAmount, singleDiscountWithRate, combDiscountWithRateMax,
				singleDiscountWithRateMin));
		assertFalse(chosenDiscounts.isEmpty());
		assertTrue(chosenDiscounts.size() == 2);
		assertEquals(combDiscountWithAmount, chosenDiscounts.get(0));
		assertEquals(combDiscountWithRateMax, chosenDiscounts.get(1));
	}

	/**
	 * Test for {@link DiscountUtils#chooseBestDiscountsVariant(List)}. Emulates
	 * the situation when the single-discount result is the best.
	 */
	@Test
	public void chooseBestDiscountsVariantSingleResultTest() {
		List<Discount> chosenDiscounts = DiscountUtils.chooseBestDiscountsVariant(Arrays.asList(
				singleDiscountWithRate, combDiscountWithRateMax, singleDiscountWithRateMin));
		assertFalse(chosenDiscounts.isEmpty());
		assertTrue(chosenDiscounts.size() == 1);
		assertEquals(singleDiscountWithRate, chosenDiscounts.get(0));
	}
}
