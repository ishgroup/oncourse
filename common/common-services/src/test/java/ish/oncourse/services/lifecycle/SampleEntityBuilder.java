package ish.oncourse.services.lifecycle;

import ish.common.types.*;
import ish.math.Money;
import ish.oncourse.model.*;
import org.apache.cayenne.Cayenne;
import org.apache.cayenne.ObjectContext;

import java.math.BigDecimal;
import java.util.Date;

public class SampleEntityBuilder {

	private ObjectContext ctx;

	private SampleEntityBuilder(ObjectContext ctx) {
		this.ctx = ctx;
	}

	public static SampleEntityBuilder newBuilder(ObjectContext ctx) {
		return new SampleEntityBuilder(ctx);
	}

	public Enrolment createEnrolment(InvoiceLine invoiceLine, Student student, CourseClass courseClass) {
		Enrolment enrl = ctx.newObject(Enrolment.class);
		College college = Cayenne.objectForPK(ctx, College.class, 1l);
		enrl.setCollege(college);
		enrl.setReasonForStudy(1);
		enrl.setSource(PaymentSource.SOURCE_WEB);
		enrl.setStatus(EnrolmentStatus.SUCCESS);
		enrl.setStudent(student);
		enrl.setCourseClass(courseClass);
		invoiceLine.setEnrolment(enrl);
		return enrl;
	}

	public CourseClass createCourseClass(Course course) {
		CourseClass c = ctx.newObject(CourseClass.class);

		c.setCancelled(false);
		c.setCode("12345");
		c.setMaximumPlaces(3);

		College college = Cayenne.objectForPK(ctx, College.class, 1l);
		c.setCollege(college);

		c.setCountOfSessions(5);
		c.setCourse(course);

		c.setDetail("Test details");
		c.setFeeGst(new Money(123, 25));
		c.setFeeExGst(new Money(123, 25));
		c.setMaterials("Test materials");
		c.setMaximumPlaces(18);
		c.setIsDistantLearningCourse(false);

		return c;
	}

	public Course createCourse() {
		Course c = ctx.newObject(Course.class);

		c.setAllowWaitingList(true);
		c.setCode("123");

		College college = Cayenne.objectForPK(ctx, College.class, 1l);
		c.setCollege(college);

		c.setDetail("test details");
		c.setFieldOfEducation("computers skills");
		c.setName("computer literance");

		Qualification qualification = Cayenne.objectForPK(ctx, Qualification.class, 1l);
		c.setQualification(qualification);

		return c;
	}

	public InvoiceLine createInvoiceLine(Invoice invoice) {
		InvoiceLine invLine = ctx.newObject(InvoiceLine.class);

		College college = Cayenne.objectForPK(ctx, College.class, 1l);
		invLine.setCollege(college);

		invLine.setTitle("Test invoice line");
		invLine.setDescription("Test invoice line.");
		invLine.setDiscountEachExTax(new Money(123, 25));
		invLine.setInvoice(invoice);
		invLine.setPriceEachExTax(new Money(123, 25));
		invLine.setQuantity(new BigDecimal(5));
		invLine.setTaxEach(new Money(10, 1));
		invLine.setUnit("unit");

		return invLine;
	}

	public Invoice createInvoice(Contact contact) {
		Invoice inv = ctx.newObject(Invoice.class);

		inv.setAmountOwing(new Money("10"));

		College college = Cayenne.objectForPK(ctx, College.class, 1l);
		WebSite webSite = Cayenne.objectForPK(ctx, WebSite.class, 1l);
		inv.setCollege(college);

		inv.setContact(contact);
		inv.setCustomerReference("customer reference");
		inv.setDateDue(new Date());
		inv.setDescription("test description");
		inv.setInvoiceDate(new Date());
		inv.setPublicNotes("test public notes");
		inv.setShippingAddress("test shipping address");
		inv.setSource(PaymentSource.SOURCE_WEB);
		inv.setTotalGst(new Money("25"));
		inv.setTotalExGst(new Money("20"));
		inv.setWebSite(webSite);

		return inv;
	}

	public Student createStudent(Contact contact) {
		Student st = ctx.newObject(Student.class);

		College college = Cayenne.objectForPK(ctx, College.class, 1l);
		st.setCollege(college);

		st.setContact(contact);
		Country country = Cayenne.objectForPK(ctx, Country.class, 1l);
		st.setCountryOfBirth(country);
		st.setDisabilityType(AvetmissStudentDisabilityType.HEARING);
		st.setEnglishProficiency(AvetmissStudentEnglishProficiency.VERY_WELL);
		st.setHighestSchoolLevel(AvetmissStudentSchoolLevel.COMPLETED_YEAR_11);
		st.setIndigenousStatus(AvetmissStudentIndigenousStatus.ABORIGINAL);
		st.setIsOverseasClient(true);
		st.setIsStillAtSchool(true);
		st.setLabourForceType(1);

		Language lang = Cayenne.objectForPK(ctx, Language.class, 2l);
		st.setLanguageHome(lang);
		st.setLanguage(lang);

		st.setPriorEducationCode(AvetmissStudentPriorEducation.MISC);
		st.setYearSchoolCompleted(5);

		return st;
	}

	public Contact createContact() {
		Contact c = ctx.newObject(Contact.class);

		c.setBusinessPhoneNumber("724-556");

		College college = Cayenne.objectForPK(ctx, College.class, 1l);
		c.setCollege(college);

		c.setCookieHash("12345");

		Country country = Cayenne.objectForPK(ctx, Country.class, 1l);
		c.setCountry(country);

		c.setDateOfBirth(new Date());
		c.setEmailAddress("test@gmail.com");
		c.setFamilyName("Putin");
		c.setFaxNumber("777-771");
		c.setGivenName("Vladimir");
		c.setHomePhoneNumber("777-771");
		c.setIsCompany(true);
		c.setIsMale(true);
		c.setIsMarketingViaEmailAllowed(true);
		c.setIsMarketingViaPostAllowed(true);
		c.setIsMarketingViaSMSAllowed(true);
		c.setMobilePhoneNumber("777-771");
		c.setPassword("pwd23%4");
		c.setPasswordHash("pwd23%4");
		c.setPostcode("777AB");
		c.setState("NSW");
		c.setStreet("30 Avoca st.");
		c.setSuburb("Randwick");

		return c;
	}
}
