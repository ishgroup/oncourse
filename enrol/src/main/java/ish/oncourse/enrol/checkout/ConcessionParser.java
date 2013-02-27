package ish.oncourse.enrol.checkout;

import ish.oncourse.enrol.components.checkout.concession.ConcessionEditor;
import ish.oncourse.enrol.pages.Checkout;
import ish.oncourse.model.StudentConcession;
import ish.oncourse.util.FormatUtils;
import ish.oncourse.util.HTMLUtils;
import ish.oncourse.util.MessagesNamingConvention;
import org.apache.commons.lang.StringUtils;
import org.apache.tapestry5.ioc.Messages;
import org.apache.tapestry5.ioc.internal.util.MessagesImpl;
import org.apache.tapestry5.services.Request;

import java.text.DateFormat;
import java.text.ParseException;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;


public class ConcessionParser implements IFieldsParser {

	private StudentConcession studentConcession;

	private Request request;

	private Messages messages;

	private Map<String, String> errors = new HashMap<String, String>();

	private DateFormat dateFormat;

	public void parse() {
		String concessionNumber = StringUtils.trimToNull(request.getParameter(Field.concessionNumber.name()));
		String expiresOn = StringUtils.trimToNull(request.getParameter(Field.expiresOn.name()));
		String concessionAgree = StringUtils.trimToNull(request.getParameter(Field.concessionAgree.name()));

		parseConcessionNumber(concessionNumber);
		parseExpiresOn(expiresOn);
		parseConcessionAgree(concessionAgree);
	}

	private void parseConcessionAgree(String concessionAgree) {
		if (!HTMLUtils.parserBooleanValue(concessionAgree)) {
			errors.put(Field.concessionAgree.name(), getErrorMessageBy(Field.concessionAgree));
		}
	}

	private void parseExpiresOn(String expiresOn) {
		String error = null;
		if (studentConcession.getConcessionType().getHasExpiryDate()) {
			if (expiresOn == null) {
				error = getErrorMessageBy(Field.expiresOn, studentConcession.getConcessionType().getName());
			} else {
				try {
					Date expiry = dateFormat.parse(expiresOn);
					if (new Date().compareTo(expiry) > 0) {
						error =  getErrorMessageWithSuffixBy(Field.expiresOn, "pastDate");
					}
					studentConcession.setExpiresOn(expiry);
				} catch (ParseException e) {
					error =  getErrorMessageWithSuffixBy(Field.expiresOn, "invalidFormat");
				}
			}
		}
		if (error != null)
			errors.put(Field.expiresOn.name(), error);
	}

	private void parseConcessionNumber(String number) {
		if (studentConcession.getConcessionType().getHasConcessionNumber()) {
			if (number == null) {
				errors.put(Field.concessionNumber.name(), getErrorMessageBy(Field.concessionNumber, (Object)studentConcession.getConcessionType().getName()));
			}
			else
			studentConcession.setConcessionNumber(number);
		}
	}

	private String getErrorMessageBy(Field field, Object... params) {
		return messages.format(String.format(MessagesNamingConvention.MESSAGE_KEY_TEMPLATE, field.name()),
				params);
	}

	private String getErrorMessageWithSuffixBy(Field field, String suffix, Object... params) {
		return messages.format(String.format(MessagesNamingConvention.MESSAGE_KEY_TEMPLATE_WITH_SUFFIX, field.name(), suffix),
				params);
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

	public void setDateFormat(DateFormat dateFormat) {
		this.dateFormat = dateFormat;
	}

	public StudentConcession getStudentConcession() {
		return studentConcession;
	}

	public void setStudentConcession(StudentConcession studentConcession) {
		this.studentConcession = studentConcession;
	}

	public static ConcessionParser newInstance(Request request, StudentConcession studentConcession, String timeZone) {
		ConcessionParser parser = new ConcessionParser();
		parser.setStudentConcession(studentConcession);
		parser.setMessages(MessagesImpl.forClass(ConcessionEditor.class));
		parser.setRequest(request);
		parser.setDateFormat(FormatUtils.getDateFormat(Checkout.DATE_FIELD_PARSE_FORMAT, timeZone));
		return parser;
	}


	public static enum Field {
		concessionNumber,
		expiresOn,
		concessionAgree,
	}
}
