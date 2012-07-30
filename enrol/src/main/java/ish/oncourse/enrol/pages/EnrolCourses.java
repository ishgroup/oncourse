package ish.oncourse.enrol.pages;

import ish.common.types.EnrolmentStatus;
import ish.common.types.PaymentSource;
import ish.common.types.PaymentStatus;
import ish.math.Money;
import ish.oncourse.enrol.components.EnrolmentPaymentEntry;
import ish.oncourse.enrol.components.EnrolmentPaymentProcessing;
import ish.oncourse.enrol.services.concessions.IConcessionsService;
import ish.oncourse.enrol.services.invoice.IInvoiceProcessingService;
import ish.oncourse.enrol.services.student.IStudentService;
import ish.oncourse.enrol.utils.EnrolmentValidationUtil;
import ish.oncourse.model.*;
import ish.oncourse.services.cookies.ICookiesService;
import ish.oncourse.services.courseclass.ICourseClassService;
import ish.oncourse.services.discount.IDiscountService;
import ish.oncourse.services.paymentexpress.IPaymentGatewayService;
import ish.oncourse.services.persistence.ICayenneService;
import ish.oncourse.services.preference.PreferenceController;
import ish.oncourse.services.site.IWebSiteService;
import ish.oncourse.util.FormatUtils;
import ish.util.InvoiceUtil;
import org.apache.cayenne.ObjectContext;
import org.apache.cayenne.PersistenceState;
import org.apache.cayenne.exp.Expression;
import org.apache.cayenne.exp.ExpressionFactory;
import org.apache.cayenne.query.Ordering;
import org.apache.cayenne.query.SortOrder;
import org.apache.log4j.Logger;
import org.apache.tapestry5.ComponentResources;
import org.apache.tapestry5.StreamResponse;
import org.apache.tapestry5.annotations.*;
import org.apache.tapestry5.corelib.components.Zone;
import org.apache.tapestry5.ioc.annotations.Inject;
import org.apache.tapestry5.json.JSONObject;
import org.apache.tapestry5.services.Request;
import org.apache.tapestry5.services.RequestGlobals;
import org.apache.tapestry5.services.Session;
import org.apache.tapestry5.util.TextStreamResponse;

import java.math.BigDecimal;
import java.text.Format;
import java.util.*;

public class EnrolCourses {

    private static final Logger LOGGER = Logger.getLogger(EnrolCourses.class);

    private static final String INDEX_SEPARATOR = "_";
    public static final String HTTP_PROTOCOL = "http://";


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

    @Inject
    private IDiscountService discountService;

    @Inject
    private IPaymentGatewayService paymentGatewayService;

    /**
     * tapestry services
     */
    @Inject
    private Request request;

    @SuppressWarnings("all")
	@Inject
    private RequestGlobals requestGlobals;

    @Inject
    private ComponentResources componentResources;

    @Inject
    private PreferenceController preferenceController;

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

    @Persist
    @Property
    private PaymentIn failedPayment;


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

        synchronized (this)
        {
            if (context == null)
            {
                context = cayenneService.newContext();
            }
        }

        // No need to create the entities if the page is used just to display
        // the checkout result, or if the payment processing is disabled at all
        synchronized (context)
        {
            if (!isCheckoutResult() && isPaymentGatewayEnabled()) {

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

        List<Enrolment> enrolments = getEnrolmentsList();
        deleteNotUsedEnrolments(enrolments, orderedClassesIds);

        if (orderedClassesIds == null || orderedClassesIds.size() < 1)
        {
            classesToEnrol = null;
            return;
        }

        if (shouldReloadClassesToEnrol(orderedClassesIds))
        {
            classesToEnrol = courseClassService.loadByIds(orderedClassesIds);
            List<Ordering> orderings = new ArrayList<Ordering>();
            orderings.add(new Ordering(CourseClass.COURSE_PROPERTY + "." + Course.CODE_PROPERTY, SortOrder.ASCENDING));
            orderings.add(new Ordering(CourseClass.CODE_PROPERTY, SortOrder.ASCENDING));
            Ordering.orderList(classesToEnrol, orderings);
        }
    }

    /**
     * The method returns true if orderedClassesIds and classesToEnrol doesn't contain the same classes.
     * @param orderedClassesIds
     * @return
     */

    private boolean shouldReloadClassesToEnrol(List<Long> orderedClassesIds)
    {
        if (classesToEnrol == null)
            return true;
        if (orderedClassesIds.size() != classesToEnrol.size())
            return true;
        for (CourseClass courseClass: classesToEnrol)
        {
            if (!orderedClassesIds.contains(courseClass.getId()))
            {
                return true;
            }
        }
        return false;
    }

    /**
     * The method deletes existed in-transaction enrolments  if the orderedClassesIds does not contains classes for these enrollments
     *
     * @param orderedClassesIds
     */
    private void deleteNotUsedEnrolments(List<Enrolment> enrolments, List<Long> orderedClassesIds) {

        List<Enrolment> enrolmentsToDelete = new ArrayList<Enrolment>();
        List<InvoiceLine> invoiceLinesToDelete = new ArrayList<InvoiceLine>();
        if (enrolments != null && !enrolments.isEmpty())
        {
            for (Enrolment enrolment:enrolments)
            {
                if (orderedClassesIds == null ||
                        orderedClassesIds.size() < 1 ||
                        !orderedClassesIds.contains(enrolment.getCourseClass().getId()))
                {
                    /**
                     * We can delete only IN_TRANSACTION enrollments
                     */
                    if (enrolment.getStatus() == EnrolmentStatus.IN_TRANSACTION)
                    {
                        enrolmentsToDelete.add(enrolment);
                        InvoiceLine invoiceLine = enrolment.getInvoiceLine();
                        if (invoiceLine != null)
                        {
                            invoiceLinesToDelete.add(enrolment.getInvoiceLine());
                        }
                    }
                    else
                    {
                        String message = String.format("State of enrollment with id=%d is %s !", enrolment.getId(), enrolment.getStatus());
                        LOGGER.error(message);
                        throw new IllegalArgumentException(message);
                    }
                }
            }
        }

        getContext().deleteObjects(enrolmentsToDelete);
        getContext().deleteObjects(invoiceLinesToDelete);
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
     */
    private void initPayment() {
        College currentCollege = webSiteService.getCurrentCollege();
        College college = (College) context.localObject(currentCollege.getObjectId(), currentCollege);

        if (payment == null ||payment.getStatus() == PaymentStatus.FAILED || 
        		payment.getStatus() == PaymentStatus.FAILED_CARD_DECLINED || 
        		payment.getStatus() == PaymentStatus.FAILED_NO_PLACES) {

            payment = context.newObject(PaymentIn.class);
            payment.setStatus(PaymentStatus.NEW);
            payment.setSource(PaymentSource.SOURCE_WEB);
            payment.setCollege(college);

            Session session = request.getSession(false);

            if (session != null) {
                if (failedPayment != null) {
                    hadPreviousPaymentFailure = true;
                    payment.setCreditCardCVV(failedPayment.getCreditCardCVV());
                    payment.setCreditCardExpiry(failedPayment.getCreditCardExpiry());
                    payment.setCreditCardName(failedPayment.getCreditCardName());
                    payment.setCreditCardNumber(failedPayment.getCreditCardNumber());
                    payment.setCreditCardType(failedPayment.getCreditCardType());
                    failedPayment = null;
                }
            }
        }

        if (invoice == null) {
            invoice = context.newObject(Invoice.class);
            // fill the invoice with default values
            invoice.setInvoiceDate(new Date());
            invoice.setAmountOwing(BigDecimal.ZERO);
            invoice.setDateDue(new Date());
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
        enrolment.setStatus(EnrolmentStatus.IN_TRANSACTION);
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
        return preferenceController.isPaymentGatewayEnabled();
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
     * @param checkoutResult .
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


    /**
     * Invoked when the paymentDetailsForm is submitted and validated
     * successfully. Fills in the rest of the needed properties, sets the
     * transaction status to entities to be committed and commits the
     * appropriate set of entities to context:
     * <ul>
     * <li>if payment amount is not zero, commits the payment with lines,
     * invoice with lines, enrolments</li>
     * <li>if payment amount is zero, commits only the enrolments with related
     * invoice and invoice lines(the others are deleted)</li>
     * </ul>
     *
     * @return the block that displays the processing of payment {@see
     *         EnrolmentPaymentProcessing}.
     */

    public void processPayment() {

        synchronized (context)
        {
            List<Enrolment> enrolments = getEnrolmentsList();

            // enrolments to be persisted
            List<Enrolment> validEnrolments = getEnrolmentsToPersist(enrolments);
            // invoiceLines to be persisted
            List<InvoiceLine> validInvoiceLines = getInvoiceLinesToPersist();

            /**
             * The test has been added to exclude problem described in task #14138
             */
            if (validEnrolments.isEmpty() || validInvoiceLines.isEmpty())
            {
                throw new IllegalStateException("Course is not selected. Perhaps,  two or more tabs are used to pay for the courses.");
            }

            // fill the processing result component with proper values
            EnrolmentPaymentProcessing enrolmentPaymentProcessing = getResultingElement();
            enrolmentPaymentProcessing.setInvoice(invoice);

            // even if the payment amount is zero, the contact for it is
            // selected(the first in list by default)
            invoice.setContact(payment.getContact());
            invoice.setBillToAddress(payment.getContact().getAddress());

            Money totalIncGst = getTotalIncGst();
            payment.setAmount(totalIncGst.toBigDecimal());

            Money totalGst = InvoiceUtil.sumInvoiceLines(validInvoiceLines, true);
            Money totalExGst = InvoiceUtil.sumInvoiceLines(validInvoiceLines, false);

            invoice.setTotalExGst(totalExGst.toBigDecimal());
            invoice.setTotalGst(totalGst.toBigDecimal());

            PaymentInLine paymentInLine = context.newObject(PaymentInLine.class);
            paymentInLine.setInvoice(invoice);
            paymentInLine.setPaymentIn(payment);
            paymentInLine.setAmount(payment.getAmount());
            paymentInLine.setCollege(payment.getCollege());

            enrolmentPaymentProcessing.setPayment(payment);
            payment.setStatus(PaymentStatus.IN_TRANSACTION);

            for (Enrolment e : validEnrolments) {
            	e.setStatus(EnrolmentStatus.IN_TRANSACTION);
            }
            enrolmentPaymentProcessing.setEnrolments(validEnrolments);
                
            // commit enrolments in IN_TRANSACTION state and then run validation for places
            context.commitChanges();
            
            if (EnrolmentValidationUtil.isPlacesLimitExceeded(validEnrolments)) {
            	performGatewayOperation();
            } else {
            	
            	// if places limit exceeded then failing payment and process everything the same
            	// way like if payment was failed by gateway
            	payment.setStatus(PaymentStatus.FAILED_NO_PLACES);
            	payment.failPayment();
            	failedPayment = payment;
            }

            setCheckoutResult(false);
        }

    }

    void performGatewayOperation() {
        paymentGatewayService.performGatewayOperation(payment);
        if (PaymentStatus.SUCCESS.equals(payment.getStatus())) {
            //PaymentIn success so commit.
            payment.getObjectContext().commitChanges();
        } else {
            failedPayment = payment;
        }
    }


    /**
     * Iterates through all the enrolments selected(ie which has the related
     * invoiceLine) and checks if the related class has any available places for
     * enrolling.
     *
     * @return true if all the selected classes are available for enrolling.
     */
    public boolean isAllEnrolmentsAvailable(List<Enrolment> enrolments) {
        for (Enrolment enrolment : enrolments) {
            if (enrolment.getInvoiceLine() != null
                    && (!enrolment.getCourseClass().isHasAvailableEnrolmentPlaces() || enrolment.getCourseClass().hasEnded())) {
                return false;
            }
        }
        return true;
    }


    /**
     * Defines which enrolments are "checked" and should be included into the
     * processing and deletes the non-checked. Invoked on submit the checkout.
     *
     * @return
     */
    public List<Enrolment> getEnrolmentsToPersist(List<Enrolment> enrolments) {
        List<Enrolment> validEnrolments = new ArrayList<Enrolment>();
        ObjectContext context = payment.getObjectContext();

        // define which enrolments are "checked" and should be included into the
        // processing
        for (Enrolment e : enrolments) {
            if (e.getInvoiceLine() == null) {
                context.deleteObject(e);
            } else {
                validEnrolments.add(e);
            }
        }

        return validEnrolments;
    }


    /**
     * Defines which invoiceLines have the not-null reference to enrolment and
     * should be included into the processing and deletes the others. Invoked on
     * submit the checkout.
     *
     * @return
     */
    List<InvoiceLine> getInvoiceLinesToPersist() {
        ObjectContext context = payment.getObjectContext();
        List<InvoiceLine> validInvoiceLines = new ArrayList<InvoiceLine>();

        List<InvoiceLine> invoiceLinesToDelete = new ArrayList<InvoiceLine>();
        // define which invoiceLines have the reference to enrolment and should
        // be included into the processing
        for (InvoiceLine invLine : invoice.getInvoiceLines()) {
            Enrolment enrolment = invLine.getEnrolment();
            if (enrolment == null) {
                invoiceLinesToDelete.add(invLine);
            } else {
                validInvoiceLines.add(invLine);
                // discounts that could be applied to the courseClass and the
                // student of enrolment
                List<Discount> discountsToApply = enrolment.getCourseClass().getDiscountsToApply(
                        new RealDiscountsPolicy(discountService.getPromotions(), enrolment.getStudent()));
                // iterate through the list of discounts and create the
                // appropriate InvoiceLineDiscount objects
                for (Discount discount : discountsToApply) {
                    Expression discountQualifier = ExpressionFactory.matchExp(InvoiceLineDiscount.DISCOUNT_PROPERTY, discount);
                    if (discountQualifier.filterObjects(invLine.getInvoiceLineDiscounts()).isEmpty()) {
                        InvoiceLineDiscount invoiceLineDiscount = context.newObject(InvoiceLineDiscount.class);
                        invoiceLineDiscount.setInvoiceLine(invLine);
                        invoiceLineDiscount.setDiscount(discount);
                        invoiceLineDiscount.setCollege(enrolment.getCollege());
                    }
                }
            }
        }

        context.deleteObjects(invoiceLinesToDelete);

        return validInvoiceLines;
    }

    /**
     * Payment process should be start only for new payment and payment process is not started.
     * checkout flag false and payment is not new can be when DPS failed the last attempt
     * and an user stays on the result page and there is other opened tab where make payment button is available.
     * @return true when we can.
     */
    public boolean canStartPaymentProcess() {
        return PaymentStatus.NEW.equals(payment.getStatus()) && !isCheckoutResult();
    }

    
    public ObjectContext getContext()
    {
        return context;
    }


    /**
     * Method returns true if by some reason persist properties have been cleared.
     * For example: the payment has been processed from other tab of the browser.
     */
    public boolean isPersistCleared()
    {
        return context == null;
    }
    
    public Object handleUnexpectedException(final Throwable cause) {
    	if (isPersistCleared()) {
			LOGGER.warn("Persist properties have been cleared. User used two or more tabs", cause);
			return this;
		} else {
			throw new IllegalArgumentException(cause);
		}
    }


}
