package ish.oncourse.enrol.utils;

import ish.common.types.EnrolmentStatus;
import ish.common.types.PaymentSource;
import ish.common.types.PaymentStatus;
import ish.math.Money;
import ish.oncourse.enrol.services.invoice.IInvoiceProcessingService;
import ish.oncourse.model.*;
import ish.oncourse.services.paymentexpress.IPaymentGatewayService;
import ish.oncourse.util.FormatUtils;
import ish.util.InvoiceUtil;
import org.apache.cayenne.ObjectContext;
import org.apache.cayenne.PersistenceState;
import org.apache.cayenne.exp.Expression;
import org.apache.cayenne.exp.ExpressionFactory;
import org.apache.cayenne.query.Ordering;
import org.apache.cayenne.query.SortOrder;
import org.apache.log4j.Logger;
import org.apache.tapestry5.StreamResponse;
import org.apache.tapestry5.json.JSONObject;
import org.apache.tapestry5.services.Session;
import org.apache.tapestry5.util.TextStreamResponse;

import java.math.BigDecimal;
import java.util.*;

@Deprecated //will be removed after checkout/payemnt page will be ready
public class EnrolCoursesController {
	private static final Logger LOGGER = Logger.getLogger(EnrolCoursesController.class);
	private static final String INDEX_SEPARATOR = "_";
	public static final String HTTP_PROTOCOL = "http://";
	
	private EnrolCoursesModel model;
	
	private ObjectContext context;
	
	/**
     * Indicates if this page is used for displaying the enrollment checkout(if
     * false), and the result of previous checkout otherwise.
     */
	private boolean checkoutResult;
    
    private boolean hadPreviousPaymentFailure;
	
    private final IInvoiceProcessingService invoiceProcessingService;
    
    private final IPaymentGatewayService paymentGatewayService;
	
	public EnrolCoursesController(IInvoiceProcessingService invoiceProcessingService, IPaymentGatewayService paymentGatewayService) {
		super();
		this.invoiceProcessingService = invoiceProcessingService;
		this.paymentGatewayService = paymentGatewayService;
		setModel(new EnrolCoursesModel());
		setCheckoutResult(false);
		setHadPreviousPaymentFailure(false);
	}
	
	/**
	 * @return the hadPreviousPaymentFailure
	 */
	public synchronized boolean isHadPreviousPaymentFailure() {
		return hadPreviousPaymentFailure;
	}

	/**
	 * @param hadPreviousPaymentFailure the hadPreviousPaymentFailure to set
	 */
	protected void setHadPreviousPaymentFailure(boolean hadPreviousPaymentFailure) {
		this.hadPreviousPaymentFailure = hadPreviousPaymentFailure;
	}

	/**
	 * @return the model
	 */
	public synchronized EnrolCoursesModel getModel() {
		return model;
	}

	/**
	 * @param model the model to set
	 */
	void setModel(EnrolCoursesModel model) {
		this.model = model;
	}

	/**
	 * @return the context
	 */
	public synchronized ObjectContext getContext() {
		return context;
	}

	/**
	 * @param context the context to set
	 */
	public synchronized void setContext(ObjectContext context) {
		this.context = context;
	}

	/**
	 * @return the checkoutResult
	 */
	public synchronized boolean isCheckoutResult() {
		return checkoutResult;
	}

	/**
     * Sets value to the {@link #checkoutResult}.
     *
     * @param checkoutResult .
     */
	public synchronized void setCheckoutResult(boolean checkoutResult) {
		this.checkoutResult = checkoutResult;
	}
    
	/**
	 * Fill the list of the enrollments from the vector of enrollments and students
	 * @return the list of actual enrollments.
	 */
	public synchronized List<Enrolment> getEnrolmentsList() {
        List<Enrolment> result = new ArrayList<Enrolment>();
        if (getModel().getEnrolments() != null) {
            for (Enrolment[] e : getModel().getEnrolments()) {
                if (e != null) {
                    result.addAll(Arrays.asList(e));
                }
            }
        }
        //this is the optimization which allow us not pass this data to EnrolmentPaymentEntry
        getModel().setEnrolmentsList(result);
        return result;
    }
	
	/**
     * The method deletes existed in-transaction enrollments  if the orderedClassesIds does not contains classes for these enrollments
     * @param enrolments - enrollments list for check.
     * @param orderedClassesIds - actual ordered classes ids.
     */
	public synchronized void deleteNotUsedEnrolments(List<Enrolment> enrolments, List<Long> orderedClassesIds) {
        List<Enrolment> enrolmentsToDelete = new ArrayList<Enrolment>();
        List<InvoiceLine> invoiceLinesToDelete = new ArrayList<InvoiceLine>();
        if (enrolments != null && !enrolments.isEmpty()) {
            for (Enrolment enrolment : enrolments) {
                if (orderedClassesIds == null || orderedClassesIds.size() < 1 || !orderedClassesIds.contains(enrolment.getCourseClass().getId())) {
                    /**
                     * We can delete only IN_TRANSACTION enrollments
                     */
                    if (EnrolmentStatus.IN_TRANSACTION.equals(enrolment.getStatus())) {
                        enrolmentsToDelete.add(enrolment);
                        InvoiceLine invoiceLine = enrolment.getInvoiceLine();
                        if (invoiceLine != null) {
                            invoiceLinesToDelete.add(enrolment.getInvoiceLine());
                        }
                    }
                    else {
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
	
	/**
     * The method returns true if orderedClassesIds and classesToEnrol doesn't contain the same classes.
     * @param orderedClassesIds - actual ordered classes ids.
     * @return true if model's classesToEnrol doesn't contain required classes.
     */
	public synchronized boolean shouldReloadClassesToEnrol(List<Long> orderedClassesIds) {
        if (getModel().getClassesToEnrol() == null) {
            return true;
        }
        if (orderedClassesIds.size() != getModel().getClassesToEnrol().size()) {
            return true;
        }
        for (CourseClass courseClass: getModel().getClassesToEnrol()) {
            if (!orderedClassesIds.contains(courseClass.getId())) {
                return true;
            }
        }
        return false;
    }
	
	/**
	 * Order actual classes to enrol.
	 */
	public synchronized void orderClassesToEnrol() {
		List<Ordering> orderings = new ArrayList<Ordering>();
        orderings.add(new Ordering(CourseClass.COURSE_PROPERTY + "." + Course.CODE_PROPERTY, SortOrder.ASCENDING));
        orderings.add(new Ordering(CourseClass.CODE_PROPERTY, SortOrder.ASCENDING));
        Ordering.orderList(getModel().getClassesToEnrol(), orderings);
	}
	
	/**
	 * Prepare enrollments indexes map.
	 * @return map with the enrollments indexes.
	 */
	protected Map<Enrolment, String> getEnrolmentsIndexesMap() {
        Map<Enrolment, String> result = new HashMap<Enrolment, String>();
        if (getModel().getEnrolments() != null) {
            for (int i = 0; i < getModel().getEnrolments().length; i++) {
                for (int j = 0; j < getModel().getEnrolments()[0].length; j++) {
                    result.put(getModel().getEnrolments()[i][j], i + INDEX_SEPARATOR + j);
                }
            }
        }
        return result;
    }
	
	/**
     * Creates the new {@link Enrolment} entity for the given courseClass and
     * Student.
     *
     * @param courseClass
     * @param student
     * @return
     */
	protected Enrolment createEnrolment(CourseClass courseClass, Student student, List<Discount> actualPromotions) {
        Enrolment enrolment = getContext().newObject(Enrolment.class);
        enrolment.setStatus(EnrolmentStatus.IN_TRANSACTION);
        enrolment.setSource(PaymentSource.SOURCE_WEB);
        enrolment.setCollege(student.getCollege());
        enrolment.setStudent(student);
        enrolment.setCourseClass(courseClass);
        if (!enrolment.isDuplicated() && courseClass.isHasAvailableEnrolmentPlaces() && !courseClass.hasEnded()) {
            InvoiceLine invoiceLine = invoiceProcessingService.createInvoiceLineForEnrolment(enrolment, actualPromotions);
            invoiceLine.setInvoice(getModel().getInvoice());
            enrolment.setInvoiceLine(invoiceLine);
        }
        return enrolment;
    }
	
	/**
     * Checks the newly inited classes and contacts, init {@link #enrollments}
     * properly: create new ones or use created previously if they are correct.
     */
	protected void initEnrolments(List<Discount> actualPromotions) {
        Enrolment[][] enrolments = new Enrolment[getModel().getContacts().size()][getModel().getClassesToEnrol().size()];
        InvoiceLine[][] invoiceLines = new InvoiceLine[getModel().getContacts().size()][getModel().getClassesToEnrol().size()];
        Map<Enrolment, String> currentEnrolmentsMap = getEnrolmentsIndexesMap();
        List<Enrolment> currentEnrolments = new ArrayList<Enrolment>(currentEnrolmentsMap.keySet());
        // Checks the current contacts and classes to create proper enrollments
        for (int i = 0; i < getModel().getContacts().size(); i++) {
            for (int j = 0; j < getModel().getClassesToEnrol().size(); j++) {
                Enrolment enrolmentToAdd = null;
                InvoiceLine invoiceLineToAdd = null;
                Enrolment existingEnrolment = null;
                Student student = ((Contact) getContext().localObject(getModel().getContacts().get(i).getObjectId(), getModel().getContacts().get(i)))
                	.getStudent();
                CourseClass courseClass = (CourseClass) getContext().localObject(getModel().getClassesToEnrol().get(j).getObjectId(), 
                	getModel().getClassesToEnrol().get(j));
                if (!currentEnrolments.isEmpty()) {
                    // checks if the enrollment with such a class and student is
                    // already created
                    Expression sameStudentAndClass = ExpressionFactory.matchExp(Enrolment.STUDENT_PROPERTY, student)
                           .andExp(ExpressionFactory.matchExp(Enrolment.COURSE_CLASS_PROPERTY, courseClass));
                    List<Enrolment> sameStudentAndClassResult = sameStudentAndClass.filterObjects(currentEnrolments);
                    if (!sameStudentAndClassResult.isEmpty()) {
                        existingEnrolment = sameStudentAndClassResult.get(0);
                    }
                }
                if (existingEnrolment == null || existingEnrolment.getPersistenceState() == PersistenceState.TRANSIENT) {
                    // create new enrollment if it doen't exist or has been
                    // deleted
                    enrolmentToAdd = createEnrolment(courseClass, student, actualPromotions);
                    invoiceLineToAdd = enrolmentToAdd.getInvoiceLine();
                } else {
                    // use previously created enrollment
                    enrolmentToAdd = existingEnrolment;
                    // the invoiceLine could be null because of unticked
                    // enrollment, use the corresponded from existing array
                    String[] index = currentEnrolmentsMap.get(enrolmentToAdd).split(INDEX_SEPARATOR);
                    invoiceLineToAdd = getModel().getInvoiceLines()[Integer.parseInt(index[0])][Integer.parseInt(index[1])];
                    if (invoiceLineToAdd != null) {
                        // recalculate discounts that possibly have changed
                        invoiceProcessingService.setupDiscounts(enrolmentToAdd, invoiceLineToAdd, actualPromotions);
                    }
                }
                enrolments[i][j] = enrolmentToAdd;
                invoiceLines[i][j] = invoiceLineToAdd;
            }
        }
        getModel().setEnrolments(enrolments);
        getModel().setInvoiceLines(invoiceLines);
    }
    
    /**
     * Creates and initializes the set of payment/enrollment-related entities:
     * <ul>
     * <li> {@link PaymentIn} - one payment entity is created for all the
     * selected courses and contacts</li>
     * <li> {@link Invoice} - one invoice entity is created for all the selected
     * courses and contacts</li>
     * <li> {@link Enrolment} - separate enrollment entity is created for the each
     * element of the cartesian product of the selected courses and contacts</li>
     * <li> {@link InvoiceLine} - the new entity is created for each enrollment if
     * the class is enrollable, linked to the invoice entity.</li>
     * </ul>
     * @param session - tapestry request session
     * @param currentCollege - current college object returned from webSiteService.getCurrentCollege() call
     */
    public synchronized void initPayment(Session session, College currentCollege, List<Discount> actualPromotions) {
    	College college = (College) getContext().localObject(currentCollege.getObjectId(), currentCollege);
    	if (getModel().getPayment() == null || PaymentStatus.FAILED.equals(getModel().getPayment().getStatus()) 
        	|| PaymentStatus.FAILED_CARD_DECLINED.equals(getModel().getPayment().getStatus()) 
        	|| PaymentStatus.FAILED_NO_PLACES.equals(getModel().getPayment().getStatus())) {
        	
    		getModel().setPayment(getContext().newObject(PaymentIn.class));
        	getModel().getPayment().setStatus(PaymentStatus.NEW);
        	getModel().getPayment().setSource(PaymentSource.SOURCE_WEB);
        	getModel().getPayment().setCollege(college);
        	
        	if (session != null) {
        		if (getModel().getFailedPayment() != null) {
        			setHadPreviousPaymentFailure(true);
                    getModel().getPayment().setCreditCardCVV(getModel().getFailedPayment().getCreditCardCVV());
                    getModel().getPayment().setCreditCardExpiry(getModel().getFailedPayment().getCreditCardExpiry());
                    getModel().getPayment().setCreditCardName(getModel().getFailedPayment().getCreditCardName());
                    getModel().getPayment().setCreditCardNumber(getModel().getFailedPayment().getCreditCardNumber());
                    getModel().getPayment().setCreditCardType(getModel().getFailedPayment().getCreditCardType());
                    getModel().setFailedPayment(null);
        		}
            }
        }
    	//initially set the first contact as default payer
    	if (getModel().getContact() == null) {
    		getModel().setContact(getModel().getContacts().get(0));
    	}
        if (getModel().getInvoice() == null) {
            getModel().setInvoice(getContext().newObject(Invoice.class));
            // fill the invoice with default values
            getModel().getInvoice().setInvoiceDate(new Date());
            getModel().getInvoice().setAmountOwing(Money.ZERO);
            getModel().getInvoice().setDateDue(new Date());
            getModel().getInvoice().setSource(PaymentSource.SOURCE_WEB);
            getModel().getInvoice().setCollege(college);
        }
        initEnrolments(actualPromotions);
    }
    
    /**
     * Check that invoice lines linked with the enrollments list applied some discounts
     * @return true if any discount applied.
     */
    public synchronized boolean isHasDiscount() {
        Money result = Money.ZERO;
        for (int i = 0; i < getModel().getContacts().size(); i++) {
            for (int j = 0; j < getModel().getClassesToEnrol().size(); j++) {
                InvoiceLine invoiceLine = getModel().getEnrolments()[i][j].getInvoiceLine();
                if (invoiceLine != null) {
                    result = result.add(invoiceLine.getDiscountTotalExTax());
                }
            }
        }
        return !result.isZero();
    }
    
    /**
     * Calculate total discount amount for all actual enrollments.
     * @return total discount amount for all actual enrollments.
     */
    public synchronized Money getTotalDiscountAmountIncTax() {
        Money result = Money.ZERO;
        for (int i = 0; i < getModel().getContacts().size(); i++) {
            for (int j = 0; j < getModel().getClassesToEnrol().size(); j++) {
                InvoiceLine invoiceLine = getModel().getEnrolments()[i][j].getInvoiceLine();
                if (invoiceLine != null) {
                    result = result.add(invoiceLine.getDiscountTotalIncTax());
                }
            }
        }
        getModel().setMoneyFormat(FormatUtils.chooseMoneyFormat(result));
        return result;
    }
    
    /**
     * Calculate total (include GST) invoice amount for all actual enrollments.
     * @return total invoice amount for all actual enrollments.
     */
    public synchronized Money getTotalIncGst() {
        Money result = Money.ZERO;
        for (int i = 0; i < getModel().getContacts().size(); i++) {
            for (int j = 0; j < getModel().getClassesToEnrol().size(); j++) {
                InvoiceLine invoiceLine = getModel().getEnrolments()[i][j].getInvoiceLine();
                if (invoiceLine != null) {
                    result = result.add(invoiceLine.getPriceTotalIncTax()
                            .subtract(invoiceLine.getDiscountTotalIncTax()));
                }
            }
        }
        getModel().setMoneyFormat(FormatUtils.chooseMoneyFormat(result));
        //this is the optimization which allow us not pass this data to EnrolmentPaymentEntry
        getModel().setTotalIncGst(result);
        return result;
    }
    
    // TODO port this method to some service(it is a part of DiscountService#isStudentElifible)
    /**
     * Check that any discounts potentially can be applied for actual enrollments.
     * @return true if some discounts can be applied for actual enrollments.
     */
    public synchronized boolean hasSuitableClasses(StudentConcession studentConcession) {
        for (CourseClass cc : getModel().getClassesToEnrol()) {
            for (DiscountCourseClass dcc : cc.getDiscountCourseClasses()) {
                for (DiscountConcessionType dct : dcc.getDiscount().getDiscountConcessionTypes()) {
                    if (studentConcession.getConcessionType().getId().equals(dct.getConcessionType().getId()) 
                    	&& (!Boolean.TRUE.equals(studentConcession.getConcessionType().getHasExpiryDate())
                        	|| (studentConcession.getExpiresOn() != null && studentConcession.getExpiresOn().after(new Date()))) ) {
                    	return true;
                    }
                }
            }
        }
        return false;
    }
    
    /**
     * Create checkSession StreamResponse.
     * @param session - session for check.
     * @return Text stream response.
     */
    public static StreamResponse createCheckSessionResponse(Session session) {
        JSONObject obj = new JSONObject();
        if (session == null) {
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

    public synchronized void processPayment(List<Discount> actualPromotions) {
    	synchronized (getContext()) {
            List<Enrolment> enrolments = getEnrolmentsList();
            // enrollments to be persisted
            List<Enrolment> validEnrolments = getEnrolmentsToPersist(enrolments);
            // invoiceLines to be persisted
            List<InvoiceLine> validInvoiceLines = getInvoiceLinesToPersist(actualPromotions);
            /**
             * The test has been added to exclude problem described in task #14138
             */
            if (validEnrolments.isEmpty() || validInvoiceLines.isEmpty()) {
                throw new IllegalStateException("Course is not selected. Perhaps, two or more tabs are used to pay for the courses.");
            }
            // even if the payment amount is zero, the contact for it is
            // selected(the first in list by default)
            getModel().getInvoice().setContact(getModel().getPayment().getContact());
            getModel().getInvoice().setBillToAddress(getModel().getPayment().getContact().getAddress());

            Money totalIncGst = getTotalIncGst();
            getModel().getPayment().setAmount(totalIncGst);
            Money totalGst = InvoiceUtil.sumInvoiceLines(validInvoiceLines, true);
            Money totalExGst = InvoiceUtil.sumInvoiceLines(validInvoiceLines, false);
            getModel().getInvoice().setTotalExGst(totalExGst);
            getModel().getInvoice().setTotalGst(totalGst);

            PaymentInLine paymentInLine = getContext().newObject(PaymentInLine.class);
            paymentInLine.setInvoice(getModel().getInvoice());
            paymentInLine.setPaymentIn(getModel().getPayment());
            paymentInLine.setAmount(getModel().getPayment().getAmount());
            paymentInLine.setCollege(getModel().getPayment().getCollege());

            getModel().getPayment().setStatus(PaymentStatus.IN_TRANSACTION);

            for (Enrolment e : validEnrolments) {
            	e.setStatus(EnrolmentStatus.IN_TRANSACTION);
            }
                
            // commit enrollments in IN_TRANSACTION state and then run validation for places
            context.commitChanges();
            
            if (EnrolmentValidationUtil.isPlacesLimitExceeded(validEnrolments)) {
            	performGatewayOperation();
            } else {
            	// if places limit exceeded then failing payment and process everything the same
            	// way like if payment was failed by gateway
            	getModel().getPayment().setStatus(PaymentStatus.FAILED_NO_PLACES);
            	getModel().getPayment().failPayment();
            	getModel().setFailedPayment(getModel().getPayment());
            }
            setCheckoutResult(false);
        }
    }
    
    protected void performGatewayOperation() {
        paymentGatewayService.performGatewayOperation(getModel().getPayment());
        if (PaymentStatus.SUCCESS.equals(getModel().getPayment().getStatus())) {
            //PaymentIn success so commit.
        	getModel().getPayment().getObjectContext().commitChanges();
        } else {
        	getModel().setFailedPayment(getModel().getPayment());
        }
    }
    
    /**
     * Defines which enrollments are "checked" and should be included into the
     * processing and deletes the non-checked. Invoked on submit the checkout.
     * @return list of actual enrollments.
     */
    public synchronized List<Enrolment> getEnrolmentsToPersist(List<Enrolment> enrolments) {
        List<Enrolment> validEnrolments = new ArrayList<Enrolment>();
        ObjectContext context = getModel().getPayment().getObjectContext();
        // define which enrollments are "checked" and should be included into the processing
        for (Enrolment e : enrolments) {
            if (e.getInvoiceLine() == null) {
                context.deleteObject(e);
            } else {
                validEnrolments.add(e);
            }
        }
      //this is the optimization which allow us not pass this data to EnrolmentPaymentEntry
        getModel().setEnrolmentsList(validEnrolments);
        return validEnrolments;
    }
    
    /**
     * Defines which invoiceLines have the not-null reference to enrollment and
     * should be included into the processing and deletes the others. Invoked on
     * submit the checkout.
     * Also apply the discounts for invoiceLines.
     * @return list of actual invoice lines linked with the actual enrollments.
     */
    List<InvoiceLine> getInvoiceLinesToPersist(List<Discount> actualPromotions) {
        ObjectContext context = getModel().getPayment().getObjectContext();
        List<InvoiceLine> validInvoiceLines = new ArrayList<InvoiceLine>();
        List<InvoiceLine> invoiceLinesToDelete = new ArrayList<InvoiceLine>();
        // define which invoiceLines have the reference to enrollment and should
        // be included into the processing
        for (InvoiceLine invLine : getModel().getInvoice().getInvoiceLines()) {
            Enrolment enrolment = invLine.getEnrolment();
            if (enrolment == null) {
                invoiceLinesToDelete.add(invLine);
            } else {
                validInvoiceLines.add(invLine);
                // discounts that could be applied to the courseClass and the
                // student of enrollment
                List<Discount> discountsToApply = enrolment.getCourseClass().getDiscountsToApply(
                	new RealDiscountsPolicy(actualPromotions, enrolment.getStudent()));
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
     * Iterates through all the enrolments selected(ie which has the related
     * invoiceLine) and checks if the related class has any available places for
     * enrolling.
     *
     * @return true if all the selected classes are available for enrolling.
     */
    public synchronized boolean isAllEnrolmentsAvailable(List<Enrolment> enrolments) {
        for (Enrolment enrolment : enrolments) {
            if (enrolment.getInvoiceLine() != null
                    && (!enrolment.getCourseClass().isHasAvailableEnrolmentPlaces() || enrolment.getCourseClass().hasEnded())) {
                return false;
            }
        }
        return true;
    }
    
    /**
     * Payment process should be start only for new payment and payment process is not started.
     * checkout flag false and payment is not new can be when DPS failed the last attempt
     * and an user stays on the result page and there is other opened tab where make payment button is available.
     * @return true when we can.
     */
    public synchronized boolean canStartPaymentProcess() {
        return PaymentStatus.NEW.equals(getModel().getPayment().getStatus()) && !isCheckoutResult();
    }
    
    /**
     * Method returns true if by some reason persist properties have been cleared.
     * For example: the payment has been processed from other tab of the browser.
     */
    boolean isPersistCleared() {
        return getContext() == null;
    }
    
    public synchronized Object handleUnexpectedException(final Throwable cause) {
    	if (isPersistCleared()) {
			LOGGER.warn("Persist properties have been cleared. User used two or more tabs", cause);
			return this;
		} else {
			throw new IllegalArgumentException(cause);
		}
    }
    
    /**
	 * Returns true if the enrol operation was successful and false otherwise.
	 * 
	 * @return
	 */
	public synchronized boolean isEnrolmentSuccessful() {
		return PaymentStatus.SUCCESS.equals(getModel().getPayment().getStatus());
	}
	
	/**
	 * Returns true if the enrol operation was failed and false otherwise.
	 * 
	 * @return
	 */
	public synchronized boolean isEnrolmentFailed() {
		PaymentStatus status = getModel().getPayment().getStatus();
		return PaymentStatus.FAILED.equals(status) || PaymentStatus.STATUS_REFUNDED.equals(status)
			|| PaymentStatus.FAILED_CARD_DECLINED.equals(status) || PaymentStatus.FAILED_NO_PLACES.equals(status);
	}
	
	/**
	 * Returns true if payment was failed because of insufficient places for one of enrollments.
	 * 
	 * @return
	 */
	public synchronized boolean isEnrolmentFailedNoPlaces() {
		return PaymentStatus.FAILED_NO_PLACES.equals(getModel().getPayment().getStatus());
	}
    
	/**
	 * Action which need to be called when abandon payment request.
	 */
	public synchronized void actionOnAbandon() {
        synchronized (getContext()) {
        	getModel().getPayment().abandonPayment();
        	getModel().getPayment().getObjectContext().commitChanges();
        }
	}

}
