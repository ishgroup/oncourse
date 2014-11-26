package ish.oncourse.portal.usi;

import org.apache.commons.lang3.StringUtils;
import org.apache.tapestry5.json.JSONArray;
import org.apache.tapestry5.json.JSONObject;
import org.apache.tapestry5.services.Request;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * Copyright ish group pty ltd. All rights reserved. http://www.ish.com.au No copying or use of this code is allowed without permission in writing from ish.
 */
public class JSONUtils {
    public static JSONObject getJSONValue(Value value) {
        JSONObject jsonValue = new JSONObject();
        jsonValue.put("key", value.getKey());
        if (value.getValue() != null) {
            jsonValue.put("value", value.getValue().toString());
        }

        if (value.getError() != null) {
            jsonValue.put("error", value.getError());
        }

        List<Value> options = value.getOptions();
        if (options.size() > 0) {
            JSONArray jsonOptions = new JSONArray();
            for (Value option : options) {
                jsonOptions.put(getJSONValue(option));
            }
            jsonValue.put("options", jsonOptions);
        }
        jsonValue.put("required", value.isRequired());
        return jsonValue;
    }

    public static JSONArray getJSONValues(Map<String, Value> values) {
        JSONArray jsonValues = new JSONArray();
        for (Map.Entry<String, Value> value : values.entrySet()) {
            JSONObject jsonValue = JSONUtils.getJSONValue(value.getValue());
            jsonValues.put(jsonValue);
        }
        return jsonValues;
    }

    public static Map<String, Value> getValuesFrom(Request request)
    {
        List<String> keys = request.getParameterNames();
        HashMap<String, Value> inputValues = new HashMap<>();
        for (String key : keys) {
            inputValues.put(key, Value.valueOf(key, StringUtils.trimToNull(request.getParameter(key))));
        }
        return inputValues;
    }

}
