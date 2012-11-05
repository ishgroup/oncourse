package ish.oncourse.enrol.checkout.payment;

import ish.common.types.CreditCardType;
import ish.oncourse.enrol.checkout.IFieldsParser;
import ish.oncourse.model.Contact;
import ish.oncourse.model.PaymentIn;
import org.apache.commons.lang.StringUtils;
import org.apache.tapestry5.ioc.Messages;
import org.apache.tapestry5.services.Request;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class PaymentEditorParser implements IFieldsParser {
	private Request request;
	private List<Contact> contacts;
	private Messages messages;
	private PaymentIn paymentIn;

	private Map<String, String> errors = new HashMap<String, String>();

	public void parse() {
		Field[] fields = Field.values();
		for (Field field : fields) {
			if (field != Field.expiryMonth && field != Field.expiryYear) {
				String value = StringUtils.trimToNull(request.getParameter(field.name()));
				if (value != null) {
					setValue(field, value);
				} else {
					errors.put(field.name(), messages.format(String.format(KEY_FIELD_ERROR_TEMPLATE, field.name())));
				}
			}
		}

		String expiryMonth = StringUtils.trimToNull(request.getParameter(Field.expiryMonth.name()));
		String expiryYear = StringUtils.trimToNull(request.getParameter(Field.expiryYear.name()));
		if (expiryMonth == null || expiryYear == null)
			errors.put(Field.expiryMonth.name(), messages.format(String.format(KEY_FIELD_ERROR_TEMPLATE, Field.expiryMonth.name())));
		paymentIn.setCreditCardExpiry(expiryMonth + "/" + expiryYear);
		if (!paymentIn.validateCCExpiry())
			errors.put(Field.expiryMonth.name(), messages.format(String.format(KEY_FIELD_ERROR_TEMPLATE, Field.expiryMonth.name())));
	}

	private void setValue(Field field, String value) {
		switch (field) {
			case contact:
				Long id = Long.valueOf(value);
				paymentIn.setContact(getContactBy(id));
				break;
			case creditCardType:
				paymentIn.setCreditCardType(CreditCardType.valueOf(value));
				if (!paymentIn.validateCCType())
					errors.put(field.name(), messages.format(String.format(KEY_FIELD_ERROR_TEMPLATE, field.name())));
				break;
			case creditCardName:
				paymentIn.setCreditCardName(value);
				break;
			case creditCardNumber:
				paymentIn.setCreditCardNumber(value);
				String errorMessage = paymentIn.validateCCNumber();
				if (errorMessage != null)
					errors.put(field.name(), errorMessage);
				break;
			case creditCardCVV:
				paymentIn.setCreditCardCVV(value);
				if (!paymentIn.validateCVV())
					errors.put(field.name(), messages.format(String.format(KEY_FIELD_ERROR_TEMPLATE, field)));
				break;
			case expiryMonth:
			case expiryYear:
				paymentIn.setCreditCardExpiry(value);
				break;
			case userAgreed:
				break;
			default:
				throw new IllegalArgumentException();
		}
	}

	private Contact getContactBy(Long id) {
		for (Contact contact : contacts) {
			if (contact.getId().equals(id))
				return contact;
		}
		throw new IllegalArgumentException();
	}


	public void setRequest(Request request) {
		this.request = request;
	}

	public void setContacts(List<Contact> contacts) {
		this.contacts = contacts;
	}

	public void setMessages(Messages messages) {
		this.messages = messages;
	}

	public void setPaymentIn(PaymentIn paymentIn) {
		this.paymentIn = paymentIn;
	}

	public Map<String, String> getErrors() {
		return errors;
	}


	public static enum Field {
		contact,
		creditCardType,
		creditCardName,
		creditCardNumber,
		creditCardCVV,
		expiryMonth,
		expiryYear,
		userAgreed
	}
}
