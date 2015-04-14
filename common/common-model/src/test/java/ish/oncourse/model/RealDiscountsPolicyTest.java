package ish.oncourse.model;

import ish.common.types.EnrolmentStatus;
import ish.oncourse.model.auto._Discount;
import ish.oncourse.test.ContextUtils;
import org.apache.cayenne.ObjectContext;
import org.junit.BeforeClass;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.runners.MockitoJUnitRunner;

import java.math.BigDecimal;
import java.util.*;

import static org.junit.Assert.*;

/**
 * Test for {@link RealDiscountsPolicy}.
 * 
 * @author ksenia
 * 
 */
@RunWith(MockitoJUnitRunner.class)
public class RealDiscountsPolicyTest extends AbstractDiscountPolicyTest {
	/**
	 * Calendar instance for date types.
	 */
	private static Calendar testDate;
	/**
	 * Cayenne context for creating entity instances.
	 */
	private static ObjectContext context;
	/**
	 * The student for the eligibility tests.
	 */
	private static Student student;

	/**
	 * The discount for eligibility and applicability tests.
	 */
	private static Discount discount;

	/**
	 * Concession type that hasn't date restrictions.
	 */
	private static ConcessionType concessionType1;
	/**
	 * Student concession that hasn't date restrictions, referred to
	 * {@link RealDiscountsPolicyTest#concessionType1}.
	 */
	private static StudentConcession studentConcession;
	/**
	 * Concession type that has date restrictions.
	 */
	private static ConcessionType concessionType2;
	/**
	 * Concession type that has date restrictions.
	 */
	private static ConcessionType concessionTypeForExpiredDateTest;
	/**
	 * Student concession that has valid expire date, referred to
	 * {@link RealDiscountsPolicyTest#concessionType2}.
	 */
	private static StudentConcession studentConcession2;

	/**
	 * Student concession which has been already expired, referred to
	 * {@link RealDiscountsPolicyTest#concessionType2}.
	 */
	private static StudentConcession studentConcessionExpired;

	/**
	 * Initializes instance variables.
	 * 
	 * @throws Exception
	 */
	@BeforeClass
	public static void initPolicy() throws Exception {
		testDate = new GregorianCalendar();

		ContextUtils.setupDataSources();

		context = ContextUtils.createObjectContext();

		// prepare and commit data that should be stored(to prevent using of
		// temp records as query parameters)
		commitDataForEgibilityTest();

		// initial values for discount
		discount = context.newObject(Discount.class);
		discount.setDiscountRate(new BigDecimal("0.25"));

	}

	/**
	 * Commits the data needed to be committed for
	 * {@link #isStudentEligibileWorkTest()}.
	 */
	private static void commitDataForEgibilityTest() {
		College college = context.newObject(College.class);
		college.setName("name");
		college.setTimeZone("Australia/Sydney");
		college.setFirstRemoteAuthentication(new Date());
		college.setRequiresAvetmiss(false);
		Contact contact = context.newObject(Contact.class);
		contact.setFamilyName("familyName");
		contact.setCollege(college);
		student = context.newObject(Student.class);
		student.setCollege(college);
		student.setContact(contact);
		concessionType1 = context.newObject(ConcessionType.class);
		concessionType1.setName("ct1");
		concessionType1.setCollege(college);
		studentConcession = context.newObject(StudentConcession.class);
		studentConcession.setCollege(college);
		studentConcession.setConcessionType(concessionType1);
		student.addToStudentConcessions(studentConcession);

		concessionTypeForExpiredDateTest = context.newObject(ConcessionType.class);
		concessionTypeForExpiredDateTest.setName("ctExpired");
		concessionTypeForExpiredDateTest.setHasExpiryDate(true);
		concessionTypeForExpiredDateTest.setCollege(college);

		studentConcessionExpired = context.newObject(StudentConcession.class);
		studentConcessionExpired.setCollege(college);
		studentConcessionExpired.setConcessionType(concessionTypeForExpiredDateTest);

		testDate.setTime(new Date());
		studentConcessionExpired.setExpiresOn(testDate.getTime());

		student.addToStudentConcessions(studentConcessionExpired);

		concessionType2 = context.newObject(ConcessionType.class);
		concessionType2.setName("ct2");
		concessionType2.setHasExpiryDate(true);
		concessionType2.setCollege(college);

		studentConcession2 = context.newObject(StudentConcession.class);
		studentConcession2.setCollege(college);
		studentConcession2.setConcessionType(concessionType2);

		testDate.add(Calendar.YEAR, 2);
		studentConcession2.setExpiresOn(testDate.getTime());

		student.addToStudentConcessions(studentConcession2);

		context.commitChanges();
	}

	/**
	 * Emulates the situations when "false" should be returned because of null
	 * parameters.
	 */
	@Test
	public void isStudentEligibileEmptyTest() {
		Student student = new Student();
		discountPolicy = new RealDiscountsPolicy(promotions);
		assertFalse(((RealDiscountsPolicy) discountPolicy).isStudentEligibile(null, null));
		assertFalse(((RealDiscountsPolicy) discountPolicy).isStudentEligibile(student, null));
		assertFalse(((RealDiscountsPolicy) discountPolicy).isStudentEligibile(null, combDiscountWithAmount));
	}

	/**
	 * Emulates the situations when the student is not eligible for some reason,
	 * then correct this reason, and shows changes.
	 */
	@Test
	public void isStudentEligibileWorkTest() {

		// discount has null restrictions, that's why the student is eligible
		assertTrue(((RealDiscountsPolicy) discountPolicy).isStudentEligibile(student, combDiscountWithAmount));

		testEnrolledWithinDaysRestriction();

		testStudentAgeRestriction();

		testConcessionsRestrictions();

		testPostcodeRestrictions();
	}

	/**
	 * If the {@link _Discount#STUDENT_ENROLLED_WITHIN_DAYS_PROPERTY} is set,
	 * the student should have at least one enrolment within specified days.
	 */
	private void testEnrolledWithinDaysRestriction() {
		testDate.setTime(new Date());
		// set days range restriction(discount is available for students
		// enrolled within the last 100 days)
		discount.setStudentEnrolledWithinDays(100);
		// not eligible because student isn't enrolled to any class
		assertFalse(((RealDiscountsPolicy) discountPolicy).isStudentEligibile(student, discount));

		// add the reference to enrolment
		Enrolment enrolment = context.newObject(Enrolment.class);

		// enrolled 3 days ago
		testDate.add(Calendar.DATE, -3);
		enrolment.setCreated(testDate.getTime());
		enrolment.setStatus(EnrolmentStatus.SUCCESS);

		CourseClass courseClass = context.newObject(CourseClass.class);
		courseClass.setStartDate(testDate.getTime());
		enrolment.setCourseClass(courseClass);

		student.addToEnrolments(enrolment);
		// eligible, by the date of previous enrolments restriction
		assertTrue(((RealDiscountsPolicy) discountPolicy).isStudentEligibile(student, discount));
	}

	/**
	 * If the {@link _Discount#STUDENT_AGE_PROPERTY} is set and the
	 * {@link _Discount#STUDENT_AGE_OPERATOR_PROPERTY} is valid, then the
	 * student age should be valid by the defined restrictions.
	 */
	private void testStudentAgeRestriction() {
		testDate.setTime(new Date());
		// add the student age restriction
		discount.setStudentAge(20);
		// not eligible, because the student date of birth is null
		assertFalse(((RealDiscountsPolicy) discountPolicy).isStudentEligibile(student, discount));

		// 40-years old student
		testDate.add(Calendar.YEAR, -40);
		student.getContact().setDateOfBirth(testDate.getTime());
		// set some unknown age operator
		discount.setStudentAgeOperator("unknown");
		// not eligible, because such an operation for student age is not
		// defined, can't compare
		assertFalse(((RealDiscountsPolicy) discountPolicy).isStudentEligibile(student, discount));

		// set the "<" operation for student age
		discount.setStudentAgeOperator(RealDiscountsPolicy.AGE_UNDER);
		// not eligible, because student age is 40, and "40<20" expression is
		// incorrect
		assertFalse(((RealDiscountsPolicy) discountPolicy).isStudentEligibile(student, discount));

		// 15-years old student
		testDate.add(Calendar.YEAR, 25);
		student.getContact().setDateOfBirth(testDate.getTime());
		// eligible: 15<20
		assertTrue(((RealDiscountsPolicy) discountPolicy).isStudentEligibile(student, discount));

		// set age oprator to ">"
		discount.setStudentAgeOperator(RealDiscountsPolicy.AGE_OVER);
		// not eligible, because student age is 15, and "15>20" expression is
		// incorrect
		assertFalse(((RealDiscountsPolicy) discountPolicy).isStudentEligibile(student, discount));

		// 25-years old student
		testDate.add(Calendar.YEAR, -10);
		student.getContact().setDateOfBirth(testDate.getTime());
		// eligible: 25>20
		assertTrue(((RealDiscountsPolicy) discountPolicy).isStudentEligibile(student, discount));
	}

	/**
	 * If there are any concessions linked to discount, then the student has to
	 * be linked at least to one of there concessions and the correspondent
	 * {@link StudentConcession} entity should have the valid expire date.
	 */
	private void testConcessionsRestrictions() {
		// add the concession reference to the discount
		DiscountConcessionType discountConcessionType = context.newObject(DiscountConcessionType.class);
		ConcessionType concessionType = context.newObject(ConcessionType.class);
		discountConcessionType.setConcessionType(concessionType);
		discount.addToDiscountConcessionTypes(discountConcessionType);
		// not eligible, because the student doesn't have the reference to
		// concessionType
		assertFalse(((RealDiscountsPolicy) discountPolicy).isStudentEligibile(student, discount));

		// add the reference to concessionType1(which the student has) to the
		// discount
		DiscountConcessionType discountConcessionType1 = context.newObject(DiscountConcessionType.class);
		discountConcessionType1.setConcessionType(concessionType1);
		discount.addToDiscountConcessionTypes(discountConcessionType1);
		// eligible, both the student and discount have the refs to
		// concessionType1, which isn't restricted by date
		assertTrue(((RealDiscountsPolicy) discountPolicy).isStudentEligibile(student, discount));

		// set date restriction to the concessionType1
		concessionType1.setHasExpiryDate(true);
		// not eligible: the studentConcession hasn't expiry date
		assertFalse(((RealDiscountsPolicy) discountPolicy).isStudentEligibile(student, discount));

		// add the reference to concessionTypeForExpiredDateTest(which the
		// student contains, and
		// ref to which has already expired date) to the discount
		DiscountConcessionType discountConcessionTypeExpired = context.newObject(DiscountConcessionType.class);
		discountConcessionTypeExpired.setConcessionType(concessionTypeForExpiredDateTest);
		discount.addToDiscountConcessionTypes(discountConcessionTypeExpired);
		// not eligible, both the student and discount have the refs to
		// concessionType3, but studentConcession has incorrect expire date
		assertFalse(((RealDiscountsPolicy) discountPolicy).isStudentEligibile(student, discount));

		// add the reference to concessionType2(which the student contains, and
		// ref to which has valid expire date) to the discount
		DiscountConcessionType discountConcessionType2 = context.newObject(DiscountConcessionType.class);
		discountConcessionType2.setConcessionType(concessionType2);
		discount.addToDiscountConcessionTypes(discountConcessionType2);
		// eligible, both the student and discount have the refs to
		// concessionType2, correct expire date
		assertTrue(((RealDiscountsPolicy) discountPolicy).isStudentEligibile(student, discount));

	}

	/**
	 * If the {@link _Discount#STUDENT_POSTCODES_PROPERTY} id defined, the
	 * student's postcode should be contained in this property.
	 */
	private void testPostcodeRestrictions() {
		// add the postcode restrictions to discount
		discount.setStudentPostcodes("222222, 333333");
		// not eligible: student doesn't have postcode
		assertFalse(((RealDiscountsPolicy) discountPolicy).isStudentEligibile(student, discount));

		// set some postcode to student
		student.getContact().setPostcode("000000");
		// not eligible: discount doesn't contain such a postcode
		assertFalse(((RealDiscountsPolicy) discountPolicy).isStudentEligibile(student, discount));

		// set valid poscode to student
		student.getContact().setPostcode("222222");
		// eligible: student has valid postcode
		assertTrue(((RealDiscountsPolicy) discountPolicy).isStudentEligibile(student, discount));
	}

	/**
	 * Filters some discounts, retrieves eligible.
	 */
	@Test
	public void getApplicableByPolicySmokeTest() {
		discountPolicy = new RealDiscountsPolicy(promotions, student);
		List<Discount> applicableByPolicy = discountPolicy.getApplicableByPolicy(Arrays.asList(discount,
				combDiscountWithAmount, singleDiscountWithRate, combDiscountWithRateMax, singleDiscountWithRateMin, 
				hiddenDiscountWithAmount, nonAvailableDiscountWithAmount));
		assertFalse(applicableByPolicy.isEmpty());
		assertEquals(5, applicableByPolicy.size());
		assertEquals(discount, applicableByPolicy.get(0));
		assertEquals(combDiscountWithAmount, applicableByPolicy.get(1));
		assertEquals(singleDiscountWithRate, applicableByPolicy.get(2));
		assertEquals(singleDiscountWithRateMin, applicableByPolicy.get(3));
		// hidden discount should appear in real discounts list
		assertEquals(hiddenDiscountWithAmount, applicableByPolicy.get(4));
		// non available discount should not appear in real discounts list
		assertFalse(applicableByPolicy.contains(nonAvailableDiscountWithAmount));
	}

	/**
	 * The best variant for the given discounts:
	 * {@link RealDiscountsPolicyTest#discount}.
	 */
	@Test
	public void filterDiscountsSmokeTest() {
		discountPolicy = new RealDiscountsPolicy(promotions, student);
		List<Discount> filteredDiscounts = discountPolicy.filterDiscounts(Arrays.asList(discount,
				combDiscountWithAmount, singleDiscountWithRate, combDiscountWithRateMax, singleDiscountWithRateMin),
				FEE_EX_GST);
		assertFalse(filteredDiscounts.isEmpty());
		assertEquals(1, filteredDiscounts.size());
		assertEquals(discount, filteredDiscounts.get(0));
	}

}
