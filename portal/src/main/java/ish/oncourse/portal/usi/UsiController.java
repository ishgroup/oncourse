package ish.oncourse.portal.usi;

import ish.oncourse.model.Contact;
import ish.oncourse.model.Course;
import ish.oncourse.model.CourseClass;
import ish.oncourse.model.Enrolment;
import ish.oncourse.portal.services.PortalUSIService;
import ish.oncourse.services.preference.ContactFieldHelper;
import ish.oncourse.services.preference.PreferenceController;
import ish.oncourse.services.reference.ICountryService;
import ish.oncourse.services.reference.ILanguageService;
import ish.oncourse.services.usi.IUSIVerificationService;
import ish.oncourse.util.ValidateHandler;
import org.apache.cayenne.exp.Expression;
import org.apache.cayenne.exp.ExpressionFactory;
import org.apache.tapestry5.ioc.Messages;

import java.util.Collections;
import java.util.Date;
import java.util.List;
import java.util.Map;

/**
 * Copyright ish group pty ltd. All rights reserved. http://www.ish.com.au No copying or use of this code is allowed without permission in writing from ish.
 */
public class UsiController {
    private IUSIVerificationService usiVerificationService;
    private ILanguageService languageService;
    private ICountryService countryService;
    private PortalUSIService portalUSIService;
    private PreferenceController preferenceController;
    private ContactFieldHelper contactFieldHelper;
    private Messages messages;

    private Contact contact;

    private AbstractStepHandler currentHandler;
    private Step step;

    private ValidateHandler validateHandler = new ValidateHandler();

    private ValidationResult validationResult = new ValidationResult();


    public UsiController()
    {
        step = Step.step1;
        currentHandler = new Step1Handler();
        currentHandler.setUsiController(this);
        currentHandler.init();
    }

    public Contact getContact() {
        return contact;
    }

    public void setContact(Contact contact) {
        this.contact = contact;
    }

    public ValidateHandler getValidateHandler() {
        return validateHandler;
    }


    public ValidationResult getValidationResult() {
        return validationResult;
    }

    public Step getStep() {
        return step;
    }

    public Map<String, Value> next(Map<String, Value> inputValue) {
        currentHandler.init();
        StepHandler stepHandler = currentHandler.handle(inputValue);
        if (!stepHandler.hasErrors()) {
            if (step != stepHandler.getNextStep()) {
                step = stepHandler.getNextStep();
                initCurrentHandler();
            }
        }
        return stepHandler.getResult();
    }

    private void initCurrentHandler() {

        try {
            currentHandler = step.handlerClass.newInstance();
            currentHandler.setUsiController(this);
            currentHandler.init();
        } catch (Exception e) {
            throw new IllegalArgumentException(e);
        }
    }

    public Messages getMessages() {
        return messages;
    }

    public void setMessages(Messages messages) {
        this.messages = messages;
    }

    public Map<String, Value> getValue() {
        return currentHandler.getValue();
    }

    public PortalUSIService getPortalUSIService() {
        return portalUSIService;
    }

    public void setPortalUSIService(PortalUSIService portalUSIService) {
        this.portalUSIService = portalUSIService;
    }

    public ILanguageService getLanguageService() {
        return languageService;
    }

    public void setLanguageService(ILanguageService languageService) {
        this.languageService = languageService;
    }

    public ICountryService getCountryService() {
        return countryService;
    }

    public void setCountryService(ICountryService countryService) {
        this.countryService = countryService;
    }

    public PreferenceController getPreferenceController() {
        return preferenceController;
    }

    public void setPreferenceController(PreferenceController preferenceController) {
        this.preferenceController = preferenceController;
    }

    public ContactFieldHelper getContactFieldHelper() {
        return contactFieldHelper;
    }

    public void setContactFieldHelper(ContactFieldHelper contactFieldHelper) {
        this.contactFieldHelper = contactFieldHelper;
    }

    public IUSIVerificationService getUsiVerificationService() {
        return usiVerificationService;
    }

    public void setUsiVerificationService(IUSIVerificationService usiVerificationService) {
        this.usiVerificationService = usiVerificationService;
    }


    public enum Step {
        step1(Step1Handler.class),
        step1Done(Step1DoneHandler.class),
        step1Failed(Step1FailedHandler.class),
        step2(Step2Handler.class),
        step3(Step3Handler.class),
        step3Done(Step3DoneHandler.class),
        done(DoneHandler.class),
        wait(WaitHandler.class);

        private Class<? extends AbstractStepHandler> handlerClass;

        Step(Class<? extends AbstractStepHandler> handlerClass) {
            this.handlerClass = handlerClass;
        }
    }

	public List<Enrolment> getExtraEmrolments() {
		Expression extraEmrolments = ExpressionFactory.greaterExp(Enrolment.COURSE_CLASS_PROPERTY + "." + CourseClass.END_DATE_PROPERTY, new Date()).
			andExp(ExpressionFactory.matchExp(Enrolment.COURSE_CLASS_PROPERTY + "." + CourseClass.COURSE_PROPERTY + "." + Course.IS_VETCOURSE_PROPERTY, true));
		if (contact.getStudent() != null) {
			return extraEmrolments.filterObjects(contact.getStudent().getEnrolments()); 
		} else {
			return Collections.EMPTY_LIST;
		}
	}
}
