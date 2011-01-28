package ish.oncourse.model;

import static org.junit.Assert.assertTrue;
import ish.math.Money;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import org.junit.BeforeClass;
import org.junit.Test;

public abstract class AbstractDiscountPolicyTest {
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

		singleDiscountWithRate = new Discount();
		singleDiscountWithRate.setDiscountRate(new BigDecimal(0.2));
		singleDiscountWithRate.setMaximumDiscount(Money.ZERO);
		singleDiscountWithRate.setMinimumDiscount(Money.ZERO);
		singleDiscountWithRate.setCombinationType(false);

		combDiscountWithRateMax = new Discount();
		combDiscountWithRateMax.setDiscountRate(new BigDecimal(0.2));
		combDiscountWithRateMax.setMaximumDiscount(new Money(BigDecimal.TEN));
		combDiscountWithRateMax.setCombinationType(true);
		combDiscountWithRateMax.setCode("code2");

		singleDiscountWithRateMin = new Discount();
		singleDiscountWithRateMin.setDiscountRate(new BigDecimal(0.1));
		singleDiscountWithRateMin.setMinimumDiscount(new Money("15"));
		singleDiscountWithRateMin.setCombinationType(false);
		singleDiscountWithRateMin.setCode("code3");
		promotions = new ArrayList<Discount>();
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
		List<Discount> filteredDiscounts = discountPolicy.filterDiscounts(null);
		assertTrue(filteredDiscounts.isEmpty());
		filteredDiscounts = discountPolicy.filterDiscounts(Collections.EMPTY_LIST);
		assertTrue(filteredDiscounts.isEmpty());
	}

}
