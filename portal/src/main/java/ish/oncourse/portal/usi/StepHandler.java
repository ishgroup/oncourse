package ish.oncourse.portal.usi;

import java.util.Map;

import static ish.oncourse.portal.usi.UsiController.Step;

/**
 * Copyright ish group pty ltd. All rights reserved. http://www.ish.com.au No copying or use of this code is allowed without permission in writing from ish.
 */
public interface StepHandler {
    public Map<String, Value> getResult();

    public Map<String, Value> getValue();

    public StepHandler handle(Map<String, Value> inputValue);

    boolean hasErrors();

    Step getNextStep();
}
