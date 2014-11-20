package ish.oncourse.portal.usi;

import org.apache.commons.beanutils.BeanUtils;

import java.util.Collections;
import java.util.Map;

/**
 * Copyright ish group pty ltd. All rights reserved. http://www.ish.com.au No copying or use of this code is allowed without permission in writing from ish.
 */
public abstract class AbstractStepHandler implements StepHandler {
    private UsiController usiController;
    protected Map<String, Value> result = Collections.emptyMap();
    protected boolean hasErrors = false;
    protected Map<String, Value> inputValues;

    public UsiController getUsiController() {
        return usiController;
    }

    public void setUsiController(UsiController usiController) {
        this.usiController = usiController;
    }

    @Override
    public Map<String, Value> getResult() {
        return result;
    }

    protected <T> void parseRequiredValue(String key, Value inputValue, T entity) {
        if (inputValue == null || inputValue.getValue() == null) {
            result.put(key, Value.valueOf(key, null, getUsiController().getMessages().format("message-fieldRequired")));
            hasErrors = true;
        } else {
            try {
                Object value = inputValue.getValue();
                BeanUtils.setProperty(entity, key, value);
                result.put(key, Value.valueOf(key, inputValue.getValue()));
            } catch (Exception e) {
                throw new IllegalArgumentException(e);
            }
        }
    }

    @Override
    public boolean hasErrors() {
        return hasErrors;
    }
}
