package ish.oncourse.model;

import org.junit.BeforeClass;
import org.junit.Test;

import java.math.BigDecimal;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;

/**
 * Test for {@link PotentialDiscountsPolicy}.
 * 
 * @author ksenia
 * 
 */
public class PotentialDiscountsPolicyTest extends AbstractDiscountPolicyTest {

	/**
	 * Initializes discountPolicy.
	 */
	@BeforeClass
	public static void initPolicy() {
		discountPolicy = new PotentialDiscountsPolicy(promotions);
	}

	/**
	 * Test for {@link PotentialDiscountsPolicy#getApplicableByPolicy(List, ish.math.Money, ish.math.Money)},
	 * which chooses 2 discounts which are contained in promotions list.
	 */
	@Test
	public void getApplicableByPolicyTest() {
		List<Discount> applicableByPolicy = discountPolicy.getApplicableByPolicy(Arrays.asList(
				combDiscountWithAmount, singleDiscountWithRate, combDiscountWithRateMax,
				singleDiscountWithRateMin, hiddenDiscountWithAmount, nonAvailableDiscountWithAmount),FEE_EX_GST, FEE_GST);
		assertFalse(applicableByPolicy.isEmpty());
		assertEquals(3, applicableByPolicy.size());
		assertEquals(combDiscountWithAmount, applicableByPolicy.get(0));
		assertEquals(singleDiscountWithRate, applicableByPolicy.get(1));
		assertEquals(singleDiscountWithRateMin, applicableByPolicy.get(2));
		
		// hidden discount should not be in potential discounts list
		assertFalse(applicableByPolicy.contains(hiddenDiscountWithAmount));
		// non available discount should not be in potential discounts list
		assertFalse(applicableByPolicy.contains(nonAvailableDiscountWithAmount));
	}

	/**
	 * Test for {@link PotentialDiscountsPolicy#filterDiscounts(List, ish.math.Money, ish.math.Money, java.math.BigDecimal)}, which
	 * chooses the best valiant from the applicable by policy: from 10$ and 15$
	 * of discount value chooses discount with 15.
	 */
	@Test
	public void filterDiscountsSmokeTest() {
		List<Discount> filteredDiscounts = discountPolicy.filterDiscounts(Arrays.asList(
				combDiscountWithAmount, singleDiscountWithRate, combDiscountWithRateMax,
				singleDiscountWithRateMin), FEE_EX_GST,FEE_GST, new BigDecimal(0.1));
		assertFalse(filteredDiscounts.isEmpty());
		assertEquals(1, filteredDiscounts.size());
		assertEquals(singleDiscountWithRate, filteredDiscounts.get(0));
	}

	@Test
	public void getApplicableByPolicyEmptyInputTest() {
		List<Discount> applicableByPolicy = discountPolicy.getApplicableByPolicy(null, FEE_EX_GST, FEE_GST);
		assertTrue(applicableByPolicy.isEmpty());
		applicableByPolicy = discountPolicy.getApplicableByPolicy(Collections.EMPTY_LIST, FEE_EX_GST, FEE_GST);
		assertTrue(applicableByPolicy.isEmpty());
	}

	@Test
	public void filterDiscountsEmptyTest() {
		List<Discount> filteredDiscounts = discountPolicy.filterDiscounts(null, FEE_EX_GST, FEE_GST, new BigDecimal(0.1));
		assertTrue(filteredDiscounts.isEmpty());
		filteredDiscounts = discountPolicy.filterDiscounts(Collections.EMPTY_LIST, FEE_EX_GST, FEE_GST,new BigDecimal(0.1));
		assertTrue(filteredDiscounts.isEmpty());
	}

	@Test
	public void testNonAvailableDiscounts() {
		List<Discount> filteredDiscounts = discountPolicy.filterDiscounts(Arrays.asList(nonAvailableDiscountWithAmount), FEE_EX_GST, FEE_GST, new BigDecimal(0.1));
		assertTrue(filteredDiscounts.isEmpty());
	}

}
