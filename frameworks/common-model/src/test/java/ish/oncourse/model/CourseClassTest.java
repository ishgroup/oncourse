/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

package ish.oncourse.model;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;
import ish.math.Money;
import ish.oncourse.test.ContextUtils;

import java.math.BigDecimal;
import java.util.Arrays;
import java.util.Calendar;
import java.util.List;

import org.apache.cayenne.ObjectContext;
import org.joda.time.DateTime;
import org.junit.After;
import org.junit.Before;
import org.junit.BeforeClass;
import org.junit.Test;

/**
 * Test for {@link CourseClass} methods.
 * 
 * @author marek
 */
public class CourseClassTest {

	/**
	 * Data context for persistent objects.
	 */
	private static ObjectContext context;
	/**
	 * The courseClass which will contain reference to 3 date-valid discounts.
	 */
	private static CourseClass firstClass;
	/**
	 * The courseClass which will contain reference to 1 invalid discount.
	 */
	private static CourseClass secondClass;
	/**
	 * The discount for {@link #secondClass} with expired date.
	 */
	private static Discount pastSecondClassDiscount;
	/**
	 * The discount for {@link #firstClass} with code.
	 */
	private static Discount currentPromotion;
	/**
	 * The discount for {@link #firstClass} with the reference to
	 * {@link ConcessionType}.
	 */
	private static Discount currentConcession;
	/**
	 * The discount for {@link #firstClass} without refererences to
	 * {@link ConcessionType}
	 */
	private static Discount concessionEmpty;
	/**
	 * The discount for {@link #firstClass} that will be available in future.
	 */
	private static Discount futureDicount;

	/**
	 * Initializes entities, commit needed changes.
	 * 
	 * @throws Exception
	 */
	@BeforeClass
	public static void setUpClass() throws Exception {
		Calendar date = Calendar.getInstance();
		
		ContextUtils.setupDataSources();
		
		context = ContextUtils.createObjectContext();

		College college = context.newObject(College.class);
		college.setName("name");

		Course course = context.newObject(Course.class);
		course.setCollege(college);

		firstClass = context.newObject(CourseClass.class);
		firstClass.setCourse(course);
		firstClass.setCollege(college);

		currentPromotion = context.newObject(Discount.class);
		currentPromotion.setCollege(college);
		currentPromotion.setCode("currentPromotion");
		// two days ago
		date.add(Calendar.DATE, -2);
		currentPromotion.setValidFrom(date.getTime());

		DiscountCourseClass dcc = context.newObject(DiscountCourseClass.class);
		dcc.setDiscount(currentPromotion);
		dcc.setCourseClass(firstClass);

		currentConcession = context.newObject(Discount.class);
		currentConcession.setCollege(college);
		// almost one month in future
		date.add(Calendar.MONTH, 1);
		currentConcession.setValidTo(date.getTime());
		currentConcession.setName("name");

		ConcessionType ct = context.newObject(ConcessionType.class);
		ct.setName("name");
		DiscountConcessionType dct = context.newObject(DiscountConcessionType.class);
		dct.setCollege(college);
		dct.setConcessionType(ct);
		dct.setDiscount(currentConcession);
		dcc = context.newObject(DiscountCourseClass.class);
		dcc.setDiscount(currentConcession);
		dcc.setCourseClass(firstClass);

		concessionEmpty = context.newObject(Discount.class);
		concessionEmpty.setCollege(college);
		dcc = context.newObject(DiscountCourseClass.class);
		dcc.setDiscount(concessionEmpty);
		dcc.setCourseClass(firstClass);

		futureDicount = context.newObject(Discount.class);
		futureDicount.setCollege(college);
		futureDicount.setValidFrom(date.getTime());
		dcc = context.newObject(DiscountCourseClass.class);
		dcc.setDiscount(futureDicount);
		dcc.setCourseClass(firstClass);

		secondClass = context.newObject(CourseClass.class);
		secondClass.setCourse(course);
		secondClass.setCollege(college);

		pastSecondClassDiscount = context.newObject(Discount.class);
		pastSecondClassDiscount.setCollege(college);
		// almost one month ago
		date.add(Calendar.MONTH, -2);
		pastSecondClassDiscount.setValidTo(date.getTime());
		dcc = context.newObject(DiscountCourseClass.class);
		dcc.setDiscount(pastSecondClassDiscount);
		dcc.setCourseClass(secondClass);

		context.commitChanges();
	}

	@Before
	public void setUp() {
	}

	@After
	public void tearDown() {
	}

	/**
	 * Test of getIsoStartDate method, of class CourseClass.
	 */
	@Test
	public void testGetIsoStartDate() {
		CourseClass instance = new CourseClass();
		instance.setStartDate(new DateTime(2010, 1, 27, 19, 26, 10, 10).toDate());
		String expResult = "20100127";
		String result = instance.getIsoStartDate();
		assertEquals(expResult, result);
	}

	/**
	 * Test of getIsoEndDate method, of class CourseClass.
	 */
	@Test
	public void testGetIsoEndDate() {
		CourseClass instance = new CourseClass();
		instance.setEndDate(new DateTime(2010, 1, 30, 19, 26, 10, 10).toDate());
		String expResult = "20100130";
		String result = instance.getIsoEndDate();
		assertEquals(expResult, result);
	}

	/**
	 * Emulates the situation when some of the class's discounts are restricted
	 * with dates and thus not retrieved by {@link CourseClass#getDiscounts()}.
	 */
	@Test
	public void getDiscountsTest() {
		List<Discount> firstClassDiscounts = firstClass.getDiscounts();
		assertFalse(firstClassDiscounts.isEmpty());
		assertEquals(3, firstClassDiscounts.size());
		assertTrue(firstClassDiscounts.contains(currentPromotion));
		assertTrue(firstClassDiscounts.contains(currentConcession));
		assertTrue(firstClassDiscounts.contains(concessionEmpty));

		List<Discount> secondClassDiscounts = secondClass.getDiscounts();
		assertTrue(secondClassDiscounts.isEmpty());
	}

	/**
	 * Emulates the retrieving of the class's discounts that have references to
	 * concession types.
	 */
	@Test
	public void getConcessionDiscountsTest() {
		List<Discount> firstClassConcessionDiscounts = firstClass.getConcessionDiscounts();
		assertFalse(firstClassConcessionDiscounts.isEmpty());
		assertEquals(1, firstClassConcessionDiscounts.size());
		assertEquals(currentConcession, firstClassConcessionDiscounts.get(0));

		List<Discount> secondClassDiscounts = secondClass.getConcessionDiscounts();
		assertTrue(secondClassDiscounts.isEmpty());
	}

	/**
	 * Test for {@link CourseClass#getTaxRate()}. Emulates the situations when
	 * the fee and tax are zero, when fee is zero and when the fee and tax are
	 * both not zero.
	 */
	@Test
	public void getTaxRateTest() {
		assertEquals(Money.ZERO, firstClass.getFeeExGst());
		assertEquals(Money.ZERO, firstClass.getFeeGst());
		assertEquals(BigDecimal.ZERO, firstClass.getTaxRate());
		firstClass.setFeeGst(new Money("10"));
		assertEquals(BigDecimal.ZERO, firstClass.getTaxRate());
		firstClass.setFeeExGst(new Money("100"));
		assertTrue(new BigDecimal("0.10").compareTo(firstClass.getTaxRate()) == 0);
	}

	/**
	 * Emulates the applying of the discount with amount: <br/>
	 * Assumptions: fee=100, tax=10; discountAmount=20. <br/>
	 * Digits when applied: discountAmountExTax=20, discountAmountIncTax=22,
	 * discountedFee=80, discountedTax=8, discountedFeeIncTax=88.
	 */
	@Test
	public void applyDiscountWithAmountTest() {
		firstClass.setFeeExGst(new Money("100"));
		firstClass.setFeeGst(new Money("10"));
		assertEquals(new Money("100"), firstClass.getFeeExGst());
		assertEquals(new Money("10"), firstClass.getFeeGst());
		assertEquals(new Money("110"), firstClass.getFeeIncGst());
		// 20$ discount
		currentPromotion.setDiscountAmount(new Money("20"));

		List<Discount> discWithAmount = Arrays.asList(currentPromotion);
		assertEquals(new Money("20"), firstClass.getDiscountAmountExTax(discWithAmount));
		assertEquals(new Money("22"), firstClass.getDiscountAmountIncTax(discWithAmount));
		Money discountedFee = firstClass.getDiscountedFee(discWithAmount);
		assertEquals(new Money("80"), discountedFee);
		Money discountedTax = firstClass.getDiscountedTax(discWithAmount);
		assertEquals(new Money("8"), discountedTax);
		assertTrue(firstClass.getTaxRate().compareTo(
				discountedTax.divide(discountedFee).toBigDecimal()) == 0);
		assertEquals(new Money("88"), firstClass.getDiscountedFeeIncTax(discWithAmount));

	}

	/**
	 * Emulates the applying of the discount with rate: <br/>
	 * Assumptions: fee=100, tax=10; discountRate=20%. <br/>
	 * Digits when applied: discountAmountExTax=20, discountAmountIncTax=22,
	 * discountedFee=80, discountedTax=8, discountedFeeIncTax=88.
	 */
	@Test
	public void applyDiscountWithRateTest() {
		firstClass.setFeeExGst(new Money("100"));
		firstClass.setFeeGst(new Money("10"));
		assertEquals(new Money("100"), firstClass.getFeeExGst());
		assertEquals(new Money("10"), firstClass.getFeeGst());
		assertEquals(new Money("110"), firstClass.getFeeIncGst());
		// 20% discount
		currentConcession.setDiscountRate(new BigDecimal("0.2"));

		List<Discount> discWithRate = Arrays.asList(currentConcession);
		assertEquals(new Money("20"), firstClass.getDiscountAmountExTax(discWithRate));
		assertEquals(new Money("22"), firstClass.getDiscountAmountIncTax(discWithRate));
		Money discountedFee = firstClass.getDiscountedFee(discWithRate);
		assertEquals(new Money("80"), discountedFee);
		Money discountedTax = firstClass.getDiscountedTax(discWithRate);
		assertEquals(new Money("8"), discountedTax);
		assertTrue(firstClass.getTaxRate().compareTo(
				discountedTax.divide(discountedFee).toBigDecimal()) == 0);

		assertEquals(new Money("88"), firstClass.getDiscountedFeeIncTax(discWithRate));

	}

	/**
	 * Emulates the applying of the list of dicounts: with amount of 20, with
	 * rate 20% and with rate 70%. Leads to 100% discount to price of 100.
	 */
	@Test
	public void applyMultipleDiscountsWithRateTest() {
		firstClass.setFeeExGst(new Money("100"));
		firstClass.setFeeGst(new Money("10"));
		assertEquals(new Money("100"), firstClass.getFeeExGst());
		assertEquals(new Money("10"), firstClass.getFeeGst());
		assertEquals(new Money("110"), firstClass.getFeeIncGst());
		// 20$,20%,70% - we get discount amount greater than price, so the
		// system applies 100% discount
		currentPromotion.setDiscountAmount(new Money("20"));
		currentConcession.setDiscountRate(new BigDecimal("0.2"));
		concessionEmpty.setDiscountRate(new BigDecimal("0.7"));

		List<Discount> multipleDiscounts = Arrays.asList(currentPromotion, currentConcession,
				concessionEmpty);
		assertEquals(new Money("100"), firstClass.getDiscountAmountExTax(multipleDiscounts));
		assertEquals(new Money("110"), firstClass.getDiscountAmountIncTax(multipleDiscounts));
		Money discountedFee = firstClass.getDiscountedFee(multipleDiscounts);
		assertEquals(new Money("0"), firstClass.getDiscountedFeeIncTax(multipleDiscounts));
		assertEquals(new Money("0"), discountedFee);
		Money discountedTax = firstClass.getDiscountedTax(multipleDiscounts);
		assertEquals(new Money("0"), discountedTax);

	}

}