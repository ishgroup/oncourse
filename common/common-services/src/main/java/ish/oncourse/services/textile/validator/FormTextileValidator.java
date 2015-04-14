package ish.oncourse.services.textile.validator;

import ish.oncourse.services.textile.TextileType;
import ish.oncourse.services.textile.TextileUtil;
import ish.oncourse.services.textile.attrs.FormTextileAttributes;
import ish.oncourse.services.textile.attrs.PopupListTextileAttributes;
import ish.oncourse.services.textile.attrs.RadioListTextileAttributes;
import ish.oncourse.services.textile.attrs.TextTextileAttributes;
import ish.oncourse.util.ValidationErrors;
import ish.oncourse.util.ValidationFailureType;
import org.apache.commons.validator.EmailValidator;

import java.util.Map;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class FormTextileValidator extends AbstractTextileValidator {

	@Override
	public String getFormatErrorMessage(String tag) {
		return "The rich text \""
				+ tag
				+ "\" doesn't match the pattern. the pattern is: {form name:\"e-mail us\" email:\"sales@acmecollege.com.au\"}"
				+ "{text label:\"Email address\" required:\"yes\" lines:\"yes\"}{form}";
	}

	@Override
	protected void initValidator() {
		textileType = TextileType.FORM;
	}

	@Override
	protected void specificTextileValidate(String tag, ValidationErrors errors) {
		Pattern pattern = Pattern.compile("\\{form");
		Matcher matcher = pattern.matcher(tag);
		if (matcher.groupCount() == 1) {
			errors.addFailure("Missing the closing {form}", ValidationFailureType.SYNTAX);
		}
		Pattern fieldPattern = Pattern.compile(TextileType.FORM_FIELDS_PATTERN);
		Matcher fieldMatcher = fieldPattern.matcher(tag);
		if (!fieldMatcher.find()) {
			errors.addFailure("The {form} tag must contain at least one {text} or {radiolist} or {popuplist} field",
					ValidationFailureType.SYNTAX);
		} else {
			do {
				String text = fieldMatcher.group();
				if (text.matches(TextileType.TEXT.getRegexp())) {
					if (!text.matches(TextileType.TEXT.getDetailedRegexp())) {

						errors.addFailure("The rich text " + text
								+ " doesn't match {text label:\"Reason for complaint\" required:true lines:8}",
								ValidationFailureType.SYNTAX);
					}
					TextileUtil.checkParamsUniquence(text, errors, TextileType.TEXT.getAttributes());
					TextileUtil.checkRequiredParams(text, errors, TextTextileAttributes.LABEL.getValue());
				}

				if (text.matches(TextileType.RADIOLIST.getRegexp())) {
					if (!text.matches(TextileType.RADIOLIST.getDetailedRegexp())) {

						errors.addFailure(
								"The rich text "
										+ text
										+ " doesn't match {radiolist label:\"Age range\" default:\"20-25\" options:\"20-25,26-30,31-35,36-40,41-45,46-50,50+\"}",
								ValidationFailureType.SYNTAX);
					}
					TextileUtil.checkParamsUniquence(text, errors, TextileType.RADIOLIST.getAttributes());
					TextileUtil.checkRequiredParams(text, errors, RadioListTextileAttributes.LABEL.getValue());
				}
				
				if (text.matches(TextileType.POPUPLIST.getRegexp())) {
					if (!text.matches(TextileType.POPUPLIST.getDetailedRegexp())) {

						errors.addFailure(
								"The rich text "
										+ text
										+ " doesn't match {popuplist label:\"Age range\" default:\"20-25\" options:\"20-25,26-30,31-35,36-40,41-45,46-50,50+\"}",
								ValidationFailureType.SYNTAX);
					}
					TextileUtil.checkParamsUniquence(text, errors, TextileType.POPUPLIST.getAttributes());
					TextileUtil.checkRequiredParams(text, errors, PopupListTextileAttributes.LABEL.getValue());
				}

			} while (fieldMatcher.find());
		}
		TextileUtil.checkRequiredParams(tag, errors, FormTextileAttributes.EMAIL.getValue(), FormTextileAttributes.NAME.getValue());
		Map<String, String> formParams = TextileUtil.getTagParams(tag, textileType.getAttributes());
		String email = formParams.get(FormTextileAttributes.EMAIL.getValue());

		boolean isValid = false;
		if (email != null) {
			isValid = true;
			EmailValidator validator = EmailValidator.getInstance();
			
			for (String emailValue : email.split(",")) {
				if (!validator.isValid(emailValue)) {
					isValid = false;
					break;
				}
			}
		}
		
		if (email != null && !isValid) {
			errors.addFailure("The email:" + email + " seems to be not valid.", ValidationFailureType.SYNTAX);
		}
	}

}
