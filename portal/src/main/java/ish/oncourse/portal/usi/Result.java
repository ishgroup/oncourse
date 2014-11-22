package ish.oncourse.portal.usi;

import java.util.HashMap;
import java.util.Map;

/**
 * Created by akoiro on 11/21/14.
 */
public class Result {
    private boolean hasErrors = false;
    private Map<String, Value> value = new HashMap<>();

    public boolean hasErrors() {
        return hasErrors;
    }

    public void setHasErrors(boolean hasErrors) {
        this.hasErrors = hasErrors;
    }

    public Map<String, Value> getValue() {
        return value;
    }

    public void setValue(Map<String, Value> value) {
        this.value = value;
    }

    public void addValue(Value value) {
        this.value.put(value.getKey(), value);
    }

}
