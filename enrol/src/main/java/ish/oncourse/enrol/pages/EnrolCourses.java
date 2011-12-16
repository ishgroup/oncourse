package ish.oncourse.enrol.pages;

import ish.common.types.PaymentSource;
import ish.common.types.PaymentStatus;
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
import ish.oncourse.model.DiscountConcessionType;
import ish.oncourse.model.DiscountCourseClass;
import ish.oncourse.model.Enrolment;
import ish.oncourse.model.EnrolmentStatus;
import ish.oncourse.model.Invoice;
import ish.oncourse.model.InvoiceLine;
import ish.oncourse.model.InvoiceStatus;
import ish.oncourse.model.PaymentIn;
import ish.oncourse.model.Student;
import ish.oncourse.model.StudentConcession;
import ish.oncourse.services.cookies.ICookiesService;
import ish.oncourse.services.courseclass.ICourseClassService;
import ish.oncourse.services.persistence.ICayenneService;
//import ish.oncourse.services.preference.PreferenceController;
import ish.oncourse.services.site.IWebSiteService;
import ish.oncourse.ui.utils.FormatUtils;

import java.math.BigDecimal;
import java.text.Format;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.servlet.http.HttpSession;

import org.apache.cayenne.ObjectContext;
import org.apache.cayenne.PersistenceState;
import org.apache.cayenne.exp.Expression;
import org.apache.cayenne.exp.ExpressionFactory;
import org.apache.cayenne.query.Ordering;
import org.apache.cayenne.query.SortOrder;
import org.apache.tapestry5.ComponentResources;
import org.apache.tapestry5.StreamResponse;
import org.apache.tapestry5.annotations.CleanupRender;
import org.apache.tapestry5.annotations.InjectComponent;
import org.apache.tapestry5.annotations.Persist;
import org.apache.tapestry5.annotations.Property;
import org.apache.tapestry5.annotations.SetupRender;
import org.apache.tapestry5.corelib.components.Zone;
import org.apache.tapestry5.ioc.annotations.Inject;
//import org.apache.tapestry5.ioc.annotations.InjectService;
import org.apache.tapestry5.json.JSONObject;
import org.apache.tapestry5.services.Request;
import org.apache.tapestry5.services.RequestGlobals;
import org.apache.tapestry5.services.Session;
import org.apache.tapestry5.util.TextStreamResponse;

public class EnrolCourses {

	private static final String INDEX_SEPARATOR = "_";

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

	@Inject
	private RequestGlobals requestGlobals;

	@Inject
	private ComponentResources componentResources;

	@Property
	@Persist
	private List<CourseClass> classesToEnrol;

	@Persist
	private List<Contact> contacts;

	@SuppressWarnings("unused")
	@Property
	private Contact contact;

	@SuppressWarnings("unused")
	@Property
	private int studentIndex;

	@SuppressWarnings("unused")
	@Property
	private boolean hadPreviousPaymentFailure;

	/**
	 * Indicates if this page is used for displaying the enrolment checkout(if
	 * false), and the result of previous chechout otherwise.
	 */
	@Persist
	private boolean checkoutResult;

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

	@SuppressWarnings("unused")
	@Property
	@Persist
	private Format moneyFormat;

	@SuppressWarnings("unused")
	@InjectComponent
	@Property
	private Zone totals;

	@SuppressWarnings("unused")
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

		Session session = request.getSession(true);
		if (Boolean.TRUE.equals(session.getAttribute(PaymentIn.PAYMENT_PROCESSED_PARAM))) {
			checkoutResult = true;
		}
		// No need to create the entities if the page is used just to display
		// the checkout result, or if the payment processing is disabled at all
		if (!isCheckoutResult() && isPaymentGatewayEnabled()) {

			if (context == null) {
				context = cayenneService.newContext();
			}

			initClassesToEnrol();
			if (classesToEnrol != null) {
				contacts = studentService.getStudentsFromShortList();

				if (!contacts.isEmpty()) {
					// init the payment and the related entities only if there
					// exist
					// shortlisted classes and students
					initPayment();
				}
			} else {
				// if there no shortlisted classes, send user to select them
				clearPersistedProperties();
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
		List<Long> orderedClassesIds = cookiesService.getCookieCollectionValue(CourseClass.SHORTLIST_COOKIE_KEY,
				Long.class);
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
			if (orderedClassesIds.size() != classesToEnrol.size()) {
				// if the sizes are not equal, the shortlisted classes list has
				// been changed and we need to refill the list.
				shouldCreateNewClasses = true;
			} else {
				// checks if the both lists contains the same classes: if the
				// any element from one of lists isn't contained in another
				// list(assuming that sizes of lists are equal), then the lists
				// don't contain the same classes
				for (CourseClass courseClass : classesToEnrol) {
					if (!orderedClassesIds.contains(courseClass.getId())) {
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
			orderings.add(new Ordering(CourseClass.COURSE_PROPERTY + "." + Course.CODE_PROPERTY, SortOrder.ASCENDING));
			orderings.add(new Ordering(CourseClass.CODE_PROPERTY, SortOrder.ASCENDING));
			Ordering.orderList(classesToEnrol, orderings);
		}
	}

	@CleanupRender
	void cleanupRender() {
		checkoutResult = false;
	}

	/**
	 * Clears all the properties with the @Persist annotation.
	 */
	public void clearPersistedProperties() {
		componentResources.discardPersistentFieldChanges();
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
		College college = (College) context.localObject(currentCollege.getObjectId(), currentCollege);

		if (payment == null || payment.getStatus() == PaymentStatus.FAILED
				|| payment.getStatus() == PaymentStatus.FAILED_CARD_DECLINED) {

			payment = context.newObject(PaymentIn.class);
			payment.setStatus(PaymentStatus.NEW);
			payment.setSource(PaymentSource.SOURCE_WEB);
			payment.setCollege(college);

			HttpSession httpSession = requestGlobals.getHTTPServletRequest().getSession(true);

			String sessionId = httpSession.getId();
			payment.setSessionId(sessionId);

			PaymentIn failedPayment = null;
			Session session = request.getSession(false);
			if (session != null) {
				failedPayment = (PaymentIn) session.getAttribute("failedPayment");
			}
			if (failedPayment != null) {
				hadPreviousPaymentFailure = true;
				payment.setCreditCardCVV(failedPayment.getCreditCardCVV());
				payment.setCreditCardExpiry(failedPayment.getCreditCardExpiry());
				payment.setCreditCardName(failedPayment.getCreditCardName());
				payment.setCreditCardNumber(failedPayment.getCreditCardNumber());
				payment.setCreditCardType(failedPayment.getCreditCardType());
				session.setAttribute("failedPayment", null);
			}

		}
		if (invoice == null) {
			invoice = context.newObject(Invoice.class);
			// fill the invoice with default values
			invoice.setInvoiceDate(new Date());
			invoice.setAmountOwing(BigDecimal.ZERO);
			invoice.setDateDue(new Date());
			invoice.setStatus(InvoiceStatus.PENDING);
			invoice.setSource(PaymentSource.SOURCE_WEB);
			invoice.setCollege(college);
		}

		initEnrolments();
	}

	/**
	 * Checks the newly inited classes and contacts, init {@link #enrolments}
	 * properly: create new ones or use created previously if they are correct.
	 */
	public void initEnrolments() {
		Enrolment[][] enrolments = new Enrolment[contacts.size()][classesToEnrol.size()];

		InvoiceLine[][] invoiceLines = new InvoiceLine[contacts.size()][classesToEnrol.size()];
		Map<Enrolment, String> currentEnrolmentsMap = getEnrolmentsIndexesMap();
		List<Enrolment> currentEnrolments = new ArrayList<Enrolment>(currentEnrolmentsMap.keySet());
		// Checks the current contacts and classes to create proper enrolments
		for (int i = 0; i < contacts.size(); i++) {
			for (int j = 0; j < classesToEnrol.size(); j++) {
				Enrolment enrolmentToAdd = null;
				InvoiceLine invoiceLineToAdd = null;
				Enrolment existingEnrolment = null;
				Student student = ((Contact) context.localObject(contacts.get(i).getObjectId(), contacts.get(i)))
						.getStudent();
				CourseClass courseClass = (CourseClass) context.localObject(classesToEnrol.get(j).getObjectId(),
						classesToEnrol.get(j));
				if (!currentEnrolments.isEmpty()) {
					// checks if the enrolment with such a class and student is
					// already created
					Expression sameStudentAndClass = ExpressionFactory.matchExp(Enrolment.STUDENT_PROPERTY, student)
							.andExp(ExpressionFactory.matchExp(Enrolment.COURSE_CLASS_PROPERTY, courseClass));
					List<Enrolment> sameStudentAndClassResult = sameStudentAndClass.filterObjects(currentEnrolments);
					if (!sameStudentAndClassResult.isEmpty()) {
						existingEnrolment = sameStudentAndClassResult.get(0);
					}
				}
				if (existingEnrolment == null || existingEnrolment.getPersistenceState() == PersistenceState.TRANSIENT) {
					// create new enrolment if it doen't exist or has been
					// deleted
					enrolmentToAdd = createEnrolment(courseClass, student);
					invoiceLineToAdd = enrolmentToAdd.getInvoiceLine();
				} else {
					// use previously created enrolment
					enrolmentToAdd = existingEnrolment;
					// the invoiceLine could be null because of unticked
					// enrolment, use the corresponded from existing array
					String[] index = currentEnrolmentsMap.get(enrolmentToAdd).split(INDEX_SEPARATOR);
					invoiceLineToAdd = this.invoiceLines[Integer.parseInt(index[0])][Integer.parseInt(index[1])];
					if (invoiceLineToAdd != null) {
						// recalculate discounts that possibly have changed
						invoiceProcessingService.setupDiscounts(enrolmentToAdd, invoiceLineToAdd);
					}
				}

				enrolments[i][j] = enrolmentToAdd;
				invoiceLines[i][j] = invoiceLineToAdd;
			}
		}
		this.enrolments = enrolments;
		this.invoiceLines = invoiceLines;
	}

	/**
	 * Creates the new {@link Enrolment} entity for the given courseClass and
	 * Student.
	 * 
	 * @param courseClass
	 * @param student
	 * @return
	 */
	public Enrolment createEnrolment(CourseClass courseClass, Student student) {
		Enrolment enrolment = context.newObject(Enrolment.class);
		enrolment.setStatus(EnrolmentStatus.PENDING);
		enrolment.setSource(PaymentSource.SOURCE_WEB);

		enrolment.setCollege(student.getCollege());
		enrolment.setStudent(student);
		enrolment.setCourseClass(courseClass);

		if (!enrolment.isDuplicated() && courseClass.isHasAvailableEnrolmentPlaces() && !courseClass.hasEnded()) {
			InvoiceLine invoiceLine = invoiceProcessingService.createInvoiceLineForEnrolment(enrolment);
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
		return concessionsService.hasActiveConcessionTypes();
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
		moneyFormat = FormatUtils.chooseMoneyFormat(result);
		return result;
	}

	public Money getTotalIncGst() {
		Money result = Money.ZERO;
		for (int i = 0; i < contacts.size(); i++) {
			for (int j = 0; j < classesToEnrol.size(); j++) {
				InvoiceLine invoiceLine = enrolments[i][j].getInvoiceLine();
				if (invoiceLine != null) {
					result = result.add(invoiceLine.getPriceTotalIncTax()
							.subtract(invoiceLine.getDiscountTotalIncTax()));
				}
			}
		}
		moneyFormat = FormatUtils.chooseMoneyFormat(result);
		return result;
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

		if (enrolments != null) {
			for (Enrolment[] e : enrolments) {
				if (e != null) {
					result.addAll(Arrays.asList(e));
				}
			}
		}

		return result;
	}

	public Map<Enrolment, String> getEnrolmentsIndexesMap() {
		Map<Enrolment, String> result = new HashMap<Enrolment, String>();
		if (enrolments != null) {
			for (int i = 0; i < enrolments.length; i++) {
				for (int j = 0; j < enrolments[0].length; j++) {
					result.put(enrolments[i][j], i + INDEX_SEPARATOR + j);
				}
			}
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
	 * Sets value to the {@link #checkoutResult}.
	 * 
	 * @param checkoutResult
	 *            .
	 */
	public void setCheckoutResult(boolean checkoutResult) {
		this.checkoutResult = checkoutResult;
	}

	/**
	 * @return the checkoutResult
	 */
	public boolean isCheckoutResult() {
		return checkoutResult;
	}

	// TODO port this method to some service(it is a part of
	// DiscountService#isStudentElifible)
	public boolean hasSuitableClasses(StudentConcession studentConcession) {
		for (CourseClass cc : classesToEnrol) {
			for (DiscountCourseClass dcc : cc.getDiscountCourseClasses()) {
				for (DiscountConcessionType dct : dcc.getDiscount().getDiscountConcessionTypes()) {
					if (studentConcession.getConcessionType().getId().equals(dct.getConcessionType().getId())) {
						if (!Boolean.TRUE.equals(studentConcession.getConcessionType().getHasExpiryDate())
								|| (studentConcession.getExpiresOn() != null && studentConcession.getExpiresOn().after(
										new Date()))) {
							return true;
						}
					}

				}
			}
		}
		return false;

	}

	public StreamResponse onActionFromCheckSession() {
		JSONObject obj = new JSONObject();
		if (request.getSession(false) == null) {
			obj.put("status", "session timeout");
		} else {
			obj.put("status", "session alive");
		}
		return new TextStreamResponse("text/json", obj.toString());
	}
}
