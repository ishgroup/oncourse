package ish.oncourse.model;

import static org.junit.Assert.assertNotNull;
import ish.common.types.PaymentSource;
import ish.common.types.PaymentStatus;
import ish.math.Money;
import ish.oncourse.test.ContextUtils;

import java.math.BigDecimal;
import java.util.Calendar;
import java.util.Date;

import org.apache.cayenne.ObjectContext;
import org.junit.Before;
import org.junit.Test;

public class PaymentInTest {

	private ObjectContext context;
	private Calendar calendar;
	private College college;
	private Course course;	
	private CourseClass courseClass;

	@Before
	public void setup() throws Exception {
		/*
		ContextUtils.setupDataSources();
		this.context = ContextUtils.createObjectContext();
		this.calendar = Calendar.getInstance();
		
		this.college = context.newObject(College.class);
		
		college.setName("name");
		college.setTimeZone("Australia/Sydney");
		college.setFirstRemoteAuthentication(new Date());
		
		context.commitChanges();
		
		this.course = context.newObject(Course.class);
		course.setCollege(college);
		
		this.courseClass = context.newObject(CourseClass.class);
		courseClass.setCourse(course);
		courseClass.setCollege(college);
		courseClass.setMaximumPlaces(3);
		
		context.commitChanges();*/
	}

	@Test
	public void testAbandonPayment() throws Exception {
		
		/*
		
		PaymentIn paymentIn = context.newObject(PaymentIn.class);
		paymentIn.setCollege(college);
		paymentIn.setStatus(PaymentStatus.IN_TRANSACTION);
		paymentIn.setAmount(new BigDecimal(150));
		paymentIn.setSource(PaymentSource.SOURCE_ONCOURSE);
		
		calendar.add(Calendar.DAY_OF_MONTH, 5);
		
		Contact contact = (Contact) context.newObject(Contact.class);
		contact.setGivenName("Test_Payer");
		contact.setFamilyName("Test_Payer");
		contact.setCollege(college);
		
		Invoice invoice1 = context.newObject(Invoice.class);
		invoice1.setAngelId(100l);
		invoice1.setAmountOwing(new BigDecimal(-75));
		invoice1.setInvoiceNumber(100l);
		invoice1.setCollege(college);
		invoice1.setInvoiceDate(calendar.getTime());
		invoice1.setTotalExGst(new BigDecimal(-75));
		invoice1.setTotalGst(new BigDecimal(-75));
		invoice1.setStatus(InvoiceStatus.PENDING);
		invoice1.setDateDue(calendar.getTime());
		invoice1.setContact(contact);
		
		
		Invoice invoice2 = context.newObject(Invoice.class);
		invoice2.setAngelId(102l);
		invoice2.setAmountOwing(new BigDecimal(225));
		invoice2.setCollege(college);
		invoice2.setInvoiceNumber(101l);
		invoice2.setInvoiceDate(calendar.getTime());
		invoice2.setTotalExGst(new BigDecimal(225));
		invoice2.setTotalGst(new BigDecimal(225));
		invoice2.setStatus(InvoiceStatus.PENDING);
		invoice2.setDateDue(calendar.getTime());
		invoice2.setContact(contact);

		InvoiceLine invLine1 = context.newObject(InvoiceLine.class);
		invLine1.setTitle("Test_invLine1");
		invLine1.setCollege(college);
		invLine1.setPriceEachExTax(new Money(new BigDecimal(-75)));
		invLine1.setTaxEach(new Money(new BigDecimal(0)));
		invLine1.setQuantity(new BigDecimal(1));
		invLine1.setDiscountEachExTax(new Money(new BigDecimal(0)));

		InvoiceLine invLine2 = context.newObject(InvoiceLine.class);
		invLine2.setTitle("Test_invLine2");
		invLine2.setCollege(college);
		invLine2.setEnrolment(newEnrolment());
		invLine2.setPriceEachExTax(new Money(new BigDecimal(220)));
		invLine2.setTaxEach(new Money(new BigDecimal(0)));
		invLine2.setQuantity(new BigDecimal(1));
		invLine2.setDiscountEachExTax(new Money(new BigDecimal(0)));

		invoice2.addToInvoiceLines(invLine1);
		invoice2.addToInvoiceLines(invLine2);

		PaymentInLine pLine1 = context.newObject(PaymentInLine.class);
		pLine1.setAmount(new BigDecimal(-75));
		pLine1.setCollege(college);
		pLine1.setInvoice(invoice1);

		PaymentInLine pLine2 = context.newObject(PaymentInLine.class);
		pLine2.setAmount(new BigDecimal(225));
		pLine2.setCollege(college);
		pLine2.setInvoice(invoice2);

		paymentIn.addToPaymentInLines(pLine1);
		paymentIn.addToPaymentInLines(pLine2);
		
		context.commitChanges();
		
		PaymentIn inversePayment = paymentIn.abandonPayment();
		
		assertNotNull("Expecting not null inverse payment.", inversePayment);
		
		context.commitChanges(); */
	}

	private Enrolment newEnrolment() {
		
		Enrolment enrol = new Enrolment();
		enrol.setCourseClass(courseClass);
		enrol.setStatus(EnrolmentStatus.IN_TRANSACTION);
		enrol.setCollege(college);
		
		Student student = (Student) context.newObject(Student.class);
		student.setCollege(college);
		
		Contact contact = (Contact) context.newObject(Contact.class);
		contact.setCollege(college);
		contact.setGivenName("Test_CourseClass");
		contact.setFamilyName("Test_CourseClass");
		
		student.setContact(contact);
		
		enrol.setStudent(student);
		
		return enrol;
	}
}
