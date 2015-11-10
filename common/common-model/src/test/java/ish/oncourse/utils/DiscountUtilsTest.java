package ish.oncourse.utils;

import ish.common.types.DiscountType;
import ish.math.Money;
import ish.math.MoneyRounding;
import ish.oncourse.model.Discount;
import ish.util.DiscountUtils;
import org.junit.BeforeClass;
import org.junit.Test;

import java.math.BigDecimal;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;

import static org.junit.Assert.*;

/**
 * Test for the {@link WebDiscountUtils}.
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
	 * The discount with DiscountType = percent.
	 */
	private static Discount percentDiscount;

	/**
	 * The discount with DiscountType = dollar.
	 */
	private static Discount dollarDiscount;

	/**
	 * The discount with DiscountType = feeOverride.
	 */
	private static Discount feeOverrideDiscount;

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

		dollarDiscount = new Discount();
		dollarDiscount.setDiscountRate(null);
		dollarDiscount.setDiscountAmount(new Money(BigDecimal.TEN));
		dollarDiscount.setDiscountType(DiscountType.DOLLAR);

		percentDiscount = new Discount();
		percentDiscount.setDiscountRate(new BigDecimal(0.2));
		percentDiscount.setDiscountAmount(null);
		percentDiscount.setDiscountType(DiscountType.PERCENT);

		feeOverrideDiscount = new Discount();
		feeOverrideDiscount.setDiscountRate(null);
		feeOverrideDiscount.setDiscountAmount(new Money(BigDecimal.TEN));
		feeOverrideDiscount.setDiscountType(DiscountType.FEE_OVERRIDE);
	}

	/**
	 * Test for {@link DiscountUtils#discountValue}. Emulates
	 * the situation when the discount with amount is applied to
	 * price, then to zero price, then to price
	 * that is less than discountAmount.
	 */
	@Test
	public void discountValueSmokeTest() {
		Money discountValue = DiscountUtils.discountValue(Arrays.asList(combDiscountWithAmount), PRICE, new BigDecimal(0));
		assertEquals(new Money("10"), discountValue);

		discountValue =  DiscountUtils.discountValue(Arrays.asList(combDiscountWithAmount), Money.ZERO, new BigDecimal(0));
		assertEquals(Money.ZERO, discountValue);

		discountValue = DiscountUtils.discountValue(Arrays.asList(combDiscountWithAmount), new Money("9"), new BigDecimal(0));
		assertEquals(new Money("9"), discountValue);

		discountValue = DiscountUtils.discountValue(Arrays.asList(dollarDiscount), PRICE, new BigDecimal(0));
		assertEquals(new Money("10"), discountValue);

		discountValue = DiscountUtils.discountValue(Arrays.asList(percentDiscount), PRICE, new BigDecimal(0));
		assertEquals(new Money("20"), discountValue);

		discountValue =  DiscountUtils.discountValue(Arrays.asList(feeOverrideDiscount), PRICE, new BigDecimal(0));
		assertEquals(new Money("90"), discountValue);

		// discount with DiscountTypes (dollar, percent and feeOverride) with
		// zero price

		discountValue = DiscountUtils.discountValue(Arrays.asList(dollarDiscount), Money.ZERO, new BigDecimal(0));
		assertEquals(Money.ZERO, discountValue);

		discountValue =DiscountUtils.discountValue(Arrays.asList(percentDiscount), Money.ZERO, new BigDecimal(0));
		assertEquals(Money.ZERO, discountValue);

		discountValue = DiscountUtils.discountValue(Arrays.asList(feeOverrideDiscount), Money.ZERO, new BigDecimal(0));
		assertEquals(Money.ZERO, discountValue);
	}

	/**
	 * Test for {@link DiscountUtils#discountValue}. Tests the
	 * behavior when there are different amount restrictions(min,max).
	 */
	@Test
	public void discountValueRateWithRestrictionsTest() {
		// Zero restrictions
		Money discountValue = DiscountUtils.discountValue(Arrays.asList(singleDiscountWithRate), PRICE, new BigDecimal(0));
		assertEquals(new Money("20"), discountValue);
		// Max restricted
		discountValue = DiscountUtils.discountValue(Arrays.asList(combDiscountWithRateMax), PRICE, new BigDecimal(0));
		assertTrue(PRICE.multiply(combDiscountWithRateMax.getDiscountRate()).compareTo(combDiscountWithRateMax.getMaximumDiscount()) > 0);
		assertEquals(combDiscountWithRateMax.getMaximumDiscount(), discountValue);
		// Min restricted
		discountValue = DiscountUtils.discountValue(Arrays.asList(singleDiscountWithRateMin), PRICE, new BigDecimal(0));
		assertTrue(PRICE.multiply(singleDiscountWithRateMin.getDiscountRate()).compareTo(singleDiscountWithRateMin.getMinimumDiscount()) < 0);
		assertEquals(singleDiscountWithRateMin.getMinimumDiscount(), discountValue);
	}

	/**
	 * Test for {@link DiscountUtils#discountValue}.
	 * Emulates the situations when there is a filled list of discounts and
	 * positive price, when there is a filled list of discounts and the price
	 * that is less that discount value, when there is a null list of discounts
	 * and positive price, when there is an empty list of discounts and positive
	 * price and when there is a filled list of discounts and a zero price.
	 */
	@Test
	public void discountValueForListTest() {
		List<Discount> discounts = Arrays.asList(combDiscountWithAmount, singleDiscountWithRate, combDiscountWithRateMax,
				singleDiscountWithRateMin);
		Money discountValueForList = DiscountUtils.discountValue(discounts, PRICE, new BigDecimal(0));
		assertEquals(new Money("55"), discountValueForList);
		discountValueForList =DiscountUtils.discountValue(Arrays.asList(combDiscountWithAmount, singleDiscountWithRate), new Money("10"), new BigDecimal(0));
		assertEquals(new Money("10"), discountValueForList);
		discountValueForList = DiscountUtils.discountValue(null, PRICE, new BigDecimal(0));
		assertEquals(Money.ZERO, discountValueForList);
		discountValueForList = DiscountUtils.discountValue(Collections.EMPTY_LIST, PRICE, new BigDecimal(0));
		assertEquals(Money.ZERO, discountValueForList);
		discountValueForList = DiscountUtils.discountValue(discounts, Money.ZERO, new BigDecimal(0));
		assertEquals(Money.ZERO, discountValueForList);
	}

	/**
	 * Test for {@link WebDiscountUtils#chooseDiscountForApply(java.util.List, ish.math.Money, java.math.BigDecimal)}. Emulates
	 * the situations when the discounts list parameter is null and empty.
	 */
	@Test
	public void chooseBestDiscountsVariantEmptyTest() {
		Discount chosenDiscount = WebDiscountUtils.chooseDiscountForApply(null, PRICE, new BigDecimal(0));
		assertNull(chosenDiscount);
		chosenDiscount = WebDiscountUtils.chooseDiscountForApply(Collections.EMPTY_LIST, PRICE, new BigDecimal(0));
		assertNull(chosenDiscount);
	}

	/**
	 * Test for {@link WebDiscountUtils#chooseDiscountForApply(java.util.List, ish.math.Money, java.math.BigDecimal)}. Emulates
	 * the situation when the single-discount result is the best.
	 */
	@Test
	public void chooseBestDiscountsVariantSingleResultTest() {
		Discount chosenDiscount = WebDiscountUtils.chooseDiscountForApply(
				Arrays.asList(singleDiscountWithRate, combDiscountWithRateMax, singleDiscountWithRateMin), PRICE, new BigDecimal(0));
		assertNotNull(chosenDiscount);
		assertEquals(singleDiscountWithRate, chosenDiscount);
	}

	/**
	 * Test for {@link WebDiscountUtils#chooseDiscountForApply(java.util.List, ish.math.Money, java.math.BigDecimal)} . Check
	 * situation when fee override is greater than course class price.
	 * Currently handle this situation like as negative discount
	 * That discount always beats a normal discount. It also beats having no discount at all 
	 */
	@Test
	public void chooseBestDiscountVariantWithFeeOverride() {

		Discount feeOverride = new Discount();
		feeOverride.setDiscountRate(null);
		feeOverride.setDiscountAmount(new Money(new BigDecimal(200)));
		feeOverride.setDiscountType(DiscountType.FEE_OVERRIDE);

		Discount chosenDiscount = WebDiscountUtils.chooseDiscountForApply(Arrays.asList(feeOverride), PRICE, new BigDecimal(0));

		assertNotNull(chosenDiscount);
		assertEquals(feeOverride, chosenDiscount);
		
		chosenDiscount = WebDiscountUtils.chooseDiscountForApply(
				Arrays.asList(singleDiscountWithRate, feeOverride, singleDiscountWithRateMin), PRICE, new BigDecimal(0));

		assertNotNull(chosenDiscount);
		assertEquals(feeOverride, chosenDiscount);

		feeOverride.setDiscountAmount(new Money(new BigDecimal(60)));

		chosenDiscount = WebDiscountUtils.chooseDiscountForApply(
				Arrays.asList(singleDiscountWithRate, combDiscountWithRateMax, singleDiscountWithRateMin, feeOverride), PRICE, new BigDecimal(0));

		assertNotNull(chosenDiscount);
		assertEquals(feeOverride, chosenDiscount);
	}
	
	@Test
	public void testDiscountRounding() {
		Discount discount = new Discount();
		discount.setDiscountType(DiscountType.PERCENT);
		discount.setDiscountRate(new BigDecimal("0.11"));
		
		discount.setRoundingMode(MoneyRounding.ROUNDING_NONE);
		
		assertEquals(new Money("856.63"), DiscountUtils.getDiscountedFee(Arrays.asList(discount), new Money("875.00"), new BigDecimal(0.1)));
		assertEquals(new Money("96.25"), DiscountUtils.discountValue(Arrays.asList(discount), new Money("875.00"), new BigDecimal(0.1)));
		
		discount.setRoundingMode(MoneyRounding.ROUNDING_10C);
		
		assertEquals(new Money("856.60"), DiscountUtils.getDiscountedFee(Arrays.asList(discount), new Money("875.00"), new BigDecimal(0.1)));
		assertEquals(new Money("96.27"), DiscountUtils.discountValue(Arrays.asList(discount), new Money("875.00"), new BigDecimal(0.1)));
		
		discount.setRoundingMode(MoneyRounding.ROUNDING_50C);
		
		assertEquals(new Money("856.50"),DiscountUtils.getDiscountedFee(Arrays.asList(discount), new Money("875.00"), new BigDecimal(0.1)));
		assertEquals(new Money("96.36"), DiscountUtils.discountValue(Arrays.asList(discount), new Money("875.00"), new BigDecimal(0.1)));
		
		discount.setRoundingMode(MoneyRounding.ROUNDING_1D);
		
		assertEquals(new Money("857.00"), DiscountUtils.getDiscountedFee(Arrays.asList(discount), new Money("875.00"), new BigDecimal(0.1)));
		assertEquals(new Money("95.91"), DiscountUtils.discountValue(Arrays.asList(discount), new Money("875.00"), new BigDecimal(0.1)));
	}
}
