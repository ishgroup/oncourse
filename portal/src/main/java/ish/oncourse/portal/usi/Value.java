package ish.oncourse.portal.usi;

import edu.emory.mathcs.backport.java.util.Arrays;

import java.util.ArrayList;
import java.util.List;

/**
 * Copyright ish group pty ltd. All rights reserved. http://www.ish.com.au No copying or use of this code is allowed without permission in writing from ish.
 */
public class Value {
    private String key;
    private Object value;
    private String error;
    private boolean required = false;
    private boolean hidden = false;
    private List<Value> options = new ArrayList<>();

    public static Value valueOf(String key, Object value, boolean required, boolean hidden) {
        return valueOf(key, value, null, required, hidden);
    }

    public static Value valueOf(String key, Object value, boolean required)
    {
        return Value.valueOf(key, value, null, required, false);
    }

    public static Value valueOf(Value value, String error)
    {
        return Value.valueOf(value.getKey(),
                value.getValue(), error,
                value.isRequired(), error == null && value.isHidden(),
                value.getOptions().toArray(new Value[value.getOptions().size()]));
    }

    public static Value valueOf(String key, Object value, String error, boolean required, boolean hidden, Value... options) {
        Value result = new Value();
        result.key = key;
        result.value = value;
        result.error = error;
        result.required = required;
        result.hidden = hidden;
        result.options.addAll(Arrays.asList(options));
        return result;
    }

    public static Value valueOf(String key, Object value, String error, Value... options)
    {
        return Value.valueOf(key, value, error, false, false, options);
    }

    public static Value valueOf(String key, Object value, String error)
    {
        return Value.valueOf(key, value, error, new Value[]{});
    }

    public static Value valueOf(String key, Object value)
    {
        return Value.valueOf(key, value, null);
    }

    public String getKey() {
        return key;
    }

    public Object getValue() {
        return value;
    }


    public String getError() {
        return error;
    }

    public List<Value> getOptions()
    {
        return options;
    }

    public boolean isRequired() {
        return required;
    }

    public boolean isHidden() {
        return hidden;
    }
}
