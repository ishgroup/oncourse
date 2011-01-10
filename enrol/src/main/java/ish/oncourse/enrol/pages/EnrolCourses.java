package ish.oncourse.enrol.pages;

import ish.common.types.PaymentSource;
import ish.math.Money;
import ish.oncourse.enrol.components.EnrolmentPaymentEntry;
import ish.oncourse.enrol.services.concessions.IConcessionsService;
import ish.oncourse.enrol.services.invoice.IInvoiceProcessingService;
import ish.oncourse.enrol.services.student.IStudentService;
import ish.oncourse.model.College;
import ish.oncourse.model.Contact;
import ish.oncourse.model.Course;
import ish.oncourse.model.CourseClass;
import ish.oncourse.model.Enrolment;
import ish.oncourse.model.EnrolmentStatus;
import ish.oncourse.model.Invoice;
import ish.oncourse.model.InvoiceLine;
import ish.oncourse.model.InvoiceStatus;
import ish.oncourse.model.PaymentIn;
import ish.oncourse.model.PaymentStatus;
import ish.oncourse.model.Student;
import ish.oncourse.model.services.persistence.ICayenneService;
import ish.oncourse.services.cookies.ICookiesService;
import ish.oncourse.services.courseclass.ICourseClassService;
import ish.oncourse.services.preference.IPreferenceService;
import ish.oncourse.services.site.IWebSiteService;

import java.math.BigDecimal;
import java.text.DecimalFormat;
import java.text.Format;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Date;
import java.util.List;

import org.apache.cayenne.ObjectContext;
import org.apache.cayenne.query.Ordering;
import org.apache.cayenne.query.SortOrder;
import org.apache.tapestry5.ajax.MultiZoneUpdate;
import org.apache.tapestry5.annotations.InjectComponent;
import org.apache.tapestry5.annotations.Parameter;
import org.apache.tapestry5.annotations.Persist;
import org.apache.tapestry5.annotations.Property;
import org.apache.tapestry5.annotations.SetupRender;
import org.apache.tapestry5.corelib.components.Zone;
import org.apache.tapestry5.ioc.annotations.Inject;
import org.apache.tapestry5.services.Request;

public class EnrolCourses {

	/**
	 * ish services
	 */
	@Inject
	private ICookiesService cookiesService;

	@Inject
	private ICourseClassService courseClassService;

	@Inject
	private IConcessionsService concessionsService;

	@Inject
	private IPreferenceService preferenceService;

	@Inject
	private IWebSiteService webSiteService;

	@Inject
	private ICayenneService cayenneService;

	@Inject
	private IStudentService studentService;

	@Inject
	private IInvoiceProcessingService invoiceProcessingService;
	/**
	 * tapestry services
	 */
	@Inject
	private Request request;

	@Property
	@Persist
	private List<CourseClass> classesToEnrol;

	@Property
	private CourseClass courseClass;

	@Persist
	private List<Contact> contacts;

	@Property
	private Contact contact;

	@Property
	private int studentIndex;

	@Property
	private int courseClassIndex;

	@Property
	@Parameter
	private boolean hadPreviousPaymentFailure;

	@Persist
	private Enrolment[][] enrolments;

	@Persist
	private InvoiceLine[][] invoiceLines;

	@Persist
	@Property
	private PaymentIn payment;

	@Persist
	@Property
	private Invoice invoice;

	@Property
	@Persist
	private ObjectContext context;

	@Property
	@Persist
	private Format moneyFormat;

	@InjectComponent
	@Property
	private Zone totals;

	@InjectComponent
	@Property
	private EnrolmentPaymentEntry paymentEntry;

	/**
	 * Initial setup of the EnrolCourses page. Retrieves all the shortlisted
	 * classes and students.<br/>
	 * If there're no shortlisted classes, the message for user to return to the
	 * courses list and order some is appeared. <br/>
	 * If there're no shortlisted students, the user adds it in the proposed
	 * form.<br/>
	 * When there exist both students and classes list, the payment-related
	 * entities are created: {@see EnrolCourses#initPayment()}.
	 */
	@SetupRender
	void beforeRender() {
		moneyFormat = new DecimalFormat("###,##0.00");
		context = cayenneService.newContext();

		String[] orderedClassesIds = cookiesService
				.getCookieCollectionValue(CourseClass.SHORTLIST_COOKEY_KEY);
		if (orderedClassesIds != null && orderedClassesIds.length != 0) {
			classesToEnrol = courseClassService.loadByIds(orderedClassesIds);
			List<Ordering> orderings = new ArrayList<Ordering>();
			orderings.add(new Ordering(CourseClass.COURSE_PROPERTY + "." + Course.CODE_PROPERTY,
					SortOrder.ASCENDING));
			orderings.add(new Ordering(CourseClass.CODE_PROPERTY, SortOrder.ASCENDING));
			Ordering.orderList(classesToEnrol, orderings);
		}

		contacts = studentService.getStudentsFromShortList();

		if (contacts != null && !contacts.isEmpty() && classesToEnrol != null) {
			initPayment();
		}

	}

	/**
	 * Creates and initializes the set of payment/enrolment-related entities:
	 * <ul>
	 * <li> {@link PaymentIn} - one payment entity is created for all the
	 * selected courses and contacts</li>
	 * <li> {@link Invoice} - one invoice entity is created for all the selected
	 * courses and contacts</li>
	 * <li> {@link Enrolment} - separate enrolment entity is created for the each
	 * element of the cartesian product of the selected courses and contacts</li>
	 * <li> {@link InvoiceLine} - the new entity is created for each enrolment if
	 * the class is enrolable, linked to the invoice entity.</li>
	 * </ul>
	 * 
	 */
	private void initPayment() {
		College currentCollege = webSiteService.getCurrentCollege();
		College college = (College) context.localObject(currentCollege.getObjectId(),
				currentCollege);
		payment = context.newObject(PaymentIn.class);
		payment.setStatus(PaymentStatus.PENDING);
		payment.setSource(PaymentSource.SOURCE_WEB);
		payment.setCollege(college);
		invoice = context.newObject(Invoice.class);
		// fill the invoice with default values
		invoice.setInvoiceDate(new Date());
		invoice.setAmountOwing(BigDecimal.ZERO);
		invoice.setDateDue(new Date());
		invoice.setStatus(InvoiceStatus.PENDING);

		invoice.setCollege(college);
		enrolments = new Enrolment[contacts.size()][classesToEnrol.size()];
		invoiceLines = new InvoiceLine[contacts.size()][classesToEnrol.size()];
		for (int i = 0; i < contacts.size(); i++) {
			for (int j = 0; j < classesToEnrol.size(); j++) {
				enrolments[i][j] = context.newObject(Enrolment.class);
				enrolments[i][j].setStatus(EnrolmentStatus.PENDING);
				enrolments[i][j].setSource(PaymentSource.SOURCE_WEB);

				enrolments[i][j].setCollege(college);
				Contact contact = contacts.get(i);
				Student student = ((Contact) context.localObject(contact.getObjectId(), contact))
						.getStudent();
				CourseClass courseClass = (CourseClass) context.localObject(classesToEnrol.get(j)
						.getObjectId(), classesToEnrol.get(j));

				enrolments[i][j].setStudent(student);
				enrolments[i][j].setCourseClass(courseClass);

				if (!enrolments[i][j].isDuplicated()
						&& courseClass.isHasAvailableEnrolmentPlaces()) {
					InvoiceLine invoiceLine = invoiceProcessingService
							.createInvoiceLineForEnrolment(enrolments[i][j]);
					invoiceLine.setInvoice(invoice);

					enrolments[i][j].setInvoiceLine(invoiceLine);
					invoiceLines[i][j] = invoiceLine;
				}
			}
		}
	}

	public boolean isShowConcessionsArea() {
		return (!concessionsService.getActiveConcessionTypes().isEmpty())
				&& Boolean.valueOf(preferenceService.getPreferenceByKey(
						"feature.concessionsInEnrolment").getValueString());
	}

	public String getCoursesListLink() {
		return "http://" + request.getServerName() + "/courses";
	}

	public boolean isHasDiscount() {
		Money result = Money.ZERO;
		for (int i = 0; i < contacts.size(); i++) {
			for (int j = 0; j < classesToEnrol.size(); j++) {
				InvoiceLine invoiceLine = enrolments[i][j].getInvoiceLine();
				if (invoiceLine != null) {
					result = result.add(invoiceLine.getDiscountTotalExTax());
				}
			}
		}
		return !result.isZero();
	}

	public Money getTotalDiscountAmountIncTax() {
		Money result = Money.ZERO;
		for (int i = 0; i < contacts.size(); i++) {
			for (int j = 0; j < classesToEnrol.size(); j++) {
				InvoiceLine invoiceLine = enrolments[i][j].getInvoiceLine();
				if (invoiceLine != null) {
					result = result.add(invoiceLine.getDiscountTotalIncTax());
				}
			}
		}
		return result;
	}

	public Money getTotalIncGst() {
		Money result = Money.ZERO;
		for (int i = 0; i < contacts.size(); i++) {
			for (int j = 0; j < classesToEnrol.size(); j++) {
				InvoiceLine invoiceLine = enrolments[i][j].getInvoiceLine();
				if (invoiceLine != null) {
					result = result.add(result.add(invoiceLine.getPriceTotalIncTax().subtract(
							invoiceLine.getDiscountTotalIncTax())));
				}
			}
		}
		return result;
	}

	/**
	 * Updates zones that contain info about payment amount
	 * 
	 * @return
	 */
	public Object enrolmentsUpdated() {
		return new MultiZoneUpdate("totals", totals.getBody()).add("paymentZone",
				paymentEntry.getPaymentZone());
	}

	public List<CourseClass> getCourseClasses() {
		return classesToEnrol;
	}

	public Enrolment[][] getEnrolments() {
		return enrolments;
	}

	public List<Contact> getContacts() {
		return contacts;
	}

	public void setContacts(List<Contact> contacts) {
		this.contacts = contacts;
	}

	public InvoiceLine[][] getInvoiceLines() {
		return invoiceLines;
	}

	public List<Enrolment> getEnrolmentsList() {
		List<Enrolment> result = new ArrayList<Enrolment>();

		for (Enrolment[] e : enrolments) {
			result.addAll(Arrays.asList(e));
		}

		return result;
	}
}
