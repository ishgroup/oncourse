package ish.oncourse.portal.usi;

import ish.oncourse.model.Contact;
import ish.oncourse.model.Enrolment;
import ish.oncourse.portal.pages.usi.Usi;
import ish.oncourse.services.preference.ContactFieldHelper;
import ish.oncourse.services.preference.PreferenceController;
import ish.oncourse.services.reference.ICountryService;
import ish.oncourse.services.reference.ILanguageService;
import ish.oncourse.services.usi.IUSIVerificationService;
import org.apache.tapestry5.ioc.Messages;
import org.apache.tapestry5.ioc.internal.util.MessagesImpl;

import java.util.List;
import java.util.Map;

/**
 * Copyright ish group pty ltd. All rights reserved. http://www.ish.com.au No copying or use of this code is allowed without permission in writing from ish.
 */
public class UsiController {
    private IUSIVerificationService usiVerificationService;
    private ILanguageService languageService;
    private ICountryService countryService;
    private PreferenceController preferenceController;
    private ContactFieldHelper contactFieldHelper;
    private Messages messages;

    private AbstractStepHandler currentHandler;

    private UsiControllerModel usiControllerModel;


    public Contact getContact() {
        return usiControllerModel.getContact();
    }

    public ValidationResult getValidationResult() {
        return usiControllerModel.getValidationResult();
    }

    public boolean isSkipContactInfo() {
        return usiControllerModel.isSkipContactInfo();
    }

    public Step getStep() {
        return usiControllerModel.getStep();
    }

    public void setStep(Step step) {
        usiControllerModel.setStep(step);
        initCurrentHandler();
    }

    public Result next(Map<String, Value> inputValue) {
        usiControllerModel.getValidationResult().clear();
        currentHandler.init();
        StepHandler stepHandler = currentHandler.handle(inputValue);
        Result result = stepHandler.getValue();
        if (!result.hasErrors()) {
            if (usiControllerModel.getStep() != stepHandler.getNextStep()) {
                usiControllerModel.commitChanges();
                usiControllerModel.setStep(stepHandler.getNextStep());
                initCurrentHandler();
                currentHandler.setPreviousResult(result);
            }
        }
        return result;
    }

    private void initCurrentHandler() {

        try {
            currentHandler = usiControllerModel.getStep().getHandlerClass().newInstance();
            currentHandler.setUsiController(this);
            currentHandler.init();
        } catch (Exception e) {
            throw new IllegalArgumentException(e);
        }
    }

    public Messages getMessages() {
        return messages;
    }

    public Result getValue() {
        return currentHandler.getValue();
    }

    public ILanguageService getLanguageService() {
        return languageService;
    }

    public ICountryService getCountryService() {
        return countryService;
    }

    public PreferenceController getPreferenceController() {
        return preferenceController;
    }

    public ContactFieldHelper getContactFieldHelper() {
        return contactFieldHelper;
    }

    public IUSIVerificationService getUsiVerificationService() {
        return usiVerificationService;
    }

    public List<Enrolment> getVETEnrolments() {
        return usiControllerModel.getVETEnrolments();
    }

    public static UsiController valueOf(UsiControllerModel model,
                                        ICountryService countryService,
                                        ILanguageService languageService,
                                        PreferenceController preferenceController,
                                        IUSIVerificationService usiVerificationService) {
        UsiController usiController = new UsiController();
        usiController.usiControllerModel = model;

        usiController.countryService = countryService;
        usiController.languageService = languageService;

        usiController.preferenceController = preferenceController;
        usiController.contactFieldHelper = new ContactFieldHelper(usiController.getPreferenceController());

        usiController.usiVerificationService = usiVerificationService;
        usiController.messages = MessagesImpl.forClass(Usi.class);

        usiController.initCurrentHandler();
        return usiController;
    }
}
