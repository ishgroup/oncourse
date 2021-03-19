package ish.oncourse.utils;

import ish.common.types.DiscountType;
import ish.math.Money;
import ish.math.MoneyRounding;
import ish.oncourse.cayenne.DiscountCourseClassInterface;
import ish.oncourse.model.Discount;
import ish.oncourse.model.DiscountCourseClass;
import ish.oncourse.test.TestContext;
import ish.util.DiscountUtils;
import org.apache.cayenne.ObjectContext;
import org.junit.BeforeClass;
import org.junit.Test;

import java.math.BigDecimal;
import java.util.Arrays;

import static org.junit.Assert.*;

/**
 * Test for the {@link WebDiscountUtils}.
 *
 * @author ksenia
 */
public class DiscountUtilsTest {
	private final static TestContext testContext = new TestContext();


	/**
	 * Combinable discount with amount=10.
	 */
	private static DiscountCourseClass combDiscountWithAmount;
	/**
	 * Not combinable discount with the rate 20% and zero restrictions.
	 */
	private static DiscountCourseClass singleDiscountWithRate;
	/**
	 * Combinable discount with the rate 20% and maximum 10.
	 */
	private static DiscountCourseClass combDiscountWithRateMax;
	/**
	 * Not combinable discount with the rate 10% and minimum 15.
	 */
	private static DiscountCourseClass singleDiscountWithRateMin;

	private static Money PRICE = new Money("100");

	/**
	 * The discount with DiscountType = percent.
	 */
	private static DiscountCourseClass percentDiscount;

	/**
	 * The discount with DiscountType = dollar.
	 */
	private static DiscountCourseClass dollarDiscount;

	/**
	 * The discount with DiscountType = feeOverride.
	 */
	private static DiscountCourseClass feeOverrideDiscount;

	private static ObjectContext context;

	/**
	 * Initializes discounts entities.
	 */
	@BeforeClass
	public static void init() throws Exception {
		testContext.open();
		context = testContext.getServerRuntime().newContext();

		Discount discount = context.newObject(Discount.class);
		discount.setDiscountAmount(new Money(BigDecimal.TEN));
		discount.setDiscountType(DiscountType.DOLLAR);
		discount.setCombinationType(true);
		combDiscountWithAmount = context.newObject(DiscountCourseClass.class);
		combDiscountWithAmount.setDiscount(discount);
		discount.addToDiscountCourseClasses(combDiscountWithAmount);

		discount = context.newObject(Discount.class);
		discount.setDiscountRate(new BigDecimal(0.2));
		discount.setDiscountType(DiscountType.PERCENT);
		discount.setMaximumDiscount(Money.ZERO);
		discount.setMinimumDiscount(Money.ZERO);
		discount.setCombinationType(false);
		singleDiscountWithRate = context.newObject(DiscountCourseClass.class);
		singleDiscountWithRate.setDiscount(discount);
		discount.addToDiscountCourseClasses(singleDiscountWithRate);

		discount = context.newObject(Discount.class);
		discount.setDiscountRate(new BigDecimal(0.2));
		discount.setDiscountType(DiscountType.PERCENT);
		discount.setMaximumDiscount(new Money(BigDecimal.TEN));
		discount.setCombinationType(true);
		combDiscountWithRateMax = context.newObject(DiscountCourseClass.class);
		combDiscountWithRateMax.setDiscount(discount);
		discount.addToDiscountCourseClasses(combDiscountWithRateMax);

		discount = context.newObject(Discount.class);
		discount.setDiscountRate(new BigDecimal(0.1));
		discount.setDiscountType(DiscountType.PERCENT);
		discount.setMinimumDiscount(new Money("15"));
		discount.setCombinationType(false);
		singleDiscountWithRateMin = context.newObject(DiscountCourseClass.class);
		singleDiscountWithRateMin.setDiscount(discount);
		discount.addToDiscountCourseClasses(singleDiscountWithRateMin);

		discount = context.newObject(Discount.class);
		discount.setDiscountRate(null);
		discount.setDiscountAmount(new Money(BigDecimal.TEN));
		discount.setDiscountType(DiscountType.DOLLAR);
		dollarDiscount = context.newObject(DiscountCourseClass.class);
		dollarDiscount.setDiscount(discount);
		discount.addToDiscountCourseClasses(dollarDiscount);

		discount = context.newObject(Discount.class);
		discount.setDiscountRate(new BigDecimal(0.2));
		discount.setDiscountAmount(null);
		discount.setDiscountType(DiscountType.PERCENT);
		percentDiscount = context.newObject(DiscountCourseClass.class);
		percentDiscount.setDiscount(discount);
		discount.addToDiscountCourseClasses(percentDiscount);

		discount = context.newObject(Discount.class);
		discount.setDiscountRate(null);
		discount.setDiscountAmount(new Money(BigDecimal.TEN));
		discount.setDiscountType(DiscountType.FEE_OVERRIDE);
		feeOverrideDiscount = context.newObject(DiscountCourseClass.class);
		feeOverrideDiscount.setDiscount(discount);
		discount.addToDiscountCourseClasses(feeOverrideDiscount);
	}

	/**
	 * Test for {@link DiscountUtils#discountValue}. Emulates
	 * the situation when the discount with amount is applied to
	 * price, then to zero price, then to price
	 * that is less than discountAmount.
	 */
	@Test
	public void discountValueSmokeTest() {
		Money discountValue = DiscountUtils.discountValue(combDiscountWithAmount, PRICE, new BigDecimal(0));
		assertEquals(new Money("10"), discountValue);

		discountValue = DiscountUtils.discountValue(combDiscountWithAmount, Money.ZERO, new BigDecimal(0));
		assertEquals(Money.ZERO, discountValue);

		discountValue = DiscountUtils.discountValue(combDiscountWithAmount, new Money("9"), new BigDecimal(0));
		assertEquals(new Money("9"), discountValue);

		discountValue = DiscountUtils.discountValue(dollarDiscount, PRICE, new BigDecimal(0));
		assertEquals(new Money("10"), discountValue);

		discountValue = DiscountUtils.discountValue(percentDiscount, PRICE, new BigDecimal(0));
		assertEquals(new Money("20"), discountValue);

		discountValue = DiscountUtils.discountValue(feeOverrideDiscount, PRICE, new BigDecimal(0));
		assertEquals(new Money("90"), discountValue);

		// discount with DiscountTypes (dollar, percent and feeOverride) with
		// zero price

		discountValue = DiscountUtils.discountValue(dollarDiscount, Money.ZERO, new BigDecimal(0));
		assertEquals(Money.ZERO, discountValue);

		discountValue = DiscountUtils.discountValue(percentDiscount, Money.ZERO, new BigDecimal(0));
		assertEquals(Money.ZERO, discountValue);

		discountValue = DiscountUtils.discountValue(feeOverrideDiscount, Money.ZERO, new BigDecimal(0));
		assertEquals(Money.ZERO, discountValue);
	}

	/**
	 * Test for {@link DiscountUtils#discountValue}. Tests the
	 * behavior when there are different amount restrictions(min,max).
	 */
	@Test
	public void discountValueRateWithRestrictionsTest() {
		// Zero restrictions
		Money discountValue = DiscountUtils.discountValue(singleDiscountWithRate, PRICE, new BigDecimal(0));
		assertEquals(new Money("20"), discountValue);
		// Max restricted
		discountValue = DiscountUtils.discountValue(combDiscountWithRateMax, PRICE, new BigDecimal(0));
		assertTrue(PRICE.multiply(combDiscountWithRateMax.getDiscount().getDiscountRate()).compareTo(combDiscountWithRateMax.getDiscount().getMaximumDiscount()) > 0);
		assertEquals(combDiscountWithRateMax.getDiscount().getMaximumDiscount(), discountValue);
		// Min restricted
		discountValue = DiscountUtils.discountValue(singleDiscountWithRateMin, PRICE, new BigDecimal(0));
		assertTrue(PRICE.multiply(singleDiscountWithRateMin.getDiscount().getDiscountRate()).compareTo(singleDiscountWithRateMin.getDiscount().getMinimumDiscount()) < 0);
		assertEquals(singleDiscountWithRateMin.getDiscount().getMinimumDiscount(), discountValue);
	}

	@Test
	public void chooseBestDiscountsVariantEmptyTest() {
		DiscountCourseClassInterface chosenDiscount = DiscountUtils.chooseDiscountForApply(null, PRICE, new BigDecimal(0));
		assertNull(chosenDiscount);
		Money discountValueForList = DiscountUtils.discountValue(null, PRICE, new BigDecimal(0));
		assertEquals(Money.ZERO, discountValueForList);
	}

	/**
	 * Test for {@link DiscountUtils#chooseDiscountForApply(java.util.List, ish.math.Money, java.math.BigDecimal)}. Emulates
	 * the situation when the single-discount result is the best.
	 */
	@Test
	public void chooseBestDiscountsVariantSingleResultTest() {
		DiscountCourseClass chosenDiscount = (DiscountCourseClass) DiscountUtils.chooseDiscountForApply(
				Arrays.asList(singleDiscountWithRate, combDiscountWithRateMax, singleDiscountWithRateMin), PRICE, new BigDecimal(0));
		assertNotNull(chosenDiscount);
		assertEquals(singleDiscountWithRate, chosenDiscount);
	}

	/**
	 * Test for {@link DiscountUtils#chooseDiscountForApply(java.util.List, ish.math.Money, java.math.BigDecimal)} . Check
	 * situation when fee override is greater than course class price.
	 * Currently handle this situation like as negative discount
	 * That discount always beats a normal discount. It also beats having no discount at all
	 */
	@Test
	public void chooseBestDiscountVariantWithFeeOverride() {

		Discount discount = context.newObject(Discount.class);
		discount.setDiscountRate(null);
		discount.setDiscountAmount(new Money(new BigDecimal(200)));
		discount.setDiscountType(DiscountType.FEE_OVERRIDE);
		DiscountCourseClass feeOverride = context.newObject(DiscountCourseClass.class);
		feeOverride.setDiscount(discount);
		discount.addToDiscountCourseClasses(feeOverride);

		DiscountCourseClass chosenDiscount = (DiscountCourseClass) DiscountUtils.chooseDiscountForApply(Arrays.asList(feeOverride), PRICE, new BigDecimal(0));

		assertNotNull(chosenDiscount);
		assertEquals(feeOverride, chosenDiscount);

		chosenDiscount = (DiscountCourseClass) DiscountUtils.chooseDiscountForApply(
				Arrays.asList(singleDiscountWithRate, feeOverride, singleDiscountWithRateMin), PRICE, new BigDecimal(0));

		assertNotNull(chosenDiscount);
		assertEquals(feeOverride, chosenDiscount);

		feeOverride.setDiscountAmount(new Money(new BigDecimal(60)));

		chosenDiscount = (DiscountCourseClass) DiscountUtils.chooseDiscountForApply(
				Arrays.asList(singleDiscountWithRate, combDiscountWithRateMax, singleDiscountWithRateMin, feeOverride), PRICE, new BigDecimal(0));

		assertNotNull(chosenDiscount);
		assertEquals(feeOverride, chosenDiscount);
	}

	@Test
	public void testDiscountRounding() {
		Discount discount = context.newObject(Discount.class);
		discount.setDiscountType(DiscountType.PERCENT);
		discount.setDiscountRate(new BigDecimal("0.11"));

		DiscountCourseClass classDiscount = context.newObject(DiscountCourseClass.class);
		classDiscount.setDiscount(discount);
		discount.addToDiscountCourseClasses(classDiscount);

		discount.setRoundingMode(MoneyRounding.ROUNDING_NONE);

		assertEquals(new Money("856.63"), DiscountUtils.getDiscountedFee(classDiscount, new Money("875.00"), new BigDecimal(0.1)));
		assertEquals(new Money("96.25"), DiscountUtils.discountValue(classDiscount, new Money("875.00"), new BigDecimal(0.1)));

		discount.setRoundingMode(MoneyRounding.ROUNDING_10C);

		assertEquals(new Money("856.60"), DiscountUtils.getDiscountedFee(classDiscount, new Money("875.00"), new BigDecimal(0.1)));
		assertEquals(new Money("96.27"), DiscountUtils.discountValue(classDiscount, new Money("875.00"), new BigDecimal(0.1)));

		discount.setRoundingMode(MoneyRounding.ROUNDING_50C);

		assertEquals(new Money("856.50"), DiscountUtils.getDiscountedFee(classDiscount, new Money("875.00"), new BigDecimal(0.1)));
		assertEquals(new Money("96.36"), DiscountUtils.discountValue(classDiscount, new Money("875.00"), new BigDecimal(0.1)));

		discount.setRoundingMode(MoneyRounding.ROUNDING_1D);

		assertEquals(new Money("857.00"), DiscountUtils.getDiscountedFee(classDiscount, new Money("875.00"), new BigDecimal(0.1)));
		assertEquals(new Money("95.91"), DiscountUtils.discountValue(classDiscount, new Money("875.00"), new BigDecimal(0.1)));
	}

	/**
	 * Test that any negative always beats a normal discounts.
	 * If multiple negative discounts allowed to apply then the higher (as an absolute value) applies.
	 */
	@Test
	public void testNegativeDiscount() {

		Money classPrice = new Money("800.00");
		BigDecimal taxRate = new BigDecimal(0.1);

		Discount discount = context.newObject(Discount.class);
		discount.setDiscountType(DiscountType.PERCENT);
		discount.setDiscountRate(new BigDecimal("0.50"));

		DiscountCourseClass positivePercent = context.newObject(DiscountCourseClass.class);
		positivePercent.setDiscount(discount);
		discount.addToDiscountCourseClasses(positivePercent);

		discount = context.newObject(Discount.class);
		discount.setDiscountType(DiscountType.DOLLAR);
		discount.setDiscountAmount(new Money("300"));

		DiscountCourseClass positiveDollar = context.newObject(DiscountCourseClass.class);
		positiveDollar.setDiscount(discount);
		discount.addToDiscountCourseClasses(positiveDollar);

		discount = context.newObject(Discount.class);
		discount.setDiscountType(DiscountType.FEE_OVERRIDE);
		discount.setDiscountAmount(new Money("600"));

		DiscountCourseClass positiveFeeOverride = context.newObject(DiscountCourseClass.class);
		positiveFeeOverride.setDiscount(discount);
		discount.addToDiscountCourseClasses(positiveFeeOverride);

		discount = context.newObject(Discount.class);
		discount.setDiscountType(DiscountType.PERCENT);
		discount.setDiscountRate(new BigDecimal("-0.10"));
		discount.setRoundingMode(MoneyRounding.ROUNDING_1D);

		DiscountCourseClass negative10Percent = context.newObject(DiscountCourseClass.class);
		negative10Percent.setDiscount(discount);
		discount.addToDiscountCourseClasses(negative10Percent);

		assertEquals(negative10Percent, DiscountUtils.chooseDiscountForApply(Arrays.asList(positivePercent, positiveDollar, positiveFeeOverride, negative10Percent), classPrice, taxRate));
		assertEquals(new Money("968"), DiscountUtils.getDiscountedFee(negative10Percent, classPrice, taxRate));
		assertEquals(new Money("-80"), DiscountUtils.discountValue(negative10Percent, classPrice, taxRate));

		discount = context.newObject(Discount.class);
		discount.setDiscountType(DiscountType.PERCENT);
		discount.setDiscountRate(new BigDecimal("-0.20"));
		discount.setRoundingMode(MoneyRounding.ROUNDING_1D);

		DiscountCourseClass negative20Percent = context.newObject(DiscountCourseClass.class);
		negative20Percent.setDiscount(discount);
		discount.addToDiscountCourseClasses(negative20Percent);

		assertEquals(negative20Percent, DiscountUtils.chooseDiscountForApply(Arrays.asList(positivePercent, positiveDollar, positiveFeeOverride, negative10Percent, negative20Percent), classPrice, taxRate));
		assertEquals(new Money("1056"), DiscountUtils.getDiscountedFee(negative20Percent, classPrice, taxRate));
		assertEquals(new Money("-160"), DiscountUtils.discountValue(negative20Percent, classPrice, taxRate));


		discount = context.newObject(Discount.class);
		discount.setDiscountType(DiscountType.DOLLAR);
		discount.setDiscountAmount(new Money("-10"));
		discount.setRoundingMode(MoneyRounding.ROUNDING_1D);

		DiscountCourseClass negative10Dollar = context.newObject(DiscountCourseClass.class);
		negative10Dollar.setDiscount(discount);
		discount.addToDiscountCourseClasses(negative10Dollar);

		// check that the higher (as an absolute value) negative discount still beats (- %20)
		assertEquals(negative20Percent, DiscountUtils.chooseDiscountForApply(Arrays.asList(positivePercent, positiveDollar, positiveFeeOverride, negative10Percent, negative20Percent, negative10Dollar), classPrice, taxRate));

		discount = context.newObject(Discount.class);
		discount.setDiscountType(DiscountType.DOLLAR);
		discount.setDiscountAmount(new Money("-200"));
		discount.setRoundingMode(MoneyRounding.ROUNDING_1D);

		DiscountCourseClass negative200Dollar = context.newObject(DiscountCourseClass.class);
		negative200Dollar.setDiscount(discount);
		discount.addToDiscountCourseClasses(negative200Dollar);

		assertEquals(negative200Dollar, DiscountUtils.chooseDiscountForApply(Arrays.asList(positivePercent, positiveDollar, positiveFeeOverride, negative10Percent, negative20Percent, negative10Dollar, negative200Dollar), classPrice, taxRate));
		assertEquals(new Money("1100"), DiscountUtils.getDiscountedFee(negative200Dollar, classPrice, taxRate));
		assertEquals(new Money("-200"), DiscountUtils.discountValue(negative200Dollar, classPrice, taxRate));

		discount = context.newObject(Discount.class);
		discount.setDiscountType(DiscountType.FEE_OVERRIDE);
		discount.setDiscountAmount(new Money("1300"));
		discount.setRoundingMode(MoneyRounding.ROUNDING_1D);

		DiscountCourseClass negativeFeeOverride = context.newObject(DiscountCourseClass.class);
		negativeFeeOverride.setDiscount(discount);
		discount.addToDiscountCourseClasses(negativeFeeOverride);

		// check that negative "Fee Override" discount has the higher surcharge
		assertEquals(negativeFeeOverride, DiscountUtils.chooseDiscountForApply(Arrays.asList(positivePercent, positiveDollar, positiveFeeOverride, negative10Percent, negative20Percent, negative10Dollar, negative200Dollar, negativeFeeOverride), classPrice, taxRate));
		assertEquals(new Money("1430"), DiscountUtils.getDiscountedFee(negativeFeeOverride, classPrice, taxRate));
		assertEquals(new Money("-500"), DiscountUtils.discountValue(negativeFeeOverride, classPrice, taxRate));
	}
}
