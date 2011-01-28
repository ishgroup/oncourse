package ish.oncourse.model;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;

import java.util.Arrays;
import java.util.List;

import org.junit.BeforeClass;
import org.junit.Test;

public class PotentialDiscountsPolicyTest extends AbstractDiscountPolicyTest {
	
	/**
	 * Initializes discounts entities.
	 */
	@BeforeClass
	public static void initPolicy() {
		discountPolicy = new PotentialDiscountsPolicy(promotions);
	}

	@Test
	public void getApplicableByPolicyTest() {
		List<Discount> applicableByPolicy = discountPolicy.getApplicableByPolicy(Arrays.asList(
				combDiscountWithAmount, singleDiscountWithRate, combDiscountWithRateMax,
				singleDiscountWithRateMin));
		assertFalse(applicableByPolicy.isEmpty());
		assertEquals(2, applicableByPolicy.size());
		assertEquals(combDiscountWithAmount, applicableByPolicy.get(0));
		assertEquals(singleDiscountWithRateMin, applicableByPolicy.get(1));
	}

	

	@Test
	public void filterDiscountsSmokeTest() {
		List<Discount> filteredDiscounts = discountPolicy.filterDiscounts(Arrays.asList(
				combDiscountWithAmount, singleDiscountWithRate, combDiscountWithRateMax,
				singleDiscountWithRateMin));
		assertFalse(filteredDiscounts.isEmpty());
		assertEquals(1, filteredDiscounts.size());
		assertEquals(singleDiscountWithRateMin, filteredDiscounts.get(0));
	}

	
}
