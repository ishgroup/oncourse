package ish.oncourse.portal.pages.usi;

import ish.oncourse.model.Contact;
import ish.oncourse.portal.pages.PageNotFound;
import ish.oncourse.portal.usi.UsiController;
import ish.oncourse.portal.usi.ValidationResult;
import ish.oncourse.portal.usi.Value;
import ish.oncourse.services.persistence.ICayenneService;
import ish.oncourse.util.ValidateHandler;
import org.apache.cayenne.ObjectContext;
import org.apache.cayenne.exp.Expression;
import org.apache.cayenne.exp.ExpressionFactory;
import org.apache.cayenne.query.SelectQuery;
import org.apache.commons.lang3.StringUtils;
import org.apache.tapestry5.annotations.OnEvent;
import org.apache.tapestry5.annotations.Persist;
import org.apache.tapestry5.annotations.Property;
import org.apache.tapestry5.ioc.Messages;
import org.apache.tapestry5.ioc.annotations.Inject;
import org.apache.tapestry5.json.JSONArray;
import org.apache.tapestry5.json.JSONObject;
import org.apache.tapestry5.services.Request;
import org.apache.tapestry5.util.TextStreamResponse;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.regex.Pattern;

/**
 * Copyright ish group pty ltd. All rights reserved. http://www.ish.com.au No copying or use of this code is allowed without permission in writing from ish.
 */
public class Usi {

    public static final String ATTR_usiContact = "usiContact";
    public static final Pattern REGEXP_USI_PATH = Pattern.compile("^/usi/(.)*$");

    @Inject
    @Property
    private Request request;

    @Inject
    private Messages messages;

    @Inject
    private ICayenneService cayenneService;

    @Persist
    @Property
    private UsiController usiController;


    public Object onActivate() {

        if (usiController == null) {
            Contact contact = (Contact) request.getAttribute(ATTR_usiContact);
            if (contact == null) {
                return PageNotFound.class;
            }
            usiController = new UsiController();
            usiController.setContact(contact);
            usiController.setMessages(messages);
            usiController.getValidationResult().setMessages(messages);
            usiController.getValidationResult().addError("message-invalidUsi");
            usiController.getValidationResult().addError("message-personalDetailsNotMatch");
            return null;
        } else {
            return null;
        }
    }

    public Object onActivate(String uniqueCode) {
        validateUrl();


        ObjectContext context = cayenneService.newNonReplicatingContext();
        Expression expression = ExpressionFactory.matchAllExp(Contact.GIVEN_NAME_PROPERTY, "James");
        expression = expression.andExp(ExpressionFactory.matchAllExp(Contact.GIVEN_NAME_PROPERTY, "Saum"));
        expression = expression.andExp(ExpressionFactory.matchAllExp(Contact.EMAIL_ADDRESS_PROPERTY, "JamesKSaum@dayrep.com"));

        SelectQuery query = new SelectQuery(Contact.class, expression);
        Contact contact = (Contact) context.performQuery(query).get(0);
        usiController = new UsiController();
        usiController.setContact(contact);
        return null;
    }

    public ValidateHandler getValidateHandler() {
        return usiController.getValidateHandler();
    }

    public ValidationResult getValidationResult() {
        return usiController.getValidationResult();
    }

    public Contact getContact() {
        return usiController.getContact();
    }

    private void validateUrl() {
        //https://skillsoncourse.com.au/portal/usi/uniqueCode?valid=21140101&key=k9_S8uk68W5PoCvq5lSUp70sqQY
//        String key = request.getParameter(UrlUtil.KEY);
//        String valid = request.getParameter(UrlUtil.VALID_UNTIL);
//
//        if (key == null || valid == null) {
//            return PageNotFound.class;
//        }
//        String url = String.format("%s?valid=%s&key=%s", URLUtils.buildURLString(request, request.getPath(), true), valid, key);
//        ObjectContext context = cayenneService.newNonReplicatingContext();
//        Expression expression = ExpressionFactory.matchAllExp(Contact.UNIQUE_CODE_PROPERTY, uniqueCode);
//        SelectQuery<Contact> query = new SelectQuery<>(Contact.class, expression);
//
//        List<Contact> contacts = context.performQuery(query);
//        if (contacts.isEmpty()) {
//            return PageNotFound.class;
//        }
//
//        if (contacts.size() > 1) {
//            throw new IllegalStateException(String.format("% is not unique", uniqueCode));
//        }
//
//        Contact contact = contacts.get(0);
//
//        if (!UrlUtil.validatePortalUsiLink(url, contact.getCollege().getWebServicesSecurityCode(), new Date())) {
//            return PageNotFound.class;
//        }
    }

    @OnEvent(value = "next")
    public Object next() {
        List<String> keys = request.getParameterNames();
        HashMap<String, Value> inputValues = new HashMap<>();
        for (String key : keys) {
            inputValues.put(key, Value.valueOf(key, (Object) StringUtils.trimToNull(request.getParameter(key))));
        }

        Map<String, Value> values = usiController.next(inputValues);
        JSONObject result = getJSONObject(values);
        return new TextStreamResponse("text/json", result.toString());
    }

    private JSONObject getJSONObject(Map<String, Value> values) {
        JSONObject result = new JSONObject();
        JSONArray jsonValues = new JSONArray();
        boolean hasErrors = false;
        for (Map.Entry<String, Value> value : values.entrySet()) {
            JSONObject jsonValue = new JSONObject();
            jsonValue.put("key", value.getValue().getKey());
            if (value.getValue().getValue() != null) {
                jsonValue.put("value", value.getValue().getValue().toString());
            }

            if (value.getValue().getError() != null) {
                jsonValue.put("error", value.getValue().getError());
                hasErrors = true;
            }
            jsonValues.put(jsonValue);
        }
        result.put("values", jsonValues);
        result.put("hasErrors", hasErrors);
        result.put("step", usiController.getStep().name());
        return result;
    }

    @OnEvent(value = "value")
    public Object value() {
        Map<String, Value> values = usiController.getValue();
        JSONObject result = getJSONObject(values);
        return new TextStreamResponse("text/json", result.toString());
    }
}
