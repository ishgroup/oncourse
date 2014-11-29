package ish.oncourse.portal.usi;

import java.util.Map;

import static ish.oncourse.portal.usi.UsiController.Step;

/**
 * Copyright ish group pty ltd. All rights reserved. http://www.ish.com.au No copying or use of this code is allowed without permission in writing from ish.
 */
public interface StepHandler {
    public Result getPreviousResult();

    public Result getValue();

    public StepHandler handle(Map<String, Value> inputValue);

    Step getNextStep();
}
