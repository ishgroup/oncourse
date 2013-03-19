package ish.oncourse.enrol.checkout.payment;

import ish.common.types.CreditCardType;
import ish.oncourse.enrol.checkout.IFieldsParser;
import ish.oncourse.model.Contact;
import ish.oncourse.model.PaymentIn;
import ish.oncourse.util.payment.CreditCardParser;
import org.apache.commons.lang.StringUtils;
import org.apache.tapestry5.ioc.Messages;
import org.apache.tapestry5.services.Request;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import static ish.oncourse.util.MessagesNamingConvention.MESSAGE_KEY_TEMPLATE;

public class PaymentEditorParser implements IFieldsParser {
	private Request request;
	private List<Contact> contacts;
	private Messages messages;
	private PaymentIn paymentIn;

	private boolean newPayer;

	private Map<String, String> errors = new HashMap<String, String>();

	public void parse() {
		if (paymentIn.isZeroPayment())
			parse(Field.zeroPaymentFields());
		else {
			parse(Field.noZeroPaymentFields());
			String expiryMonth = StringUtils.trimToNull(request.getParameter(Field.expiryMonth.name()));
			String expiryYear = StringUtils.trimToNull(request.getParameter(Field.expiryYear.name()));
			if (expiryMonth != null && expiryYear != null) {
				paymentIn.setCreditCardExpiry(expiryMonth + "/" + expiryYear);
				if (!paymentIn.validateCCExpiry())
					errors.put(Field.expiryMonth.name(), messages.format(String.format(MESSAGE_KEY_TEMPLATE, Field.expiryMonth.name())));
			} else
				errors.put(Field.expiryMonth.name(), messages.format(String.format(MESSAGE_KEY_TEMPLATE, Field.expiryMonth.name())));
		}
	}

	private void parse(Field[] fields) {
		for (Field field : fields) {
			if (field != Field.expiryMonth && field != Field.expiryYear) {
				String value = StringUtils.trimToNull(request.getParameter(field.name()));
				if (value != null) {
					setValue(field, value);
				} else {
					errors.put(field.name(), messages.format(String.format(MESSAGE_KEY_TEMPLATE, field.name())));
				}
			}
		}
	}

	private void setValue(Field field, String value) {
		switch (field) {
			case contact:
				Long id = Long.valueOf(value);
				if (id.equals(Long.MIN_VALUE))
					newPayer = true;
				else
					paymentIn.setContact(getContactBy(id));
				break;
			case creditCardName:
				paymentIn.setCreditCardName(value);
				break;
			case creditCardNumber:
				paymentIn.setCreditCardNumber(value);
				CreditCardParser parser = new CreditCardParser();
				CreditCardType creditCardType = parser.parser(value);
				if (creditCardType != null) {
					paymentIn.setCreditCardType(creditCardType);
					String errorMessage = paymentIn.validateCCNumber();
					if (errorMessage != null)
						errors.put(field.name(), errorMessage);
				} else
					errors.put(field.name(), messages.format(String.format(MESSAGE_KEY_TEMPLATE, Field.creditCardType.name())));
				break;
			case creditCardCVV:
				paymentIn.setCreditCardCVV(value);
				if (!paymentIn.validateCVV())
					errors.put(field.name(), messages.format(String.format(MESSAGE_KEY_TEMPLATE, field)));
				break;
			case expiryMonth:
			case expiryYear:
				paymentIn.setCreditCardExpiry(value);
				break;
			case userAgreed:
			case creditCardType:
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

	public boolean isNewPayer() {
		return newPayer;
	}


	public static enum Field {
		contact,
		creditCardType,
		creditCardName,
		creditCardNumber,
		creditCardCVV,
		expiryMonth,
		expiryYear,
		userAgreed;

		public static Field[] zeroPaymentFields() {
			return new Field[]{Field.userAgreed, Field.contact};
		}

		public static Field[] noZeroPaymentFields() {
			return new Field[]{Field.contact,
					Field.creditCardName,
					Field.creditCardNumber,
					Field.creditCardCVV,
					Field.expiryMonth,
					Field.expiryYear,
					Field.userAgreed};
		}
	}
}
