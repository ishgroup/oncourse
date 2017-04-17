package ish.oncourse.enrol.checkout.payment;

import ish.common.types.CreditCardType;
import ish.oncourse.enrol.checkout.IFieldsParser;
import ish.oncourse.model.Contact;
import ish.oncourse.model.PaymentIn;
import ish.oncourse.services.payment.PaymentRequest;
import ish.oncourse.util.payment.CreditCardParser;
import ish.oncourse.util.payment.CreditCardValidator;
import ish.oncourse.utils.StringUtilities;
import ish.util.CreditCardUtil;
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

	private boolean isCorporatePass;

	private PaymentRequest paymentRequest;

	private Map<String, String> errors = new HashMap<>();

	public void parse() {
		if (paymentIn.isZeroPayment())
			parse(Field.zeroPaymentFields());
		else {
			parse(Field.noZeroPaymentFields());
			String expiryMonth = StringUtilities.cutToNull(request.getParameter(Field.expiryMonth.name()));
			String expiryYear = StringUtilities.cutToNull(request.getParameter(Field.expiryYear.name()));
			if (expiryMonth != null && expiryYear != null) {
				paymentRequest.setMonth(expiryMonth);
				paymentRequest.setMonth(expiryYear);
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
				String value = StringUtilities.cutToNull(request.getParameter(field.name()));
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
			case creditCardName:
				paymentIn.setCreditCardName(value);
				break;
			case creditCardNumber:
				value = value.replaceAll("\\D","");
				paymentIn.setCreditCardNumber(value);
				CreditCardParser parser = new CreditCardParser();
				CreditCardType creditCardType = parser.parser(value);
				if (creditCardType != null) {
					paymentIn.setCreditCardType(creditCardType);
					String errorMessage = paymentIn.validateCCNumber();
					if (errorMessage != null)
						errors.put(field.name(), errorMessage);
					paymentIn.setCreditCardNumber(CreditCardUtil.obfuscateCCNumber(value));
				} else
					errors.put(field.name(), messages.format(String.format(MESSAGE_KEY_TEMPLATE, Field.creditCardType.name())));
				break;
			case creditCardCVV:
				paymentIn.setCreditCardCVV(value);
				if (!paymentIn.validateCVV())
					errors.put(field.name(), messages.format(String.format(MESSAGE_KEY_TEMPLATE, field)));
				paymentIn.setCreditCardCVV(CreditCardUtil.obfuscateCVVNumber(value));
				break;
			case expiryMonth:
			case expiryYear:
			case userAgreed:
			case creditCardType:
				break;
			default:
				throw new IllegalArgumentException();
		}
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

	public void  setPaymentRequest(PaymentRequest paymentRequest) {
		this.paymentRequest = paymentRequest;
	} 

	public Map<String, String> getErrors() {
		return errors;
	}

	public void setCorporatePass(boolean corporatePass) {
		isCorporatePass = corporatePass;
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
			return new Field[]{Field.userAgreed};
		}

		public static Field[] noZeroPaymentFields() {
			return new Field[]{
					Field.creditCardName,
					Field.creditCardNumber,
					Field.creditCardCVV,
					Field.expiryMonth,
					Field.expiryYear,
					Field.userAgreed};
		}
	}
}
