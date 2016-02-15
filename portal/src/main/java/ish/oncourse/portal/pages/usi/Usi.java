package ish.oncourse.portal.pages.usi;

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
import org.apache.tapestry5.annotations.Persist;
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

    @Persist
    private UsiControllerModel usiControllerModel;


    public Object onActivate() {

        if (usiControllerModel == null) {
            return PageNotFound.class;
        } else {
            return null;
        }
    }

    @Cached
    public UsiController getUsiController() {
        return UsiController.valueOf(usiControllerModel, countryService, languageService,
                preferenceControllerFactory.getPreferenceController(usiControllerModel.getContact().getCollege()),
                usiVerificationService);
    }

    public Object onActivate(String uniqueCode) {
        if (usiControllerModel == null || !usiControllerModel.getContact().getUniqueCode().equals(uniqueCode)) {
            Contact contact = parseUrl(uniqueCode);
            if (contact != null) {
                initUsiControllerModel(contact);
                return null;
            } else {
                return PageNotFound.class;
            }
        } else {
            return null;
        }
    }

    private void initUsiControllerModel(Contact contact) {
        ObjectContext context = cayenneService.newContext();
        usiControllerModel = UsiControllerModel.valueOf(context.localObject(contact));
	    usiControllerModel.setStep(skipContactInfo(contact) ? Step.avetmissInfo : Step.contactInfo);

        // we need put college id to the session for WebSiteService.getCurrentCollege() method
        request.getSession(false).setAttribute(College.REQUESTING_COLLEGE_ATTRIBUTE, contact.getCollege().getId());
    }

	public boolean skipContactInfo(Contact contact) {
		return contact.getStreet() != null &&
				contact.getSuburb() != null &&
				contact.getPostcode() != null &&
				contact.getDateOfBirth() != null &&
				contact.getStudent().getCountryOfBirth() != null &&
				contact.getStudent().getTownOfBirth() != null &&
				contact.getIsMale() != null && contact.getState() != null &&
				contact.getCountry() != null &&
				contact.getMobilePhoneNumber() != null &&
				contact.getHomePhoneNumber() != null &&
				contact.getBusinessPhoneNumber() != null && contact.getStudent().getSpecialNeeds() != null;
	}


    public ValidationResult getValidationResult() {
        return getUsiController().getValidationResult();
    }

    public Contact getContact() {
        return getUsiController().getContact();
    }

    private Contact parseUrl(String uniqueCode) {

        String key = request.getParameter(UrlUtil.KEY);
        String valid = request.getParameter(UrlUtil.VALID_UNTIL);

        if (key == null || valid == null) {
            return null;
        }
        String url = String.format("%s?valid=%s&key=%s", URLUtils.buildURLString(request, request.getPath(), true), valid, key);
        List<Contact> contacts = ObjectSelect.query(Contact.class).where(Contact.UNIQUE_CODE.eq(uniqueCode)).select(cayenneService.newNonReplicatingContext());
        if (contacts.isEmpty()) {
            return null;
        }

        if (contacts.size() > 1) {
            throw new IllegalStateException(String.format("%s is not unique", uniqueCode));
        }

        Contact contact = contacts.get(0);

	    if (false && !UrlUtil.validatePortalUsiLink(url, contact.getCollege().getWebServicesSecurityCode(), new Date())) {
		    return null;
        }
        return contact;
    }

    @OnEvent(value = "next")
    public Object next() {
        Map<String, Value> inputValues = JSONUtils.getValuesFrom(request);

        Result result = getUsiController().next(inputValues);
        return getJSONResult(result);
    }

    @OnEvent(value = "value")
    public Object value() {
        Result values = getUsiController().getValue();

        return getJSONResult(values);
    }

    /**
     * ]
     * usi.js uses this method
     */
    @OnEvent(value = "verifyUsi")
    public Object verifyUsi() {
        Map<String, Value> inputValues = JSONUtils.getValuesFrom(request);
        Result values = getUsiController().next(inputValues);
        return getJSONResult(values);
    }

    private Object getJSONResult(Result result) {
        JSONObject jsoResult = new JSONObject();
        JSONArray jsonArray = JSONUtils.getJSONValues(result.getValue());
        jsoResult.put("values", jsonArray);
        jsoResult.put("hasErrors", result.hasErrors());
        jsoResult.put("step", getUsiController().getStep().name());
        return new TextStreamResponse("text/json", jsoResult.toString());
    }

}
