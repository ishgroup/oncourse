package ish.oncourse.textile.pages;

import ish.oncourse.model.TextileFormField;
import ish.oncourse.services.encrypt.EncryptionService;
import ish.oncourse.services.mail.MailService;
import ish.oncourse.services.preference.PreferenceController;
import ish.oncourse.services.site.IWebSiteService;
import ish.oncourse.services.textile.TextileUtil;
import org.apache.commons.validator.EmailValidator;
import org.apache.log4j.Logger;
import org.apache.tapestry5.StreamResponse;
import org.apache.tapestry5.annotations.Property;
import org.apache.tapestry5.annotations.SetupRender;
import org.apache.tapestry5.ioc.annotations.Inject;
import org.apache.tapestry5.services.Request;
import org.apache.tapestry5.util.TextStreamResponse;

import java.util.List;

public class TextileForm {

	private final static Logger LOGGER = Logger.getLogger(TextileForm.class);

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
	private MailService mailService;

	@Inject
	private EncryptionService encryptionService;

	@Property
	private String formName;

	@Property
	private String emailValue;

	@SuppressWarnings("all")
	@Property
	private String afterFieldsMarkUp;

	@SuppressWarnings("all")
	@Property
	private boolean shouldSend;

	@Property
	private List<TextileFormField> fields;

	@Property
	private TextileFormField field;

	@SuppressWarnings("all")
	@Property
	private int index;

	@Property
	private String option;

	@SuppressWarnings("all")
	@Property
	private String url;

	@Property
	private Integer formIndex;

	public String getTextileFormId() {
		return formIndex + "TextileForm";
	}

	@SuppressWarnings("unchecked")
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
				LOGGER.error("Problem with processing email", e);
			}
		} else {
			LOGGER.error(String.format("The email for this form is not defined.Host: %s. Path %s",
                    request != null? request.getServerName():"undefined",
                    request != null? request.getPath():"undefined"
                    ));
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

                                StringBuffer body = new StringBuffer("----------------\nA user submitted a form at http://")
                                    .append(request.getServerName()).append("/").append(pagePath)
                                    .append(" with the following information:\n");

                                for (String name : parameterNames) {
                                    if (name.endsWith("_input")) {
                                        body.append(name.split("_")[1]).append(": ").append(request.getParameter(name))
                                                .append("\n");
                                    }
                                }
                                body.append("----------------");
                                if (!mailService.sendMail(null, email, "Submitted via website", body.toString())) {
                                    LOGGER.error("Failed to send mail");
                                    return new TextStreamResponse(CONTENT_TYPE, ERROR_MESSAGE);
                                }
                            } else {
                                LOGGER.error("Recipient email is not valid:" + email);
                                return new TextStreamResponse(CONTENT_TYPE, ERROR_MESSAGE);
                            }
                        }

                        return new TextStreamResponse(CONTENT_TYPE,
                                "Thank you for your submission. The information has been supplied to "
                                + webSiteService.getCurrentCollege().getName() + ".");
                    }
                } catch (Exception e) {
                    LOGGER.error("Failed to send mail with exception " + e.getMessage());
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
