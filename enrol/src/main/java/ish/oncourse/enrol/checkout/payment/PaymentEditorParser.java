package ish.oncourse.enrol.checkout.payment;

import org.apache.commons.lang.StringUtils;
import org.apache.tapestry5.services.Request;

public class PaymentEditorParser {
	private Request request;

	public void parse()
	{
		Field[] fields = Field.values();
		for (Field field : fields) {
		   String contact = StringUtils.trimToNull(request.getParameter(field.name()));

		}

	}


	public void setRequest(Request request) {
		this.request = request;
	}


	public static enum Field
	{
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
