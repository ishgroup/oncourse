package ish.oncourse.enrol.components;

import ish.common.types.CreditCardType;
import ish.oncourse.enrol.pages.EnrolmentPaymentProcessing;
import ish.oncourse.model.Contact;
import ish.oncourse.model.Invoice;
import ish.oncourse.model.PaymentIn;
import ish.oncourse.model.Preference;
import ish.oncourse.selectutils.ISHEnumSelectModel;
import ish.oncourse.selectutils.ListSelectModel;
import ish.oncourse.selectutils.ListValueEncoder;
import ish.oncourse.services.preference.IPreferenceService;

import java.math.BigDecimal;
import java.text.DecimalFormat;
import java.text.Format;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.List;

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

	/**
	 * Parameters
	 */
	@Parameter
	@Property
	private BigDecimal totalIncGst;

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
	

	@SetupRender
	void beforeRender() {
		moneyFormat = new DecimalFormat(PAYMENT_AMOUNT_FORMAT);
		initYears();
		initEnrolmentDisclosure();

		initPayers();

		payersModel = new ListSelectModel<Contact>(payers, "fullName",
				propertyAccess);

		payersEncoder = new ListValueEncoder<Contact>(payers, "id",
				propertyAccess);

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
			localPayers.add((Contact) payment.getObjectContext().localObject(
					payer.getObjectId(), payer));
		}

		payers = localPayers;
		payment.setContact(payers.get(0));
	}

	public boolean isAllEnrolmentsAvailable() {
		// check if some of the enrolments are corrupted
		return true;
	}

	public boolean isZeroPayment() {
		// TODO payment.isZeroTotalIncGst
		return false;
	}

	public boolean isHasConcessionsCollege() {
		// feature.concessionsInEnrolment
		return true;
	}

	public boolean isHasConcessionsEnrolment() {
		// enrolmentController.hasConcessionPricing
		return true;
	}

	public boolean isHasStudentWithoutConcessions() {
		// enrolmentController.hasStudentWithoutConcession
		return true;
	}

	public String getSubmitButtonText() {
		// ~payment.isZeroTotalIncGst ? 'Confirm enrolment' : 'Make Payment'
		return "Make Payment";
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
		ValidationTracker defaultTracker = paymentDetailsForm
				.getDefaultTracker();
		return defaultTracker == null || !defaultTracker.inError(field) ? messages
				.get("validInput") : messages.get("validateInput");
	}

	@OnEvent(component = "paymentDetailsForm", value = "success")
	Object submitted() {
		invoice.setContact(payment.getContact());
		enrolmentPaymentProcessing.setPayment(payment);
		return enrolmentPaymentProcessing;
	}

	@OnEvent(component = "paymentDetailsForm", value = "failure")
	Object submitFailed() {
		return paymentZone.getBody();
	}

	@OnEvent(component = "paymentDetailsForm", value = "validate")
	void validate() {
		if (payment.getCreditCardType() == null) {
			paymentDetailsForm.recordError(cardTypeSelect,
					messages.get("cardTypeErrorMessage"));
		}
		String creditCardName = payment.getCreditCardName();
		if (creditCardName == null || creditCardName.equals("")) {
			paymentDetailsForm.recordError(cardName,
					messages.get("cardNameErrorMessage"));
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
