package ish.oncourse.portal.usi;

import java.util.Collections;
import java.util.Map;

import static ish.oncourse.portal.usi.UsiController.Step;
import static ish.oncourse.portal.usi.UsiController.Step.step1Done;
import static ish.oncourse.portal.usi.UsiController.Step.wait;

/**
 * Copyright ish group pty ltd. All rights reserved. http://www.ish.com.au No copying or use of this code is allowed without permission in writing from ish.
 */
public class WaitHandler extends AbstractStepHandler {

    private int count = 0;

    @Override
    public Map<String, Value> getValue() {
        return Collections.emptyMap();
    }

    @Override
    public Step getNextStep() {
        if (count < 2) return wait;
        else return step1Done;
    }

    public WaitHandler handle(Map<String, Value> input) {
        count++;
        result = Collections.emptyMap();
        return this;
    }
}
