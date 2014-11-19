package ish.oncourse.portal.usi;

import java.util.Collections;
import java.util.Map;

import static ish.oncourse.portal.usi.UsiController.Step;
import static ish.oncourse.portal.usi.UsiController.Step.step2;

/**
 * Copyright ish group pty ltd. All rights reserved. http://www.ish.com.au No copying or use of this code is allowed without permission in writing from ish.
 */
public class Step1DoneHandler extends AbstractStepHandler {

    @Override
    public Map<String, Value> getValue() {
        return Collections.emptyMap();
    }

  @Override
    public Step getNextStep() {
        return step2;
    }

    public Step1DoneHandler handle(Map<String, Value> input) {
        this.inputValues = input;
        return this;
    }
}
