package ish.oncourse.enrol.pages;

import ish.common.types.PaymentSource;
import ish.math.Money;
import ish.oncourse.enrol.components.EnrolmentPaymentEntry;
import ish.oncourse.enrol.components.EnrolmentPaymentProcessing;
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
import org.apache.cayenne.PersistenceState;
import org.apache.cayenne.query.Ordering;
import org.apache.cayenne.query.SortOrder;
import org.apache.tapestry5.ajax.MultiZoneUpdate;
import org.apache.tapestry5.annotations.CleanupRender;
import org.apache.tapestry5.annotations.InjectComponent;
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
	private boolean hadPreviousPaymentFailure;

	/**
	 * Indicates if this page is used for displaying the enrolment checkout(if
	 * false), and the result of previous chechout otherwise.
	 */
	@Persist
	private boolean pageResult;

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

	@InjectComponent
	@Property
	private EnrolmentPaymentProcessing resultComponent;

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
		if (payment != null) {
			payment = null;
		}
		// No need to create the entities if the page is used just to display
		// the checkout result, or if the payment processing is disabled at all
		if (!isPageResult() && isPaymentGatewayEnabled()) {
			moneyFormat = new DecimalFormat("###,##0.00");
			if (context == null) {
				context = cayenneService.newContext();
			}

			initClassesToEnrol();

			if (contacts == null || contacts.isEmpty()) {
				contacts = studentService.getStudentsFromShortList();
			}
			if (!contacts.isEmpty() && classesToEnrol != null) {
				// init the payment and the related entities only if there exist
				// shortlisted classes and students
				initPayment();
			}
		}
	}

	/**
	 * Initializes the {@link #classesToEnrol} list. Checks if the
	 * {@link #classesToEnrol} needs to be filled(refilled) or should be null:
	 * <ul>
	 * <li>if there no any shortlisted classes, the {@link #classesToEnrol} is
	 * set with null.</li>
	 * <li>if the {@link #classesToEnrol} is already filled, then it checks if
	 * the shortlisted classes hasn't been changed, and if they hasn't, refill
	 * the {@link #classesToEnrol} with the new values.</li>
	 * </ul>
	 */
	public void initClassesToEnrol() {
		String[] orderedClassesIds = cookiesService
				.getCookieCollectionValue(CourseClass.SHORTLIST_COOKEY_KEY);
		boolean shouldCreateNewClasses = orderedClassesIds != null;
		if (!shouldCreateNewClasses) {
			// if there no any shortlisted classes, the list is set with null.
			classesToEnrol = null;
			return;
		}

		// checks if the list is already filled
		shouldCreateNewClasses = shouldCreateNewClasses && classesToEnrol == null;

		if (!shouldCreateNewClasses) {
			// checks id the list of shortlisted classes hasn't been changed
			if (orderedClassesIds.length != classesToEnrol.size()) {
				// if the sizes are not equal, the shortlisted classes list has
				// been changed and we need to refill the list.
				shouldCreateNewClasses = true;
			} else {
				// checks if the both lists contains the same classes: if the
				// any element from one of lists isn't contained in another
				// list(assuming that sizes of lists are equal), then the lists
				// don't contain the same classes
				List<String> idsList = Arrays.asList(orderedClassesIds);
				for (CourseClass courseClass : classesToEnrol) {
					if (!idsList.contains(courseClass.getId().toString())) {
						shouldCreateNewClasses = true;
						break;
					}
				}
			}
		}

		// if the list should be filled or refilled
		if (shouldCreateNewClasses) {
			classesToEnrol = courseClassService.loadByIds(orderedClassesIds);
			List<Ordering> orderings = new ArrayList<Ordering>();
			orderings.add(new Ordering(CourseClass.COURSE_PROPERTY + "." + Course.CODE_PROPERTY,
					SortOrder.ASCENDING));
			orderings.add(new Ordering(CourseClass.CODE_PROPERTY, SortOrder.ASCENDING));
			Ordering.orderList(classesToEnrol, orderings);
		}
	}

	@CleanupRender
	void cleanupRender() {
		pageResult = false;
	}

	/**
	 * Clears all the properties with the @Persist annotation.
	 */
	public void clearPersistedProperties() {
		classesToEnrol = null;
		contacts = null;
		enrolments = null;
		invoiceLines = null;
		payment = null;
		invoice = null;
		context = null;
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
		if (invoice == null) {
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
					enrolments[i][j] = createEnrolment(classesToEnrol.get(j), contacts.get(i));
					invoiceLines[i][j] = enrolments[i][j].getInvoiceLine();
				}
			}
		} else {
			for (int i = 0; i < contacts.size(); i++) {
				for (int j = 0; j < classesToEnrol.size(); j++) {
					if (enrolments[i][j].getPersistenceState() == PersistenceState.TRANSIENT) {
						enrolments[i][j] = createEnrolment(classesToEnrol.get(j), contacts.get(i));
						invoiceLines[i][j] = enrolments[i][j].getInvoiceLine();
					}
				}
			}
		}
	}

	public Enrolment createEnrolment(CourseClass courseClass, Contact contact) {
		Enrolment enrolment = context.newObject(Enrolment.class);
		enrolment.setStatus(EnrolmentStatus.PENDING);
		enrolment.setSource(PaymentSource.SOURCE_WEB);

		Student student = ((Contact) context.localObject(contact.getObjectId(), contact))
				.getStudent();

		enrolment.setCollege(student.getCollege());
		enrolment.setStudent(student);
		enrolment.setCourseClass((CourseClass) context.localObject(courseClass.getObjectId(),
				courseClass));

		if (!enrolment.isDuplicated() && courseClass.isHasAvailableEnrolmentPlaces()) {
			InvoiceLine invoiceLine = invoiceProcessingService
					.createInvoiceLineForEnrolment(enrolment);
			invoiceLine.setInvoice(invoice);

			enrolment.setInvoiceLine(invoiceLine);
		}
		return enrolment;
	}

	/**
	 * Checks if the payment gateway processing is enabled for the current
	 * college. If not, the enrolling is impossible.
	 * 
	 * @return true if payment gateway is enabled.
	 */
	public boolean isPaymentGatewayEnabled() {
		return webSiteService.getCurrentCollege().isPaymentGatewayEnabled();
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

	/**
	 * Returns the embedded {@link EnrolmentPaymentProcessing} component for
	 * displaying the checkout results.
	 * 
	 * @return
	 */
	public EnrolmentPaymentProcessing getResultingElement() {
		return resultComponent;
	}

	/**
	 * Sets value to the {@link #pageResult}.
	 * 
	 * @param isResult
	 *            .
	 */
	public void setPageResult(boolean pageResult) {
		this.pageResult = pageResult;
	}

	/**
	 * @return the pageResult
	 */
	public boolean isPageResult() {
		return pageResult;
	}

}
