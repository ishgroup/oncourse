package ish.oncourse.model;

import ish.math.Money;
import org.junit.BeforeClass;
import org.junit.Test;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;

import static org.junit.Assert.assertTrue;

/**
 * Contains fields and tests common for {@link PotentialDiscountsPolicyTest} and {@link RealDiscountsPolicyTest}
 * @author ksenia
 *
 */
public abstract class AbstractDiscountPolicyTest {
	protected static final Money FEE_EX_GST = new Money("100");
	/**
	 * Combinable discount with amount=10.
	 */
	public static Discount combDiscountWithAmount;
	/**
	 * Not combinable discount with the rate 20% and zero restrictions.
	 */
	public static Discount singleDiscountWithRate;
	/**
	 * Combinable discount with the rate 20% and maximum 10.
	 */
	public static Discount combDiscountWithRateMax;
	/**
	 * Not combinable discount with the rate 10% and minimum 15.
	 */
	public static Discount singleDiscountWithRateMin;
	/**
	 * Combinable discount for $10 amount which has hideOnWeb property set to true
	 */
	public static Discount hiddenDiscountWithAmount;
	/**
	 * Combinable discount for $10 amount which has isAvailableOnWeb property set to false
	 */
	public static Discount nonAvailableDiscountWithAmount;

	public static List<Discount> promotions;
	public static DiscountPolicy discountPolicy;

	/**
	 * Initializes discounts entities.
	 */
	@BeforeClass
	public static void init() {

		combDiscountWithAmount = new Discount();
		combDiscountWithAmount.setDiscountAmount(new Money(BigDecimal.TEN));
		combDiscountWithAmount.setCombinationType(true);
		combDiscountWithAmount.setCode("code1");
		combDiscountWithAmount.setHideOnWeb(false);

		singleDiscountWithRate = new Discount();
		singleDiscountWithRate.setDiscountRate(new BigDecimal(0.2));
		singleDiscountWithRate.setMaximumDiscount(Money.ZERO);
		singleDiscountWithRate.setMinimumDiscount(Money.ZERO);
		singleDiscountWithRate.setCombinationType(false);
		singleDiscountWithRate.setHideOnWeb(false);

		combDiscountWithRateMax = new Discount();
		combDiscountWithRateMax.setDiscountRate(new BigDecimal(0.2));
		combDiscountWithRateMax.setMaximumDiscount(new Money(BigDecimal.TEN));
		combDiscountWithRateMax.setCombinationType(true);
		combDiscountWithRateMax.setCode("code2");
		combDiscountWithRateMax.setHideOnWeb(false);

		singleDiscountWithRateMin = new Discount();
		singleDiscountWithRateMin.setDiscountRate(new BigDecimal(0.1));
		singleDiscountWithRateMin.setMinimumDiscount(new Money("15"));
		singleDiscountWithRateMin.setCombinationType(false);
		singleDiscountWithRateMin.setCode("code3");
		singleDiscountWithRateMin.setHideOnWeb(false);
		
		hiddenDiscountWithAmount = new Discount();
		hiddenDiscountWithAmount.setDiscountAmount(new Money(BigDecimal.TEN));
		hiddenDiscountWithAmount.setCombinationType(true);
		hiddenDiscountWithAmount.setHideOnWeb(true);
		
		nonAvailableDiscountWithAmount = new Discount();
		nonAvailableDiscountWithAmount.setDiscountAmount(new Money(BigDecimal.TEN));
		nonAvailableDiscountWithAmount.setCombinationType(true);
		nonAvailableDiscountWithAmount.setCode("unavail_code");
		nonAvailableDiscountWithAmount.setHideOnWeb(false);
		nonAvailableDiscountWithAmount.setIsAvailableOnWeb(false);
		
		promotions = new ArrayList<>();
		promotions.add(combDiscountWithAmount);
		promotions.add(singleDiscountWithRateMin);
	}

	@Test
	public void getApplicableByPolicyEmptyInputTest() {
		List<Discount> applicableByPolicy = discountPolicy.getApplicableByPolicy(null);
		assertTrue(applicableByPolicy.isEmpty());
		applicableByPolicy = discountPolicy.getApplicableByPolicy(Collections.EMPTY_LIST);
		assertTrue(applicableByPolicy.isEmpty());
	}

	@Test
	public void filterDiscountsEmptyTest() {
		List<Discount> filteredDiscounts = discountPolicy.filterDiscounts(null, FEE_EX_GST);
		assertTrue(filteredDiscounts.isEmpty());
		filteredDiscounts = discountPolicy.filterDiscounts(Collections.EMPTY_LIST, FEE_EX_GST);
		assertTrue(filteredDiscounts.isEmpty());
	}
	
	@Test
	public void testNonAvailableDiscounts() {
		List<Discount> filteredDiscounts = discountPolicy.filterDiscounts(Arrays.asList(nonAvailableDiscountWithAmount), FEE_EX_GST);
		assertTrue(filteredDiscounts.isEmpty());
	}

}
