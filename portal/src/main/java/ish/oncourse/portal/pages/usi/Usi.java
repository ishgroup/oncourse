package ish.oncourse.portal.pages.usi;

import ish.oncourse.model.College;
import ish.oncourse.model.Contact;
import ish.oncourse.portal.pages.PageNotFound;
import ish.oncourse.portal.usi.*;
import ish.oncourse.services.persistence.ICayenneService;
import ish.oncourse.services.preference.ContactFieldHelper;
import ish.oncourse.services.preference.PreferenceControllerFactory;
import ish.oncourse.services.reference.ICountryService;
import ish.oncourse.services.reference.ILanguageService;
import ish.oncourse.services.usi.IUSIVerificationService;
import ish.oncourse.util.URLUtils;
import ish.util.UrlUtil;
import org.apache.cayenne.ObjectContext;
import org.apache.cayenne.exp.Expression;
import org.apache.cayenne.exp.ExpressionFactory;
import org.apache.cayenne.query.SelectQuery;
import org.apache.tapestry5.annotations.OnEvent;
import org.apache.tapestry5.annotations.Persist;
import org.apache.tapestry5.annotations.Property;
import org.apache.tapestry5.ioc.Messages;
import org.apache.tapestry5.ioc.annotations.Inject;
import org.apache.tapestry5.json.JSONArray;
import org.apache.tapestry5.json.JSONObject;
import org.apache.tapestry5.services.Request;
import org.apache.tapestry5.util.TextStreamResponse;

import java.util.Date;
import java.util.List;
import java.util.Map;

import static ish.oncourse.services.preference.PreferenceController.ContactFiledsSet.enrolment;

/**
 * Copyright ish group pty ltd. All rights reserved. http://www.ish.com.au No copying or use of this code is allowed without permission in writing from ish.
 */
public class Usi {
    @Inject
    @Property
    private Request request;

    @Inject
    private Messages messages;

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
    @Property
    private UsiController usiController;


    public Object onActivate() {

        if (usiController == null) {
            return PageNotFound.class;
        } else {
            return null;
        }
    }

    public Object onActivate(String uniqueCode) {
        if (usiController == null || !usiController.getContact().getUniqueCode().equals(uniqueCode)) {
            Contact contact = parseUrl(uniqueCode);
            if (contact != null) {
                initUsiController(contact);
                return null;
            } else {
                return PageNotFound.class;
            }
        } else {
            return null;
        }
    }

    private void initUsiController(Contact contact) {
        ObjectContext context = cayenneService.newContext();
        usiController = new UsiController();
        usiController.setContact(context.localObject(contact));
        usiController.setCountryService(countryService);
        usiController.setLanguageService(languageService);
        usiController.setPreferenceController(preferenceControllerFactory.getPreferenceController(contact.getCollege()));
        usiController.setContactFieldHelper(new ContactFieldHelper(usiController.getPreferenceController(), enrolment));
        usiController.setUsiVerificationService(usiVerificationService);
        usiController.setMessages(messages);
        usiController.getValidationResult().setMessages(messages);

        // we need put college id to the session for WebSiteService.getCurrentCollege() method
        request.getSession(false).setAttribute(College.REQUESTING_COLLEGE_ATTRIBUTE, contact.getCollege().getId());
    }


    public ValidationResult getValidationResult() {
        return usiController.getValidationResult();
    }

    public Contact getContact() {
        return usiController.getContact();
    }

    private Contact parseUrl(String uniqueCode) {

        String key = request.getParameter(UrlUtil.KEY);
        String valid = request.getParameter(UrlUtil.VALID_UNTIL);

        if (key == null || valid == null) {
            return null;
        }
        String url = String.format("%s?valid=%s&key=%s", URLUtils.buildURLString(request, request.getPath(), true), valid, key);
        ObjectContext context = cayenneService.newNonReplicatingContext();
        Expression expression = ExpressionFactory.matchAllExp(Contact.UNIQUE_CODE_PROPERTY, uniqueCode);
        SelectQuery query = new SelectQuery(Contact.class, expression);

        List<Contact> contacts = context.performQuery(query);
        if (contacts.isEmpty()) {
            return null;
        }

        if (contacts.size() > 1) {
            throw new IllegalStateException(String.format("% is not unique", uniqueCode));
        }

        Contact contact = contacts.get(0);

        if (!UrlUtil.validatePortalUsiLink(url, contact.getCollege().getWebServicesSecurityCode(), new Date())) {
            return null;
        }
        return contact;
    }

    @OnEvent(value = "next")
    public Object next() {
        Map<String, Value> inputValues = JSONUtils.getValuesFrom(request);

        Result result = usiController.next(inputValues);
        JSONObject jsoResult = new JSONObject();
        JSONArray jsonArray = JSONUtils.getJSONValues(result.getValue());
        jsoResult.put("values", jsonArray);
        jsoResult.put("hasErrors", result.hasErrors());
        jsoResult.put("step", usiController.getStep().name());
        return new TextStreamResponse("text/json", jsoResult.toString());
    }

    @OnEvent(value = "value")
    public Object value() {
        Map<String, Value> values = usiController.getValue();
        JSONObject jsonResult = new JSONObject();
        JSONArray jsonArray = JSONUtils.getJSONValues(values);
        jsonResult.put("values", jsonArray);
        jsonResult.put("hasErrors", false);
        jsonResult.put("step", usiController.getStep().name());

        return new TextStreamResponse("text/json", jsonResult.toString());
    }
}
