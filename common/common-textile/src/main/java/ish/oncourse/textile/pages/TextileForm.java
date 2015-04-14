package ish.oncourse.textile.pages;

import ish.oncourse.model.TextileFormField;
import ish.oncourse.services.encrypt.EncryptionService;
import ish.oncourse.services.mail.IMailService;
import ish.oncourse.services.preference.PreferenceController;
import ish.oncourse.services.site.IWebSiteService;
import ish.oncourse.services.textile.TextileUtil;
import org.apache.commons.validator.EmailValidator;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.apache.tapestry5.StreamResponse;
import org.apache.tapestry5.annotations.Property;
import org.apache.tapestry5.annotations.SetupRender;
import org.apache.tapestry5.ioc.annotations.Inject;
import org.apache.tapestry5.services.Request;
import org.apache.tapestry5.util.TextStreamResponse;

import java.util.Collections;
import java.util.Comparator;
import java.util.List;

public class TextileForm {

	private final static Logger logger = LogManager.getLogger();

	private static final String CONTENT_TYPE = "text/json";

	private static final String FORM_INDEX_ATTRIBUTE = "FORM_INDEX_ATTRIBUTE";

	private static final String ERROR_MESSAGE = "Contact support@ish.com.au, some exception occured.";

	private static final String EMAIL_FORM_NAME = "emailForm";

	private static final String EMAIL_DELIMITER = ",";

	@Property
	private static final String EMAIL_FIELD_NAME = "ish.formEmailTo";

	@Inject
	private Request request;

	@Inject
	private PreferenceController preferenceController;

	@Inject
	private IWebSiteService webSiteService;

	@Inject
	private IMailService mailService;

	@Inject
	private EncryptionService encryptionService;

	@Property
	private String formName;

	@Property
	private String emailValue;

	@Property
	private String afterFieldsMarkUp;

	@Property
	private boolean shouldSend;

	@Property
	private List<TextileFormField> fields;

	@Property
	private TextileFormField field;

	@Property
	private int index;

	@Property
	private String option;

	@Property
	private String url;

	@Property
	private Integer formIndex;

	@SetupRender
	boolean beginRender() {

		fields = (List<TextileFormField>) request.getAttribute(TextileUtil.TEXTILE_FORM_PAGE_FIELDS_PARAM);
		if (fields == null) {
			return false;
		}
		formName = (String) request.getAttribute(TextileUtil.TEXTILE_FORM_PAGE_NAME_PARAM);
		if (formName == null) {
			formName = EMAIL_FORM_NAME;
		}

		emailValue = (String) request.getAttribute(TextileUtil.TEXTILE_FORM_PAGE_EMAIL_PARAM);
		EmailValidator validator = EmailValidator.getInstance();
		String emailFromAddress = preferenceController.getEmailFromAddress();

		boolean isValid = false;
		if (emailValue != null) {
			isValid = true;
			String[] emailValues = emailValue.split(EMAIL_DELIMITER);
			for (String email : emailValues) {
				if (!validator.isValid(email)) {
					isValid = false;
					break;
				}
			}
		}

		shouldSend = emailValue != null && isValid && emailFromAddress != null
				&& validator.isValid(emailFromAddress);
		if (emailValue != null) {
			try {

				byte[] encryptedBytes = encryptionService.encrypt(emailValue);
				emailValue = String.valueOf(encryptedBytes[0]);
				for (int i = 1; i < encryptedBytes.length; i++) {
					emailValue += "_" + encryptedBytes[i];
				}
			} catch (Exception e) {
				logger.error("Problem with processing email", e);
			}
		} else {
			logger.error("The email for this form is not defined.Host: {}. Path {}",
					request != null ? request.getServerName() : "undefined",
					request != null ? request.getPath() : "undefined"
			);
		}

		afterFieldsMarkUp = (String) request.getAttribute(TextileUtil.TEXTILE_FORM_PAGE_AFTER_FIELDS_PARAM);
		url = (String) request.getAttribute(TextileUtil.TEXTILE_FORM_PAGE_URL_PARAM);

		Object attribute = request.getAttribute(FORM_INDEX_ATTRIBUTE);
		if (attribute == null) {
			formIndex = 0;
		} else {
			formIndex = ((Integer) attribute) + 1;
		}
		request.setAttribute(FORM_INDEX_ATTRIBUTE, formIndex);
		return true;

	}

	/**
	 * Handles ajax call to send.
	 *
	 * @return json-status
	 */
	StreamResponse onActionFromSend() {

        if (request.isXHR())
        {
            String emailTo = request.getParameter(EMAIL_FIELD_NAME);
            if (emailTo != null) {
                try {
                    String[] splitted = emailTo.split("_");
                    byte[] encryptedBytes = new byte[splitted.length];
                    for (int i = 0; i < splitted.length; i++) {
                        encryptedBytes[i] = Byte.parseByte(splitted[i]);
                    }
                    emailTo = encryptionService.decrypt(encryptedBytes);

                    if (emailTo != null) {
                        for (String email : emailTo.split(EMAIL_DELIMITER)) {
                            if (EmailValidator.getInstance().isValid(email)) {
                            List<String> parameterNames = request.getParameterNames();
                            String pagePath = request.getParameter("pagePath");
                            // FIXME extract js to the separate file(where "&" is
							// allowed to be used) and remove this
							if (pagePath == null) {
								pagePath = request.getParameter("amp;pagePath");
							}
							final String pagePathProperty = pagePath;
                            Collections.sort(parameterNames, new Comparator<String>() {
								@Override
								public int compare(String parameter1, String parameter2) {
									String [] splittedParam1 = parameter1.split("_");
									String [] splittedParam2 = parameter2.split("_");
									if (splittedParam1.length == 3 && splittedParam2.length == 3) {
										try {
											Integer parameter1Index = Integer.parseInt(splittedParam1[0]),
											parameter2Index = Integer.parseInt(splittedParam2[0]);
											return parameter1Index.compareTo(parameter2Index);
										} catch (Exception e) {
											logger.error("Failed to parse request parameter index on pagepath = {}", pagePathProperty, e);
											return 0;
										}
									} else {
										return 0;
									}
								}
                            });

							StringBuilder body = new StringBuilder("----------------\nA user submitted a form at ")
								.append(pagePath)
								.append(" with the following information:\n");

							for (String name : parameterNames) {
								if (name.endsWith("_input")) {
									body.append(name.split("_")[1]).append(": ").append(request.getParameter(name)).append("\n");
								}
							}
							//separate the parameters and break-line
							body.append("\n").append("----------------");
							StringBuilder subject = new StringBuilder();
							formName = request.getParameter(TextileUtil.TEXTILE_FORM_PAGE_NAME_PARAM);
							if (formName == null) {
								formName = EMAIL_FORM_NAME;
							}
							subject.append(formName).append(" [submitted from website]");
							if (!mailService.sendMail(null, email, subject.toString(), body.toString())) {
								logger.error("Failed to send mail");
								return new TextStreamResponse(CONTENT_TYPE, ERROR_MESSAGE);
							}
						} else {
							logger.error("Recipient email is not valid: {}", email);
							return new TextStreamResponse(CONTENT_TYPE, ERROR_MESSAGE);
						}
					}

					return new TextStreamResponse(CONTENT_TYPE,
							"Thank you for your submission. The information has been supplied to "
							+ webSiteService.getCurrentCollege().getName() + ".");
				}
			} catch (Exception e) {
				logger.catching(e);
				return new TextStreamResponse(CONTENT_TYPE, ERROR_MESSAGE);
			}

            }
		}

		return new TextStreamResponse(CONTENT_TYPE, ERROR_MESSAGE);

	}

	public String getOptionChecked() {
		return option.equals(field.getDefaultValue()) ? "_checked" : "";
	}

	public String getRequired() {
		return field.isRequired() ? "required" : "";
	}

	public boolean isTextArea() {
		return field.isText() && field.getTextAreaRows() != null;
	}

	public boolean isTextWithMaxLength() {
		return field.isText() && field.getMaxLength() != null;
	}

	public boolean isTextField() {
		return field.isText() && field.getTextAreaRows() == null && field.getMaxLength() == null;
	}
}
