package ish.oncourse.model;

import org.junit.BeforeClass;
import org.junit.Test;

import java.util.Arrays;
import java.util.List;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;

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
	 * Test for {@link PotentialDiscountsPolicy#getApplicableByPolicy(List)},
	 * which chooses 2 discounts which are contained in promotions list.
	 */
	@Test
	public void getApplicableByPolicyTest() {
		List<Discount> applicableByPolicy = discountPolicy.getApplicableByPolicy(Arrays.asList(
				combDiscountWithAmount, singleDiscountWithRate, combDiscountWithRateMax,
				singleDiscountWithRateMin, hiddenDiscountWithAmount, nonAvailableDiscountWithAmount));
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
	 * Test for {@link PotentialDiscountsPolicy#filterDiscounts(List)}, which
	 * chooses the best valiant from the applicable by policy: from 10$ and 15$
	 * of discount value chooses discount with 15.
	 */
	@Test
	public void filterDiscountsSmokeTest() {
		List<Discount> filteredDiscounts = discountPolicy.filterDiscounts(Arrays.asList(
				combDiscountWithAmount, singleDiscountWithRate, combDiscountWithRateMax,
				singleDiscountWithRateMin), FEE_EX_GST);
		assertFalse(filteredDiscounts.isEmpty());
		assertEquals(1, filteredDiscounts.size());
		assertEquals(singleDiscountWithRate, filteredDiscounts.get(0));
	}

}
