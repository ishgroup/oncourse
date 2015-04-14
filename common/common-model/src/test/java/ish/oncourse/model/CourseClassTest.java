/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

package ish.oncourse.model;

import ish.common.types.CourseEnrolmentType;
import ish.math.Money;
import ish.oncourse.test.ContextUtils;
import org.apache.cayenne.ObjectContext;
import org.apache.commons.lang.StringUtils;
import org.joda.time.DateTime;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;

import java.math.BigDecimal;
import java.util.Arrays;
import java.util.Calendar;
import java.util.Date;
import java.util.List;

import static org.junit.Assert.*;

/**
 * Test for {@link CourseClass} methods.
 * 
 * @author marek
 */
public class CourseClassTest {

	/**
	 * Data context for persistent objects.
	 */
	private ObjectContext context;
	/**
	 * The courseClass which will contain reference to 3 date-valid discounts.
	 */
	private CourseClass firstClass;
	/**
	 * The courseClass which will contain reference to 1 invalid discount.
	 */
	private CourseClass secondClass;
	
	/**
	 * The courseClass which will contain reference to 1 discount.
	 */
	private CourseClass thirdClass;
	
	/**
	 * The courseClass which will contain reference to 1 disabled discount.
	 */
	private CourseClass fourthClass;
	
	/**
	 * The discount for {@link #secondClass} with expired date.
	 */
	private Discount pastSecondClassDiscount;
	/**
	 * The discount for {@link #firstClass} with code.
	 */
	private Discount currentPromotion;
	
	/**
	 * The discount for {@link #firstClass} with the reference to
	 * {@link ConcessionType}.
	 */
	private Discount currentConcession;
	/**
	 * The discount for {@link #firstClass} without refererences to
	 * {@link ConcessionType}
	 */
	private Discount concessionEmpty;
	/**
	 * The discount for {@link #firstClass} that will be available in future.
	 */
	private Discount futureDicount;

	/**
	 * The discount for {@link #firstClass} with code = "".
	 */
	private Discount concessionEmptyCode;
	
	private Discount disabledDiscount;
	
	/**
	 * Initializes entities, commit needed changes.
	 * 
	 * @throws Exception
	 */
	@Before
	public void setUpClass() throws Exception {
		Calendar date = Calendar.getInstance();
		
		ContextUtils.setupDataSources();
		
		context = ContextUtils.createObjectContext();

		College college = context.newObject(College.class);
		college.setName("name");
		college.setTimeZone("Australia/Sydney");
		college.setFirstRemoteAuthentication(new Date());
		college.setRequiresAvetmiss(false);

		Course course = context.newObject(Course.class);
		course.setCollege(college);
		course.setEnrolmentType(CourseEnrolmentType.OPEN_FOR_ENROLMENT);

		firstClass = context.newObject(CourseClass.class);
		firstClass.setCourse(course);
		firstClass.setCollege(college);
		firstClass.setMaximumPlaces(3);
		firstClass.setIsDistantLearningCourse(false);

		currentPromotion = context.newObject(Discount.class);
		currentPromotion.setCollege(college);
		currentPromotion.setCode("currentPromotion");
		currentPromotion.setIsAvailableOnWeb(true);
		
		// two days ago
		date.add(Calendar.DATE, -2);
		currentPromotion.setValidFrom(date.getTime());
		currentPromotion.setHideOnWeb(false);

		DiscountCourseClass dcc = context.newObject(DiscountCourseClass.class);
		dcc.setDiscount(currentPromotion);
		dcc.setCourseClass(firstClass);
		dcc.setCollege(college);

		currentConcession = context.newObject(Discount.class);
		currentConcession.setCollege(college);
		// almost one month in future
		date.add(Calendar.MONTH, 1);
		currentConcession.setValidTo(date.getTime());
		currentConcession.setName("name");
		currentConcession.setHideOnWeb(false);
		currentConcession.setIsAvailableOnWeb(true);

		ConcessionType ct = context.newObject(ConcessionType.class);
		ct.setName("name");
		ct.setIsEnabled(true);
		ct.setCollege(college);		
		DiscountConcessionType dct = context.newObject(DiscountConcessionType.class);
		dct.setCollege(college);
		dct.setConcessionType(ct);
		dct.setDiscount(currentConcession);
		dcc = context.newObject(DiscountCourseClass.class);
		dcc.setDiscount(currentConcession);
		dcc.setCourseClass(firstClass);
		dcc.setCollege(college);

		concessionEmpty = context.newObject(Discount.class);
		concessionEmpty.setCollege(college);
		concessionEmpty.setHideOnWeb(false);
		concessionEmpty.setIsAvailableOnWeb(true);
		dcc = context.newObject(DiscountCourseClass.class);
		dcc.setDiscount(concessionEmpty);
		dcc.setCourseClass(firstClass);
		dcc.setCollege(college);

		futureDicount = context.newObject(Discount.class);
		futureDicount.setCollege(college);
		futureDicount.setValidFrom(date.getTime());
		futureDicount.setHideOnWeb(false);
		futureDicount.setIsAvailableOnWeb(true);
		dcc = context.newObject(DiscountCourseClass.class);
		dcc.setDiscount(futureDicount);
		dcc.setCourseClass(firstClass);
		dcc.setCollege(college);

		secondClass = context.newObject(CourseClass.class);
		secondClass.setCourse(course);
		secondClass.setCollege(college);
		secondClass.setMaximumPlaces(3);
		secondClass.setIsDistantLearningCourse(false);
		
		pastSecondClassDiscount = context.newObject(Discount.class);
		pastSecondClassDiscount.setCollege(college);
		
		// almost one month ago
		date.add(Calendar.MONTH, -2);
		pastSecondClassDiscount.setValidTo(date.getTime());
		pastSecondClassDiscount.setHideOnWeb(false);
		pastSecondClassDiscount.setIsAvailableOnWeb(true);
		dcc = context.newObject(DiscountCourseClass.class);
		dcc.setDiscount(pastSecondClassDiscount);
		dcc.setCourseClass(secondClass);
		dcc.setCollege(college);
		
		
		thirdClass = context.newObject(CourseClass.class);
		thirdClass.setCourse(course);
		thirdClass.setCollege(college);
		thirdClass.setMaximumPlaces(3);
		thirdClass.setIsDistantLearningCourse(false);
		
		concessionEmptyCode = context.newObject(Discount.class);
		concessionEmptyCode.setCollege(college);
		concessionEmptyCode.setCode(StringUtils.EMPTY);
		concessionEmptyCode.setHideOnWeb(false);
		concessionEmptyCode.setIsAvailableOnWeb(true);
		ConcessionType ctype = context.newObject(ConcessionType.class);
		ctype.setName("name");
		ctype.setIsEnabled(true);
		ctype.setCollege(college);
		DiscountConcessionType dctype = context.newObject(DiscountConcessionType.class);
		dctype.setCollege(college);
		dctype.setConcessionType(ctype);
		dctype.setDiscount(concessionEmptyCode);
		
		dcc = context.newObject(DiscountCourseClass.class);
		dcc.setDiscount(concessionEmptyCode);
		dcc.setCourseClass(thirdClass);
		dcc.setCollege(college);
		
		fourthClass = context.newObject(CourseClass.class);
		fourthClass.setCourse(course);
		fourthClass.setCollege(college);
		fourthClass.setMaximumPlaces(3);
		fourthClass.setIsDistantLearningCourse(false);
		
		disabledDiscount = context.newObject(Discount.class);
		disabledDiscount.setCollege(college);
		disabledDiscount.setCode(StringUtils.EMPTY);
		disabledDiscount.setHideOnWeb(false);
		disabledDiscount.setIsAvailableOnWeb(false);
		
		ConcessionType cctype = context.newObject(ConcessionType.class);
		cctype.setName("name");
		cctype.setIsEnabled(false);
		cctype.setCollege(college);
		
		DiscountConcessionType dcctype = context.newObject(DiscountConcessionType.class);
		dcctype.setCollege(college);
		dcctype.setConcessionType(cctype);
		dcctype.setDiscount(disabledDiscount);
		
		dcc = context.newObject(DiscountCourseClass.class);
		dcc.setDiscount(disabledDiscount);
		dcc.setCourseClass(fourthClass);
		dcc.setCollege(college);
		context.commitChanges();
		
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
		instance.setMaximumPlaces(3);
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
		instance.setMaximumPlaces(3);
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
		assertFalse(firstClassDiscounts.contains(disabledDiscount));
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
		
		List<Discount> thirdClassDiscounts = thirdClass.getConcessionDiscounts();
		assertFalse(thirdClassDiscounts.isEmpty());
		assertEquals(1, thirdClassDiscounts.size());
		assertTrue(thirdClassDiscounts.contains(concessionEmptyCode));
		
		List<Discount> fourthClassDiscounts = fourthClass.getConcessionDiscounts();
		assertTrue("Fourth class should contains only 1 disabled discount", fourthClassDiscounts.isEmpty());
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