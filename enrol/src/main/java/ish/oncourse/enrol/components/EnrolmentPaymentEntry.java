package ish.oncourse.enrol.components;

import ish.common.types.CreditCardType;
import ish.math.Money;
import ish.oncourse.enrol.pages.EnrolmentPaymentProcessing;
import ish.oncourse.model.Contact;
import ish.oncourse.model.Enrolment;
import ish.oncourse.model.EnrolmentStatus;
import ish.oncourse.model.Invoice;
import ish.oncourse.model.InvoiceLine;
import ish.oncourse.model.InvoiceStatus;
import ish.oncourse.model.PaymentIn;
import ish.oncourse.model.PaymentInLine;
import ish.oncourse.model.PaymentStatus;
import ish.oncourse.model.Preference;
import ish.oncourse.selectutils.ISHEnumSelectModel;
import ish.oncourse.selectutils.ListSelectModel;
import ish.oncourse.selectutils.ListValueEncoder;
import ish.oncourse.services.discount.IDiscountService;
import ish.oncourse.services.preference.IPreferenceService;

import java.math.BigDecimal;
import java.text.DecimalFormat;
import java.text.Format;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.List;
import java.util.concurrent.locks.ReentrantLock;

import org.apache.cayenne.ObjectContext;
import org.apache.cayenne.PersistenceState;
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
	 * Constants
	 */
	private static final String PAYMENT_AMOUNT_FORMAT = "###,##0.00";

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
	private IPreferenceService preferenceService;

	@Inject
	private IDiscountService discountService;

	/**
	 * Parameters
	 */
	@Parameter
	@Property
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
	private EnrolmentPaymentProcessing enrolmentPaymentProcessing;

	@Parameter
	private List<Enrolment> enrolments;
	private final ReentrantLock lock = new ReentrantLock();

	@SetupRender
	void beforeRender() {
		moneyFormat = new DecimalFormat(PAYMENT_AMOUNT_FORMAT);
		initYears();
		initEnrolmentDisclosure();

		initPayers();

		payersModel = new ListSelectModel<Contact>(payers, "fullName", propertyAccess);

		payersEncoder = new ListValueEncoder<Contact>(payers, "id", propertyAccess);

		cardTypeModel = new ISHEnumSelectModel(CreditCardType.class, messages);
	}

	private void initYears() {
		years = new ArrayList<Integer>();
		int currentYear = Calendar.getInstance().get(Calendar.YEAR);

		for (int i = 0; i < 10; i++) {
			years.add(currentYear + i);
		}
	}

	private void initEnrolmentDisclosure() {
		Preference enrolmentDisclosurePref = preferenceService
				.getPreferenceByKey("feature.enrolmentDisclosure");
		enrolmentDisclosure = enrolmentDisclosurePref.getValueString();

		if ("".equals(enrolmentDisclosure)) {
			enrolmentDisclosure = null;
		}
	}

	private void initPayers() {
		List<Contact> localPayers = new ArrayList<Contact>(payers.size());
		for (Contact payer : payers) {
			localPayers.add((Contact) payment.getObjectContext().localObject(payer.getObjectId(),
					payer));
		}

		payers = localPayers;
		payment.setContact(payers.get(0));
	}

	/**
	 * Iterates through all the enrolments selected(ie which has the related
	 * invoiceLine) and checks if the related class has any available places for
	 * enrolling.
	 * 
	 * @return true if all the selected classes are available for enrolling.
	 */
	public boolean isAllEnrolmentsAvailable() {
		for (Enrolment enrolment : enrolments) {
			if (enrolment.getInvoiceLine() != null
					&& !enrolment.getCourseClass().isHasAvailableEnrolmentPlaces()) {
				return false;
			}
		}
		return true;
	}

	public boolean isZeroPayment() {
		return totalIncGst.isZero();
	}

	public boolean isHasConcessionsCollege() {
		return Boolean.valueOf(preferenceService.getPreferenceByKey(
				"feature.concessionsInEnrolment").getValueString());
	}

	public boolean isHasConcessionsEnrolment() {
		for (Enrolment enrolment : enrolments) {
			if (!discountService.getConcessionDiscounts(enrolment.getCourseClass()).isEmpty()) {
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
		return isZeroPayment() ? "Confirm enrolment" : "Make Payment";
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
		return defaultTracker == null || !defaultTracker.inError(field) ? messages
				.get("validInput") : messages.get("validateInput");
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
	@OnEvent(component = "paymentDetailsForm", value = "success")
	Object submitted() {
		ObjectContext context = payment.getObjectContext();
		List<Object> objectsToDelete = new ArrayList<Object>();
		List<Enrolment>validEnrolments=new ArrayList<Enrolment>();
		if (!isZeroPayment()) {
			payment.setAmount(totalIncGst.toBigDecimal());
			BigDecimal totalGst = BigDecimal.ZERO;
			BigDecimal totalExGst = BigDecimal.ZERO;

			for (Enrolment e : enrolments) {
				if (e.getInvoiceLine() == null) {
					objectsToDelete.add(e);
				}else{
					validEnrolments.add(e);
				}
			}
			for (InvoiceLine invLine : invoice.getInvoiceLines()) {
				if (invLine.getEnrolment() == null) {
					objectsToDelete.add(invLine);
				} else {
					totalExGst = totalGst.add(invLine.getPriceTotalExTax().toBigDecimal());
					totalGst = totalGst.add(invLine.getPriceTotalIncTax()
							.subtract(invLine.getPriceTotalExTax()).toBigDecimal());

				}
			}

			invoice.setTotalExGst(totalExGst);
			invoice.setTotalGst(totalGst);

			PaymentInLine paymentInLine = context.newObject(PaymentInLine.class);
			paymentInLine.setInvoice(invoice);
			paymentInLine.setPaymentIn(payment);
			paymentInLine.setAmount(payment.getAmount());

			enrolmentPaymentProcessing.setPayment(payment);
			invoice.setContact(payment.getContact());
			payment.setStatus(PaymentStatus.IN_TRANSACTION);
			invoice.setStatus(InvoiceStatus.IN_TRANSACTION);
		} else {
			objectsToDelete.add(payment);
			enrolmentPaymentProcessing.setPayment(null);
		}
		context.deleteObjects(objectsToDelete);
		lock.lock();
		// block until checking and the change of state holds
		try {
			if (isAllEnrolmentsAvailable()) {
				for (Enrolment e : validEnrolments) {
					e.setStatus(EnrolmentStatus.IN_TRANSACTION);
				}
				context.commitChanges();
				enrolmentPaymentProcessing.setEnrolments(validEnrolments);
			} else {
				context.rollbackChanges();
				enrolmentPaymentProcessing.setEnrolments(null);
			}
		} finally {
			lock.unlock();
		}
		
		return enrolmentPaymentProcessing;
	}

	@OnEvent(component = "paymentDetailsForm", value = "failure")
	Object submitFailed() {
		return paymentZone.getBody();
	}

	@OnEvent(component = "paymentDetailsForm", value = "validate")
	void validate() {
		if (payment.getCreditCardType() == null) {
			paymentDetailsForm.recordError(cardTypeSelect, messages.get("cardTypeErrorMessage"));
		}
		String creditCardName = payment.getCreditCardName();
		if (creditCardName == null || creditCardName.equals("")) {
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
			Calendar cal = Calendar.getInstance();
			cal.set(Calendar.MONTH, Integer.parseInt(ccExpiryMonth) - 1);
			cal.set(Calendar.YEAR, ccExpiryYear);
			if (cal.getTime().before(new Date())) {
				hasErrorInDate = true;
			}
		}
		if (hasErrorInDate) {
			paymentDetailsForm.recordError(messages.get("expiryDateError"));
		} else {
			payment.setCreditCardExpiry(ccExpiryMonth + "/" + ccExpiryYear);
		}
		if (!userAgreed) {
			paymentDetailsForm.recordError(messages.get("agreeErrorMessage"));
		}
	}

	public Zone getPaymentZone() {
		return paymentZone;
	}
}
