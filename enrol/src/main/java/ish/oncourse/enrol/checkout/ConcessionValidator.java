package ish.oncourse.enrol.checkout;

import ish.oncourse.model.ConcessionType;
import org.apache.commons.lang.StringUtils;
import org.apache.tapestry5.ioc.Messages;
import org.apache.tapestry5.services.Request;

import java.text.DateFormat;
import java.text.ParseException;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;

public class ConcessionValidator {


	public static final String FIELD_NAME_CONCESSION_NUMBER = "concession-number";
	public static final String FIELD_NAME_CONCESSION_EXPIRY = "concession-expiry";
	public static final String FIELD_NAME_CONCESSION_AGREE = "concession-agree";

	public static final String MESSAGE_KEY_numberRequiredMessage = "numberRequiredMessage";
	public static final String MESSAGE_KEY_expiryRequiredMessage = "expiryRequiredMessage";
	public static final String MESSAGE_KEY_expiryPastDateMessage = "expiryPastDateMessage";
	public static final String MESSAGE_KEY_expiryInvalidFormat = "expiryInvalidFormat";
	public static final String MESSAGE_KEY_certificationRequiredMessage = "certificationRequiredMessage";


	private ConcessionType concessionType;
	private Request request;

	private Messages messages;

	private Map<String,String> errors = new HashMap<String, String>();

	private String number;
	private Date expiry;

	private DateFormat dateFormat;

	public void validate()
	{
		number = StringUtils.trimToNull(request.getParameter(ConcessionValidator.FIELD_NAME_CONCESSION_NUMBER));
		String expiry = StringUtils.trimToNull(request.getParameter(ConcessionValidator.FIELD_NAME_CONCESSION_EXPIRY));
		String agree = StringUtils.trimToNull(request.getParameter(ConcessionValidator.FIELD_NAME_CONCESSION_AGREE));

		validateNumber(concessionType, number);
		validateExpiry(concessionType, expiry);
		validateAgree(concessionType, agree);
	}

	private void validateNumber(ConcessionType type, String number)
	{

		if (type.getHasConcessionNumber()) {
			if (number == null) {
				errors.put(FIELD_NAME_CONCESSION_NUMBER, String.format(messages.get(MESSAGE_KEY_numberRequiredMessage),
						concessionType.getName()));
			}
		}
	}

	private void validateExpiry(ConcessionType type, String expiry)
	{
		String error = null;
		if (type.getHasExpiryDate()) {
			if (expiry == null) {
				error =  String.format(messages.get(MESSAGE_KEY_expiryRequiredMessage), type.getName());
			}
			else
			{
				try {
					this.expiry = dateFormat.parse(expiry);
					if (new Date().compareTo(this.expiry) > 0) {
						error = messages.get(MESSAGE_KEY_expiryPastDateMessage);
					}
				} catch (ParseException e) {
					error = messages.get(MESSAGE_KEY_expiryInvalidFormat);
				}
			}
		}
		if (error != null)
			errors.put(FIELD_NAME_CONCESSION_EXPIRY,error);
	}

	private void validateAgree(ConcessionType type, String agree)
	{
		if (agree == null || !agree.equalsIgnoreCase("on")) {
			errors.put(FIELD_NAME_CONCESSION_AGREE,messages.get(MESSAGE_KEY_certificationRequiredMessage));
		}
	}


	public ConcessionType getConcessionType() {
		return concessionType;
	}

	public void setConcessionType(ConcessionType concessionType) {
		this.concessionType = concessionType;
	}


	public void setMessages(Messages messages) {
		this.messages = messages;
	}

	public void setRequest(Request request) {
		this.request = request;
	}

	public Map<String, String> getErrors() {
		return errors;
	}

	public String getNumber() {
		return number;
	}

	public Date getExpiry() {
		return expiry;
	}

	public void setDateFormat(DateFormat dateFormat) {
		this.dateFormat = dateFormat;
	}
}
