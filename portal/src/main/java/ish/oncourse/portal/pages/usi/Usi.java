package ish.oncourse.portal.pages.usi;

import ish.common.types.UsiStatus;
import ish.oncourse.model.College;
import ish.oncourse.model.Contact;
import ish.oncourse.portal.pages.PageNotFound;
import ish.oncourse.portal.usi.*;
import ish.oncourse.services.persistence.ICayenneService;
import ish.oncourse.services.preference.PreferenceControllerFactory;
import ish.oncourse.services.reference.ICountryService;
import ish.oncourse.services.reference.ILanguageService;
import ish.oncourse.services.usi.IUSIVerificationService;
import ish.oncourse.util.URLUtils;
import ish.util.UrlUtil;
import org.apache.cayenne.ObjectContext;
import org.apache.cayenne.query.ObjectSelect;
import org.apache.tapestry5.annotations.Cached;
import org.apache.tapestry5.annotations.OnEvent;
import org.apache.tapestry5.annotations.Property;
import org.apache.tapestry5.ioc.annotations.Inject;
import org.apache.tapestry5.json.JSONArray;
import org.apache.tapestry5.json.JSONObject;
import org.apache.tapestry5.services.Request;
import org.apache.tapestry5.util.TextStreamResponse;

import java.util.Date;
import java.util.List;
import java.util.Map;

/**
 * Copyright ish group pty ltd. All rights reserved. http://www.ish.com.au No copying or use of this code is allowed without permission in writing from ish.
 */
public class Usi {
    @Inject
    @Property
    private Request request;

    @Inject
    private ICayenneService cayenneService;

    @Inject
    private ILanguageService languageService;

    @Inject
    private ICountryService countryService;

    @Inject
    private PreferenceControllerFactory preferenceControllerFactory;

    @Inject
    private IUSIVerificationService usiVerificationService;
    
    private UsiControllerModel usiControllerModel;
    
    @Property
    private Step step;

    private String uniqueCode;
    

    @Cached
    public UsiController getUsiController() {
        return UsiController.valueOf(usiControllerModel, countryService, languageService,
                preferenceControllerFactory.getPreferenceController(usiControllerModel.getContact().getCollege()),
                usiVerificationService);
    }

    public Object onActivate(String uniqueCode) {
        if (request.getParameter("step") != null) {
            step = Step.valueOf(request.getParameter("step"));
        }
        this.uniqueCode = uniqueCode;
        Contact contact = parseUrl();
        if (contact != null) {
            initUsiControllerModel(contact);
            return null;
        } else {
            return PageNotFound.class;
        }
    }

    private void initUsiControllerModel(Contact contact) {
        ObjectContext context = cayenneService.newContext();
        usiControllerModel = UsiControllerModel.valueOf(context.localObject(contact));
        
        if (UsiStatus.VERIFIED.equals(contact.getStudent().getUsiStatus())) {
            usiControllerModel.setStep(Step.done);
        } else if (step != null) {
            usiControllerModel.setStep(step);
        } else {
            step = usiControllerModel.isSkipContactInfo() ? Step.avetmissInfo : Step.contactInfo;
            usiControllerModel.setStep(step);
        }
    }

    public ValidationResult getValidationResult() {
        return getUsiController().getValidationResult();
    }

    public Contact getContact() {
        return getUsiController().getContact();
    }

    private Contact parseUrl() {

        String key = request.getParameter(UrlUtil.KEY);
        String valid = request.getParameter(UrlUtil.VALID_UNTIL);

        if (key == null || valid == null) {
            return null;
        }
        String url = String.format("%s?valid=%s&key=%s", URLUtils.buildURLString(request, request.getPath(), true), valid, key);
        Contact contact = findContact();

        if (!UrlUtil.validatePortalUsiLink(url, contact.getCollege().getWebServicesSecurityCode(), new Date())) {
            return null;
        }
        return contact;
    }
    private Contact findContact() {
        List<Contact> contacts = ObjectSelect.query(Contact.class).where(Contact.UNIQUE_CODE.eq(uniqueCode)).select(cayenneService.newNonReplicatingContext());
        if (contacts.isEmpty()) {
            return null;
        }

        if (contacts.size() > 1) {
            throw new IllegalStateException(String.format("%s is not unique", uniqueCode));
        }

       return contacts.get(0);
    }

    @OnEvent(value = "next")
    public Object next(String step, String uniqueCode) {
        parseRequest(step, uniqueCode);
        Map<String, Value> inputValues = JSONUtils.getValuesFrom(request);
        Result result = getUsiController().next(inputValues);
        return getJSONResult(result);
    }

    @OnEvent(value = "value")
    public Object value(String step, String uniqueCode) {
        parseRequest(step, uniqueCode);
        Result values = getUsiController().getValue();
        return getJSONResult(values);
    }

    /**
     * ]
     * usi.js uses this method
     */
    @OnEvent(value = "verifyUsi")
    public Object verifyUsi(String step, String uniqueCode) {
        parseRequest(step, uniqueCode);
        Map<String, Value> inputValues = JSONUtils.getValuesFrom(request);
        Result result = getUsiController().next(inputValues);
        return getJSONResult(result);
    }

    private void parseRequest(String step, String uniqueCode) {
        this.step = Step.valueOf(step);
        this.uniqueCode = uniqueCode;
        Contact contact = findContact();
        request.setAttribute(College.REQUESTING_COLLEGE_ATTRIBUTE, contact.getCollege().getId());
        initUsiControllerModel(contact);
    }

    private Object getJSONResult(Result result) {
        JSONObject jsoResult = new JSONObject();
        JSONArray jsonArray = JSONUtils.getJSONValues(result.getValue());
        JSONArray errors = JSONUtils.getJSONArray(getUsiController().getValidationResult().getErrors());

        jsoResult.put("values", jsonArray);
        jsoResult.put("hasErrors", result.hasErrors());
        jsoResult.put("step", getUsiController().getStep().name());
        jsoResult.put("errors", errors);

        return new TextStreamResponse("text/json", jsoResult.toString());
    }

}
