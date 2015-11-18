package ish.oncourse.portal.usi;

import java.util.Map;

/**
 * Copyright ish group pty ltd. All rights reserved. http://www.ish.com.au No copying or use of this code is allowed without permission in writing from ish.
 */
public interface StepHandler {
    Result getPreviousResult();

    Result getValue();

    StepHandler handle(Map<String, Value> inputValue);

    Step getNextStep();
}
