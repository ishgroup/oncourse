package ish.oncourse.enrol.components;

import ish.common.types.CreditCardType;
import ish.common.types.PaymentStatus;
import ish.math.Money;
import ish.oncourse.enrol.pages.EnrolCourses;
import ish.oncourse.model.Contact;
import ish.oncourse.model.Discount;
import ish.oncourse.model.Enrolment;
import ish.oncourse.model.EnrolmentStatus;
import ish.oncourse.model.Invoice;
import ish.oncourse.model.InvoiceLine;
import ish.oncourse.model.InvoiceLineDiscount;
import ish.oncourse.model.InvoiceStatus;
import ish.oncourse.model.PaymentIn;
import ish.oncourse.model.PaymentInLine;
import ish.oncourse.model.RealDiscountsPolicy;
import ish.oncourse.selectutils.ISHEnumSelectModel;
import ish.oncourse.selectutils.ListSelectModel;
import ish.oncourse.selectutils.ListValueEncoder;
import ish.oncourse.services.discount.IDiscountService;
import ish.oncourse.ui.utils.FormatUtils;
import ish.persistence.CommonPreferenceController;

import java.math.BigDecimal;
import java.text.Format;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.List;
import java.util.concurrent.locks.ReentrantLock;

import org.apache.cayenne.ObjectContext;
import org.apache.cayenne.exp.Expression;
import org.apache.cayenne.exp.ExpressionFactory;
import org.apache.tapestry5.Field;
import org.apache.tapestry5.ValidationTracker;
import org.apache.tapestry5.annotations.InjectComponent;
import org.apache.tapestry5.annotations.InjectPage;
import org.apache.tapestry5.annotations.OnEvent;
import org.apache.tapestry5.annotations.Parameter;
import org.apache.tapestry5.annotations.Persist;
import org.apache.tapestry5.annotations.Property;
import org.apache.tapestry5.annotations.SetupRender;
import org.apache.tapestry5.corelib.components.Form;
import org.apache.tapestry5.corelib.components.Select;
import org.apache.tapestry5.corelib.components.TextField;
import org.apache.tapestry5.corelib.components.Zone;
import org.apache.tapestry5.ioc.Messages;
import org.apache.tapestry5.ioc.annotations.Inject;
import org.apache.tapestry5.ioc.services.PropertyAccess;

public class EnrolmentPaymentEntry {

	/**
	 * Credit card expire date interval
	 */
	private static final int EXPIRE_YEAR_INTERVAL = 15;

	/**
	 * tapestry services
	 */
	@Inject
	private PropertyAccess propertyAccess;

	@Inject
	private Messages messages;

	/**
	 * ish services
	 */
	@Inject
	private CommonPreferenceController preferenceService;

	@Inject
	private IDiscountService discountService;

	/**
	 * Parameters
	 */
	@Parameter
	private Money totalIncGst;

	@Parameter
	@Property
	private List<Contact> payers;

	@Parameter
	@Property
	private PaymentIn payment;

	@Parameter
	private Invoice invoice;
	/**
	 * Auxiliary properties
	 */
	@Property
	@Persist
	private Format moneyFormat;

	@Property
	@Persist
	private ListSelectModel<Contact> payersModel;

	@Property
	@Persist
	private ListValueEncoder<Contact> payersEncoder;

	@Property
	@Persist
	private ISHEnumSelectModel cardTypeModel;

	@Property
	private String cardNumberErrorMessage;

	@Property
	private String ccExpiryMonth;

	@Property
	private Integer ccExpiryYear;

	@Property
	@Persist
	private List<Integer> years;

	@Property
	@Persist
	private boolean userAgreed;

	@Property
	@Persist
	private String enrolmentDisclosure;

	/**
	 * Components
	 */
	@InjectComponent
	private Zone paymentZone;

	@InjectComponent
	@Property
	private Form paymentDetailsForm;

	@InjectComponent
	private Select cardTypeSelect;

	@InjectComponent
	private TextField cardName;

	@InjectComponent
	private TextField cardNumber;

	@InjectComponent
	private TextField cardcvv;

	@InjectPage
	private EnrolCourses enrolCourses;

	@Parameter
	private List<Enrolment> enrolments;
	private final ReentrantLock lock = new ReentrantLock();

	/**
	 * Indicates if the submit was because of pressing the submit button.
	 */
	private boolean isSubmitted;

	@SetupRender
	void beforeRender() {
		initYears();
		initEnrolmentDisclosure();

		initPayers();

		payersModel = new ListSelectModel<Contact>(payers, "fullName", propertyAccess);

		payersEncoder = new ListValueEncoder<Contact>(payers, "id", propertyAccess);

		cardTypeModel = new ISHEnumSelectModel(CreditCardType.class, messages, CommonPreferenceController
				.getCCAvailableTypes(preferenceService).values().toArray(new CreditCardType[] {}));

		userAgreed = false;
	}

	private void initYears() {
		years = new ArrayList<Integer>();
		int currentYear = Calendar.getInstance().get(Calendar.YEAR);

		for (int i = 0; i < EXPIRE_YEAR_INTERVAL; i++) {
			years.add(currentYear + i);
		}
	}

	private void initEnrolmentDisclosure() {

		enrolmentDisclosure = preferenceService.getFeatureEnrolmentDisclosure();

		if ("".equals(enrolmentDisclosure)) {
			enrolmentDisclosure = null;
		}
	}

	private void initPayers() {
		List<Contact> localPayers = new ArrayList<Contact>(payers.size());
		for (Contact payer : payers) {
			localPayers.add((Contact) payment.getObjectContext().localObject(payer.getObjectId(), payer));
		}

		payers = localPayers;
		payment.setContact(payers.get(0));
	}

	/**
	 * Iterates through all the enrolments selected(ie which has the related invoiceLine) and checks if the related class has any available
	 * places for enrolling.
	 * 
	 * @return true if all the selected classes are available for enrolling.
	 */
	public boolean isAllEnrolmentsAvailable() {
		for (Enrolment enrolment : enrolments) {
			if (enrolment.getInvoiceLine() != null
					&& (!enrolment.getCourseClass().isHasAvailableEnrolmentPlaces() || enrolment.getCourseClass().hasEnded())) {
				return false;
			}
		}
		return true;
	}

	public boolean isZeroPayment() {
		return totalIncGst.isZero();
	}

	public boolean isHasConcessionsCollege() {
		return preferenceService.getFeatureConcessionsInEnrolment();
	}

	public boolean isHasConcessionsEnrolment() {
		for (Enrolment enrolment : enrolments) {
			if (!enrolment.getCourseClass().getConcessionDiscounts().isEmpty()) {
				return true;
			}
		}
		return false;
	}

	public boolean isHasStudentWithoutConcessions() {
		for (Enrolment enrolment : enrolments) {
			if (enrolment.getStudent().getStudentConcessions().isEmpty()) {
				return true;
			}
		}
		return false;
	}

	public String getSubmitButtonText() {
		return isZeroPayment() ? messages.get("submit.button.text.enrol") : messages.get("submit.button.text.payment");
	}

	public String getCardTypeClass() {
		return getInputSectionClass(cardTypeSelect);
	}

	public String getCardNameInputClass() {
		return getInputSectionClass(cardName);
	}

	public String getCardNumberInputClass() {
		return getInputSectionClass(cardNumber);
	}

	public String getCardcvvInputClass() {
		return getInputSectionClass(cardcvv);
	}

	private String getInputSectionClass(Field field) {
		ValidationTracker defaultTracker = paymentDetailsForm.getDefaultTracker();
		return defaultTracker == null || !defaultTracker.inError(field) ? messages.get("validInput") : messages.get("validateInput");
	}

	/**
	 * Invoked when the paymentDetailsForm is submitted and validated successfully. Fills in the rest of the needed properties, sets the
	 * transaction status to entities to be committed and commits the appropriate set of entities to context:
	 * <ul>
	 * <li>if payment amount is not zero, commits the payment with lines, invoice with lines, enrolments</li>
	 * <li>if payment amount is zero, commits only the enrolments with related invoice and invoice lines(the others are deleted)</li>
	 * </ul>
	 * 
	 * @return the block that displays the processing of payment {@see EnrolmentPaymentProcessing}.
	 */
	@OnEvent(component = "paymentDetailsForm", value = "success")
	Object submitted() {

		if (!isSubmitted) {
			return paymentZone.getBody();
		}
		
		// enrolments to be persisted
		List<Enrolment> validEnrolments = getEnrolmentsToPersist();
		// invoiceLines to be persisted
		List<InvoiceLine> validInvoiceLines = getInvoiceLinesToPersist();

		// fill the processing result component with proper values
		EnrolmentPaymentProcessing enrolmentPaymentProcessing = enrolCourses.getResultingElement();
		enrolmentPaymentProcessing.setInvoice(invoice);

		// even if the payment amount is zero, the contact for it is
		// selected(the first in list by default)
		invoice.setContact(payment.getContact());

		// block until checking and the change of state holds
		synchronized (payment) {
			ObjectContext context = payment.getObjectContext();

			if (!isZeroPayment()) {
				payment.setAmount(totalIncGst.toBigDecimal());
				BigDecimal totalGst = BigDecimal.ZERO;
				BigDecimal totalExGst = BigDecimal.ZERO;

				for (InvoiceLine il : validInvoiceLines) {
					totalExGst = totalGst.add(il.getPriceTotalExTax().toBigDecimal());
					totalGst = totalGst.add(il.getPriceTotalIncTax().subtract(il.getPriceTotalExTax()).toBigDecimal());
				}
				invoice.setTotalExGst(totalExGst);
				invoice.setTotalGst(totalGst);

				PaymentInLine paymentInLine = context.newObject(PaymentInLine.class);
				paymentInLine.setInvoice(invoice);
				paymentInLine.setPaymentIn(payment);
				paymentInLine.setAmount(payment.getAmount());
				paymentInLine.setCollege(payment.getCollege());

				enrolmentPaymentProcessing.setPayment(payment);
				payment.setStatus(PaymentStatus.IN_TRANSACTION);

			} else {
				context.deleteObject(payment);
				enrolmentPaymentProcessing.setPayment(null);
				invoice.setTotalExGst(BigDecimal.ZERO);
				invoice.setTotalGst(BigDecimal.ZERO);
			}

			invoice.setStatus(InvoiceStatus.IN_TRANSACTION);

			if (isAllEnrolmentsAvailable()) {
				for (Enrolment e : validEnrolments) {
					e.setStatus(EnrolmentStatus.IN_TRANSACTION);
				}
				enrolmentPaymentProcessing.setEnrolments(validEnrolments);
				context.commitChanges();
			} else {
				enrolmentPaymentProcessing.setEnrolments(null);
				context.rollbackChanges();
			}

			enrolCourses.setCheckoutResult(true);
		}

		return enrolCourses;
	}

	/**
	 * Defines which enrolments are "checked" and should be included into the processing and deletes the non-checked. Invoked on submit the
	 * checkout.
	 * 
	 * @return
	 */
	public List<Enrolment> getEnrolmentsToPersist() {
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
	 * Defines which invoiceLines have the not-null reference to enrolment and should be included into the processing and deletes the
	 * others. Invoked on submit the checkout.
	 * 
	 * @return
	 */
	public List<InvoiceLine> getInvoiceLinesToPersist() {
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

	@OnEvent(component = "paymentDetailsForm", value = "failure")
	Object submitFailed() {
		return paymentZone.getBody();
	}

	@OnEvent(component = "paymentSubmit", value = "selected")
	void submitButtonPressed() {
		isSubmitted = true;
	}

	/**
	 * Invokes when the {@link #paymentDetailsForm} is attempted to submit.
	 */
	@OnEvent(component = "paymentDetailsForm", value = "validate")
	void validate() {
		if (!isSubmitted) {
			return;
		}
		if (!isZeroPayment()) {
			if (!payment.validateCCType()) {
				paymentDetailsForm.recordError(cardTypeSelect, messages.get("cardTypeErrorMessage"));
			}
			if (!payment.validateCCName()) {
				paymentDetailsForm.recordError(cardName, messages.get("cardNameErrorMessage"));
			}

			cardNumberErrorMessage = payment.validateCCNumber();
			if (cardNumberErrorMessage != null) {
				paymentDetailsForm.recordError(cardNumber, cardNumberErrorMessage);
			}

			String creditCardCVV = payment.getCreditCardCVV();
			if (creditCardCVV == null || creditCardCVV.equals("")) {
				paymentDetailsForm.recordError(cardcvv, messages.get("cardcvv"));
			}

			boolean hasErrorInDate = false;
			if (ccExpiryMonth == null || ccExpiryYear == null) {
				hasErrorInDate = true;
			} else {
				// don't check the format of ccExpiryMonth and ccExpiryYear
				// because
				// they are filled with dropdown lists with predefined
				// values
				payment.setCreditCardExpiry(ccExpiryMonth + "/" + ccExpiryYear);
			}

			hasErrorInDate = !payment.validateCCExpiry();
			if (hasErrorInDate) {
				paymentDetailsForm.recordError(messages.get("expiryDateError"));
			}
		}
		if (!userAgreed) {
			paymentDetailsForm.recordError(messages.get("agreeErrorMessage"));
		}

	}

	public Zone getPaymentZone() {
		return paymentZone;
	}

	/**
	 * Checks if it is need to show or hide the submit button.
	 * 
	 * @return true if there is at least one enrolment selected(show submit button), false id there no any enrolments selected(hide submit
	 * button).
	 */
	public boolean isHasAnyEnrolmentsSelected() {
		for (Enrolment enrolment : enrolments) {
			if (enrolment.getInvoiceLine() != null) {
				return true;
			}
		}
		return false;
	}

	public Money getTotalIncGst() {
		moneyFormat = FormatUtils.chooseMoneyFormat(totalIncGst);
		return totalIncGst;
	}

}
