package ish.oncourse.webservices.pages;

import ish.common.types.CreditCardType;
import ish.math.Money;
import ish.oncourse.model.Contact;
import ish.oncourse.model.Invoice;
import ish.oncourse.model.PaymentIn;
import ish.oncourse.model.PaymentInLine;
import ish.oncourse.model.PaymentTransaction;
import ish.oncourse.services.payment.IPaymentService;
import ish.oncourse.services.paymentexpress.IPaymentGatewayService;
import ish.oncourse.services.persistence.ICayenneService;

import java.text.DecimalFormat;
import java.text.Format;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.List;

import org.apache.cayenne.ObjectContext;
import org.apache.tapestry5.Field;
import org.apache.tapestry5.ValidationTracker;
import org.apache.tapestry5.annotations.InjectComponent;
import org.apache.tapestry5.annotations.OnEvent;
import org.apache.tapestry5.annotations.Persist;
import org.apache.tapestry5.annotations.Property;
import org.apache.tapestry5.annotations.SetupRender;
import org.apache.tapestry5.corelib.components.Form;
import org.apache.tapestry5.corelib.components.RadioGroup;
import org.apache.tapestry5.corelib.components.Select;
import org.apache.tapestry5.corelib.components.TextField;
import org.apache.tapestry5.corelib.components.Zone;
import org.apache.tapestry5.ioc.Messages;
import org.apache.tapestry5.ioc.annotations.Inject;
import org.apache.tapestry5.services.Request;

public class Payment {

	private static final String PAYMENT_AMOUNT_FORMAT = "###,##0.00";

	@Inject
	private IPaymentService paymentService;

	@Inject
	private IPaymentGatewayService paymentGatewayService;

	@Inject
	private ICayenneService cayenneService;

	@Inject
	private Request request;

	@Inject
	private Messages messages;

	@Property
	private PaymentTransaction paymentTransaction;

	@Property
	@Persist
	private PaymentIn payment;

	@Property
	@Persist
	private Money totalIncGst;

	@Property
	@Persist
	private String enrolmentDisclosure;

	@Property
	@Persist
	private Contact payer;

	@Property
	@Persist
	private Format moneyFormat;

	@Property
	private String cardNumberErrorMessage;

	@Property
	private String ccExpiryMonth;

	@Property
	private Integer ccExpiryYear;

	@Property
	@Persist
	private List<Integer> years;

	@InjectComponent
	@Property
	private Form paymentDetailsForm;

	@InjectComponent
	private Zone paymentZone;

	@InjectComponent
	@Property
	private RadioGroup cardTypeField;

	@InjectComponent
	@Property
	private TextField cardName;

	@InjectComponent
	@Property
	private TextField cardNumber;

	@InjectComponent
	@Property
	private TextField cardcvv;

	@InjectComponent
	@Property
	private Select expiryMonth;

	@InjectComponent
	@Property
	private Select expiryYear;

	@Property
	@Persist
	private List<Invoice> invoices;

	@Property
	private Invoice invoice;

	/**
	 * Indicates if the submit was because of pressing the submit button.
	 */
	private boolean isSubmitted;

	@SetupRender
	public void beforeRender() {
		this.moneyFormat = new DecimalFormat(PAYMENT_AMOUNT_FORMAT);
		
		initYears();

		String referenceId = request.getParameter(PaymentTransaction.REFERENCE_ID_PARAM);

		if (referenceId != null) {
			this.paymentTransaction = paymentService.paymentTransactionByReferenceId(referenceId);
			this.payment = paymentTransaction.getPayment();
			this.totalIncGst = new Money(payment.getAmount());
			this.payer = paymentTransaction.getPayment().getContact();
			initInvoices();
		}
	}

	private void initInvoices() {
		this.invoices = new ArrayList<Invoice>();
		for (PaymentInLine paymentLine : this.payment.getPaymentInLines()) {
			this.invoices.add(paymentLine.getInvoice());
		}
	}

	public CreditCardType getMasterCard() {
		return CreditCardType.MASTERCARD;
	}

	public CreditCardType getVisa() {
		return CreditCardType.VISA;
	}

	public CreditCardType getAmex() {
		return CreditCardType.AMEX;
	}

	public CreditCardType getBankCard() {
		return CreditCardType.BANKCARD;
	}

	public String getPaymentTitle() {
		return messages.format("payment.title", payer.getGivenName(), payer.getFamilyName());
	}

	public String getInvoiceDescription() {
		return messages.format("invoice.desc", invoice.getInvoiceNumber());
	}

	private void initYears() {
		years = new ArrayList<Integer>();
		int currentYear = Calendar.getInstance().get(Calendar.YEAR);

		for (int i = 0; i < 10; i++) {
			years.add(currentYear + i);
		}
	}

	public String getSubmitButtonText() {
		return messages.get("submit.button.text.payment");
	}

	public String getCardTypeClass() {
		return getInputSectionClass(cardTypeField);
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

	public String getCardExpireClass() {
		return getInputSectionClass(expiryMonth);
	}

	private String getInputSectionClass(Field field) {
		ValidationTracker defaultTracker = paymentDetailsForm.getDefaultTracker();
		return defaultTracker == null || !defaultTracker.inError(field) ? "pay-form-line" : "pay-form-line error";
	}

	/**
	 * Invokes when the {@link #paymentDetailsForm} is attempted to submit.
	 */
	@OnEvent(component = "paymentDetailsForm", value = "validate")
	void validate() {
		if (!isSubmitted) {
			return;
		}
		
		if (!payment.validateCCType()) {
			paymentDetailsForm.recordError(cardTypeField, messages.get("cardTypeErrorMessage"));
		}
		if (!payment.validateCCName()) {
			paymentDetailsForm.recordError(cardName, messages.get("cardNameErrorMessage"));
		}

		cardNumberErrorMessage = payment.validateCCNumber();
		if (cardNumberErrorMessage != null) {
			paymentDetailsForm.recordError(cardNumber, cardNumberErrorMessage);
		}

		if (!payment.validateCVV()) {
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
			paymentDetailsForm.recordError(expiryMonth, messages.get("expiryDateError"));
			paymentDetailsForm.recordError(expiryYear, messages.get("expiryDateError"));
		}
	}

	@OnEvent(component = "paymentSubmit", value = "selected")
	void submitButtonPressed() {
		isSubmitted = true;
	}

	@OnEvent(component = "paymentDetailsForm", value = "failure")
	Object submitFailed() {
		return paymentZone.getBody();
	}

	@OnEvent(component = "paymentDetailsForm", value = "success")
	Object submitted() {

		ObjectContext objectContext = cayenneService.newContext();
		PaymentIn localPaymentIn = (PaymentIn) objectContext.localObject(this.payment.getObjectId(), this.payment);
		paymentGatewayService.performGatewayOperation(localPaymentIn);
		objectContext.commitChanges();

		return paymentZone.getBody();
	}
}
