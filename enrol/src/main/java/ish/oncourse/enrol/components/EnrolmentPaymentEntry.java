package ish.oncourse.enrol.components;

import ish.common.payment.cc.CreditCardType;
import ish.oncourse.enrol.selectutils.ListSelectionModel;
import ish.oncourse.enrol.selectutils.ListValueEncoder;
import ish.oncourse.model.Contact;
import ish.oncourse.model.PaymentIn;
import java.math.BigDecimal;
import java.text.DecimalFormat;
import java.text.Format;
import java.util.ArrayList;
import java.util.List;

import org.apache.tapestry5.Block;
import org.apache.tapestry5.Field;
import org.apache.tapestry5.ValidationTracker;
import org.apache.tapestry5.annotations.InjectComponent;
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
import org.apache.tapestry5.util.AbstractSelectModel;
import org.apache.tapestry5.util.EnumSelectModel;

public class EnrolmentPaymentEntry {

	@Parameter
	@Property
	private BigDecimal totalIncGst;

	@Property
	@Persist
	private Format moneyFormat;

	@InjectComponent
	private Form paymentDetailsForm;

	@Parameter
	@Property
	private List<Contact> payers;

	@Property
	@Persist
	private ListSelectionModel<Contact> payersModel;

	@Property
	@Persist
	private ListValueEncoder<Contact> payersEncoder;

	@Property
	@Parameter
	private PaymentIn payment;

	private static final String VALID_CLASS = "valid";

	private static final String VALIDATE_CLASS = "validate";

	@Inject
	private PropertyAccess propertyAccess;
	@InjectComponent
	private Select cardTypeSelect;

	@Inject
	private Messages messages;

	@InjectComponent
	private Zone paymentZone;

	@Property
	@Persist
	private AbstractSelectModel cardTypeModel;
	@InjectComponent
	private TextField cardName;

	@SetupRender
	void beforeRender() {
		moneyFormat = new DecimalFormat("###,##0.00");
		List<Contact> localPayers = new ArrayList<Contact>(payers.size());
		for (Contact payer : payers) {
			localPayers.add((Contact) payment.getObjectContext().localObject(
					payer.getObjectId(), payer));
		}
		payers = localPayers;
		payment.setContact(payers.get(0));
		payersModel = new ListSelectionModel<Contact>(payers, "fullName",
				propertyAccess);
		payersEncoder = new ListValueEncoder<Contact>(payers, "id",
				propertyAccess);
		cardTypeModel = new EnumSelectModel(CreditCardType.class, messages);
	}

	public boolean isAllEnrolmentsAvailable() {
		// check if some of the enrolments are corrupted
		return true;
	}

	public boolean isZeroPayment() {
		// TODO payment.isZeroTotalIncGst
		return false;
	}

	public boolean isHasEnrolmentDisclosure() {
		// <wo:PreferenceConditional college="$myCollege"
		// key="feature.enrolmentDisclosure">
		return true;
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

	private String getInputSectionClass(Field field) {
		ValidationTracker defaultTracker = paymentDetailsForm
				.getDefaultTracker();
		return defaultTracker == null || !defaultTracker.inError(field) ? VALID_CLASS
				: VALIDATE_CLASS;
	}

	@OnEvent(component = "paymentDetailsForm", value = "success")
	Object submitted() {
		return paymentZone.getBody();
	}

	@OnEvent(component = "paymentDetailsForm", value = "failure")
	Block submitFailed() {
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
	}
}
